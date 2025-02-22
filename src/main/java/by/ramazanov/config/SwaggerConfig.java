package by.ramazanov.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
@Slf4j
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        log.info("Создание конфигурации OpenAPI для discover cluster API");

        OpenAPI openAPI = new OpenAPI()
                .servers(Collections.singletonList(new Server().url("https://www.amazonaws.cn/en/")))
                .info(createApiInfo());

        log.info("Конфигурация OpenAPI успешно создана: {}", openAPI);
        return openAPI;
    }

    private Info createApiInfo() {
        log.info("Создание информации об API");

        Info apiInfo = new Info()
                .title("Discover cluster API")
                .version("1.0")
                .description("API подключения к AWS");

        log.info("Информация об API успешно создана: {}", apiInfo);
        return apiInfo;
    }
}
