package com.kyun.example.app;

import com.kyun.example.service.UserService;

public class Main {
    public static void main(String[] args) {
        UserService service = new UserService();
        service.sayHello();

        String version = Main.class.getPackage().getImplementationVersion();
        System.out.println("Implementation-Version: " + (version == null ? "(none)" : version));
    }
}
