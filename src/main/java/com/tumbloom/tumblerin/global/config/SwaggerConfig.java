package com.tumbloom.tumblerin.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .title("TUMBLERIN")
                        .version("1.0.0")
                        .description("[멋쟁이사자처럼 중앙해커톤 텀블룸팀 api 문서입니다.")
                        .contact(new Contact()
                                .url("https://github.com/Likelion-at-SMWU-13th/Tumbloom-BE")
                                .name("Likelionuniv.sookmyung Tumbloom"))
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .servers(List.of(
                        new Server().url("http://localhost:8080") //로컬 서버 추후 배포시 배포서버도
                ));
    }
}
