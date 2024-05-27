package com.example.thenewboston

import org.springframework.web.bind.annotation.*
@RestController
@RequestMapping("api/hello")
class HelloWordController {

    @GetMapping("")
    fun helloWorld(): String = "Hello, this is a REST Endpoint"
}