package com.example.demosecurity.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api( description = "simple example")
@RestController
@RequestMapping("/simpleExample")
public class SwaggerController {
    @ApiOperation(value = "simple string example")
    @GetMapping("example")
    public String b(){
        return "testy-food";
    }
}
