package by.ramazanov.connector;


import lombok.Getter;
import lombok.Setter;

// Класс для десериализации JSON
@Setter
@Getter
public class Credentials {
    private String accessKeyId;
    private String secretAccessKey;

}