package by.ramazanov;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.EnableMBeanExport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Основной класс приложения.
 */
@Slf4j // Включает логирование с помощью Slf4j
@Tag(name = "Discover Cluster API") // Документация Swagger: тег для API
@SpringBootApplication // Маркирует класс как основной для Spring Boot приложения
@EnableScheduling // Включает планирование задач в фоне (например, через @Scheduled)
@EnableAsync // Включает асинхронное выполнение методов (через @Async)
@EnableMBeanExport // Экспортирует бины как MBeans для мониторинга и управления через JMX
@EnableCaching // Включает кэширование данных в приложении (через @Cacheable)
public class DiscoverClusterApplication {

    /**
     * Точка входа приложения.
     *
     * @param args Аргументы командной строки.
     */
    public static void main(String[] args) {
        log.info("Запуск Discover Cluster Application...");
        SpringApplication.run(DiscoverClusterApplication.class, args);
    }

}

