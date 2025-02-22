package by.ramazanov.connector;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.google.gson.Gson;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;

import java.io.FileReader;
import java.io.IOException;

/**
 * Custom implementation of AWSCredentialsProvider that provides AWS credentials.
 */
@Slf4j
@ApiModel(description = "Custom AWS Credentials Provider")
public class CustomAWSCredentialsProvider implements AWSCredentialsProvider {

    @ApiModelProperty(value = "Custom AWS Credentials", required = true)
    private CustomAWSCredentials credentials;

    /**
     * Constructs an instance of CustomAWSCredentialsProvider with the specified credentials.
     *
     * @param credentials the custom AWS credentials
     */
    public CustomAWSCredentialsProvider(CustomAWSCredentials credentials) {
        this.credentials = credentials;
        log.debug("CustomAWSCredentialsProvider initialized with credentials: {}", credentials.getAWSAccessKeyId());
    }

    @Override
    public AWSCredentials getCredentials() {
        log.debug("Retrieving AWS credentials");
        return credentials;
    }

    @Override
    public void refresh() {
        log.debug("Refreshing AWS credentials");

        try {

            this.credentials = loadNewCredentials();
            log.info("AWS credentials refreshed successfully");

        } catch (Exception e) {
            log.error("Error refreshing AWS credentials", e);
        }
    }

    private CustomAWSCredentials loadNewCredentials() throws Exception {
        String accessKeyId = readAccessKeyIdFromSource(); // Реализуйте чтение ключа доступа
        String secretAccessKey = readSecretAccessKeyFromSource(); // Реализуйте чтение секретного ключа

        return new CustomAWSCredentials(accessKeyId, secretAccessKey);
    }

    private String readAccessKeyIdFromSource() throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("path/to/credentials.json")) {
            Credentials credentials = gson.fromJson(reader, Credentials.class);
            return credentials.getAccessKeyId();
        }
    }

    private String readSecretAccessKeyFromSource() throws IOException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("path/to/credentials.json")) {
            Credentials credentials = gson.fromJson(reader, Credentials.class);
            return credentials.getSecretAccessKey();
        }
    }
}