package com.kyun.example.service;

import com.kyun.example.repository.UserRepository;

public class UserService {
    private UserRepository repo = new UserRepository();
    
    public void sayHello() {
        System.out.println(repo.findName());
    }
}
