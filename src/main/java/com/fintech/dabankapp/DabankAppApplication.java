package com.fintech.dabankapp;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
        info = @Info(
                title = "DaBank Application",
                description = "Backend Rest APIs for DaBank App",
                version = "v1.0",
                contact = @Contact(
                        name = "Chibueze Arisa",
                        email = "arisachibueze@gmail.com",
                        url = "https://github.com/ch1bueze/dabank"
                ),
                license = @License(
                        name = "DaBank App",
                        url = "https://github.com/ch1bueze/dabank"
                )
        ),
        externalDocs = @ExternalDocumentation(
                description = "The DaBank fintech application",
                url = "https://github.com/ch1bueze/dabank"
        )
)
public class DabankAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(DabankAppApplication.class, args);
    }

}
