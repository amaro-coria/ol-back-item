package com.ol.demo;

import com.ol.demo.persistence.entity.Item;
import com.ol.demo.persistence.repository.ItemRepository;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

@SpringBootApplication
@EnableSwagger2
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    ApplicationRunner init(ItemRepository itemRepository){
        return args -> {
            Item item1 = new Item("COKE");
            Item item2 = new Item("PEPSI");
            Item item3 = new Item("SPRITE");
            itemRepository.save(item1);
            itemRepository.save(item2);
            itemRepository.save(item3);
        };
    }

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.ol.demo.controller"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        return new ApiInfo(
                "Work Demo",
                "Basic api for work-demo.",
                "1.0",
                "Terms of service",
                new Contact("Jorge Amaro", "https://github.com/amaro-coria", "amaro.coria@gmail.com"),
                "License of API", "API license URL", Collections.emptyList());
    }

}
