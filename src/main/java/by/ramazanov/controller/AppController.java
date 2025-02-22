package by.ramazanov.controller;

import by.ramazanov.connector.AwsConnector;
import by.ramazanov.entity.App;
import io.kubernetes.client.extended.kubectl.exception.KubectlException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

/**
 * Контроллер для работы с приложениями в кластере.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
public class AppController {

    private final AwsConnector awsConnector;

    @QueryMapping
    Iterable<App> discoverCluster() throws IOException, KubectlException {
        log.info("Запрос на обнаружение кластера");
        try {
            return awsConnector.describeCluster("customConfig");
        } catch (IOException | KubectlException e) {
            log.error("Ошибка при обнаружении кластера", e);
            throw e;
        }
    }

    @QueryMapping
    Optional<App> appByName(@Argument String name) throws IOException, KubectlException {
        log.info("Запрос на получение приложения по имени {}", name);
        try {
            awsConnector.connectToCluster();
            return awsConnector.describeCluster("customConfig", name);
        } catch (IOException | KubectlException e) {
            log.error("Ошибка при получении приложения по имени {}", name, e);
            throw e;
        }
    }

    @MutationMapping
    String connect(@Argument String accessKey, @Argument String secretKey,
                   @Argument String clusterName, @Argument String region) throws IOException {
        log.info("Запрос на подключение к кластеру {} в регионе {}", clusterName, region);
        try {
            return awsConnector.connectToCluster(accessKey, secretKey, clusterName, region);
        } catch (IOException e) {
            log.error("Ошибка при подключении к кластеру {} в регионе {}", clusterName, region, e);
            throw e;
        }
    }
}
