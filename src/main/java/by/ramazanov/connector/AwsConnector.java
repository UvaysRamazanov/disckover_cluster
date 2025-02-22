package by.ramazanov.connector;

import by.ramazanov.entity.App;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.eks.AmazonEKS;
import com.amazonaws.services.eks.AmazonEKSClientBuilder;
import com.amazonaws.services.eks.model.DescribeClusterRequest;
import com.amazonaws.services.eks.model.DescribeClusterResult;
import io.kubernetes.client.extended.kubectl.Kubectl;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.models.*;
import io.kubernetes.client.util.Config;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Data
public class AwsConnector {
    private String accessKey;
    private String secretKey;
    private String clusterName;
    private String region;
    private String describeCluster;
    private final Map<String, String> kubeConfig = new HashMap<>();
    private final List<App> apps = new ArrayList<>();


    public String connectToCluster() throws IOException {
        AWSCredentialsProvider provider = new CustomAWSCredentialsProvider(
                new CustomAWSCredentials(accessKey,secretKey));
        AmazonEKS eks = AmazonEKSClientBuilder.standard()
                .withRegion(region)
                .withCredentials(provider)
                .build();
        DescribeClusterRequest request = new DescribeClusterRequest();
        DescribeClusterResult result = eks.describeCluster(request.withName(clusterName));
        describeCluster = result.toString();
        setMap();
        return "Connected successfully to cluster " + clusterName + " at " + kubeConfig.get("server");
    }

    public String connectToCluster(String accessKey, String secretKey, String clusterName, String region)
            throws IOException {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.clusterName = clusterName;
        this.region = region;
        return connectToCluster();
    }

    private void setMap() throws IOException {
        kubeConfig.clear();
        String arn = describeCluster.substring(describeCluster.indexOf("Arn: "));
        kubeConfig.put("user", arn.substring(5, arn.indexOf(',')));
        String endpoint = arn.substring(arn.indexOf("Endpoint: "));
        kubeConfig.put("server", endpoint.substring(10, endpoint.indexOf(',')));
        String data = endpoint.substring(endpoint.indexOf("CertificateAuthority: {Data: "));
        kubeConfig.put("certificate-authority", data.substring(29, data.indexOf("},")));
        createConfigFile();
    }

    private void createConfigFile() throws IOException {
        FileWriter writer = new FileWriter("customConfig");
        StringBuilder builder = new StringBuilder();
        builder.append("""
                apiVersion: v1
                clusters:
                - cluster:
                    certificate-authority-data:\s""").append(kubeConfig.get("certificate-authority")).append('\n');
        builder.append("    server: ").append(kubeConfig.get("server")).append('\n');
        String user = kubeConfig.get("user");
        builder.append("  name: ").append(user).append('\n');
        builder.append("""
                contexts:
                - context:
                    cluster:\s""").append(user).append('\n');
        builder.append("    user: ").append(user).append('\n');
        builder.append("  name: ").append(user).append('\n');
        builder.append("current-context: ").append(user).append('\n');
        builder.append("""
                kind: Config
                preferences: {}
                users:
                - name:\s""").append(user).append('\n');
        builder.append("  user:").append('\n');
        builder.append("    exec:").append('\n');
        builder.append("      apiVersion: client.authentication.k8s.io/v1beta1").append('\n');
        builder.append("      args:").append('\n');
        builder.append("      - --region").append('\n');
        builder.append("      - ").append(region).append('\n');
        builder.append("      - eks").append('\n');
        builder.append("      - get-token").append('\n');
        builder.append("      - --cluster-name").append('\n');
        builder.append("      - ").append(clusterName).append('\n');
        builder.append("      - --output").append('\n');
        builder.append("      - json").append('\n');
        builder.append("      command: aws").append('\n');
        writer.write(builder.toString());
        writer.close();
    }

    public List<App> describeCluster(String config) throws IOException, KubectlException {
        ApiClient client = Config.fromConfig(config);
        client.setVerifyingSsl(false);
        Configuration.setDefaultApiClient(client);

        List<V1Deployment> deploymentList = Kubectl.get(V1Deployment.class).execute();
        List<V1Ingress> ingressList = Kubectl.get(V1Ingress.class).execute();
        List<V1Pod> podList = Kubectl.get(V1Pod.class).execute();
        List<V1Secret> secretList = Kubectl.get(V1Secret.class).execute();
        List<V1ConfigMap> configMapList = Kubectl.get(V1ConfigMap.class).execute();

        apps.clear();

        for (V1Deployment item : deploymentList) {
            Map<String, String> labels = Objects.requireNonNull(item.getMetadata()).getLabels();
            if (labels!=null) {
                String appName = labels.get("app");
                if (appName != null) {
                    String deployment = item.getMetadata().getName();
                    Optional<App> appItem = (apps.stream().filter(a -> a.getName().equals(appName)).findFirst());
                    if (appItem.isPresent()) {
                        appItem.get().getDeployments().add(deployment);
                    } else {
                        App app = new App();
                        app.setName(appName);
                        app.getDeployments().add(deployment);
                        apps.add(app);
                    }
                }
            }
        }

        for (V1Ingress item : ingressList) {
            Map<String, String> labels = Objects.requireNonNull(item.getMetadata()).getLabels();
            if (labels!=null) {
                String appName = labels.get("app");
                if (appName != null) {
                    String ingress = item.getMetadata().getName();
                    Optional<App> appItem = (apps.stream().filter(a -> a.getName().equals(appName)).findFirst());
                    if (appItem.isPresent()) {
                        appItem.get().getIngresses().add(ingress);
                    } else {
                        App app = new App();
                        app.setName(appName);
                        app.getIngresses().add(ingress);
                        apps.add(app);
                    }
                }
            }
        }

        for (V1Pod item : podList) {
            Map<String, String> labels = Objects.requireNonNull(item.getMetadata()).getLabels();
            if (labels!=null) {
                String appName = labels.get("app");
                if (appName != null) {
                    String pod = item.getMetadata().getName();
                    Optional<App> appItem = (apps.stream().filter(a -> a.getName().equals(appName)).findFirst());
                    if (appItem.isPresent()) {
                        appItem.get().getPods().add(pod);
                    } else {
                        App app = new App();
                        app.setName(appName);
                        app.getPods().add(pod);
                        apps.add(app);
                    }
                }
            }
        }

        for (V1Secret item : secretList) {
            Map<String, String> labels = Objects.requireNonNull(item.getMetadata()).getLabels();
            if (labels!=null) {
                String appName = labels.get("app");
                if (appName != null) {
                    String secret = item.getMetadata().getName();
                    Optional<App> appItem = (apps.stream().filter(a -> a.getName().equals(appName)).findFirst());
                    if (appItem.isPresent()) {
                        appItem.get().getSecrets().add(secret);
                    } else {
                        App app = new App();
                        app.setName(appName);
                        app.getSecrets().add(secret);
                        apps.add(app);
                    }
                }
            }
        }

        for (V1ConfigMap item : configMapList) {
            Map<String, String> labels = Objects.requireNonNull(item.getMetadata()).getLabels();
            if (labels!=null) {
                String appName = labels.get("app");
                if (appName != null) {
                    String configMap = item.getMetadata().getName();
                    Optional<App> appItem = (apps.stream().filter(a -> a.getName().equals(appName)).findFirst());
                    if (appItem.isPresent()) {
                        appItem.get().getConfigMaps().add(configMap);
                    } else {
                        App app = new App();
                        app.setName(appName);
                        app.getConfigMaps().add(configMap);
                        apps.add(app);
                    }
                }
            }
        }

        return apps;
    }

    public Optional<App> describeCluster(String customConfig, String name)
            throws IOException, KubectlException {
        describeCluster(customConfig);
        return apps.stream().filter(a -> a.getName().equals(name)).findFirst();
    }


}