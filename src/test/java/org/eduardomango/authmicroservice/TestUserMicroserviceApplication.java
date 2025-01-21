package org.eduardomango.authmicroservice;

import org.springframework.boot.SpringApplication;

public class TestUserMicroserviceApplication {

    public static void main(String[] args) {
        SpringApplication.from(UserMicroserviceApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
