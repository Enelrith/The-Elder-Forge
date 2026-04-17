package com.enelrith.theelderforge;

import org.springframework.boot.SpringApplication;

public class TestTheelderforgeApplication {

    public static void main(String[] args) {
        SpringApplication.from(TheelderforgeApplication::main).with(TestcontainersConfiguration.class).run(args);
    }

}
