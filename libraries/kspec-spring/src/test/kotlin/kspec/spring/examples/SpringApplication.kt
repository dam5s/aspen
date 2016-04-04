package kspec.spring.examples

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@SpringBootApplication
open class ExampleApplication {

    companion object {
        @JvmStatic
        fun main(vararg args: String) {
            SpringApplication.run(ExampleApplication::class.java, *args)
        }
    }
}

@RestController
class HelloController {

    @RequestMapping("/hello")
    fun sayHello() = mapOf("hello" to "world")
}
