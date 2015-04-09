package edu.cmpe277.teamgoat.photoapp.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class TestController {
    @RequestMapping("/test")
    public Map<String, Object> greeting() {
        return new HashMap<>();
    }
}