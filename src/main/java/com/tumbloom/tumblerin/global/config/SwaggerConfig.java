package com.tumbloom.tumblerin.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {

        // Bearer Token 인증 방식 설정
        io.swagger.v3.oas.models.security.SecurityScheme bearerAuth = new io.swagger.v3.oas.models.security.SecurityScheme()
                .type(io.swagger.v3.oas.models.security.SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER)
                .name("Authorization");

        io.swagger.v3.oas.models.security.SecurityRequirement securityRequirement = new SecurityRequirement().addList("BearerAuth");

        return new OpenAPI()
                .info(new Info()
                        .title("TUMBLERIN")
                        .version("1.0.0")
                        .description("[멋쟁이사자처럼 중앙해커톤 텀블룸팀 api 문서입니다.")
                        .contact(new Contact()
                                .url("https://github.com/Likelion-at-SMWU-13th/Tumbloom-BE")
                                .name("Likelionuniv.sookmyung Tumbloom"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth", bearerAuth))
                .addSecurityItem(securityRequirement)
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Local Server"),
                        new Server().url("http://3.39.187.47:8080").description("EC2 배포 서버")
                ));
    }
}
