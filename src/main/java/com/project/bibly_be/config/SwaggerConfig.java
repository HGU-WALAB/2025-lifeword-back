package com.project.bibly_be.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI() {
        Server server = new Server();

        server.setUrl("/naimkim_1");

        List<Server> servers = new ArrayList<>();
        servers.add(server);

        return new OpenAPI()
                .servers(servers)
                .info(new Info()
                        .title("Bibly API")
                        .description("Bibly REST API 문서")
                        .version("v1.0.0"));
    }
}