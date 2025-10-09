package com.dicsar;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // 🔹 Habilita las tareas automáticas (como revisar vencimientos)
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        System.out.println("✅ Dicsar Backend iniciado correctamente con tareas automáticas.");
    }
}
