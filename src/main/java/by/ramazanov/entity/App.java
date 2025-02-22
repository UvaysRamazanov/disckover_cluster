package by.ramazanov.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Класс для представления приложения с его компонентами.
 */
@Component
@Data
@Slf4j
public class App {

    @Schema(description = "Имя приложения")
    private String name = "";

    @Schema(description = "Список развертываний")
    private List<String> deployments = new ArrayList<>();

    @Schema(description = "Список подов")
    private List<String> pods = new ArrayList<>();

    @Schema(description = "Список конфигурационных карт")
    private List<String> configMaps = new ArrayList<>();

    @Schema(description = "Список секретов")
    private List<String> secrets = new ArrayList<>();

    @Schema(description = "Список ингрессов")
    private List<String> ingresses = new ArrayList<>();
}
