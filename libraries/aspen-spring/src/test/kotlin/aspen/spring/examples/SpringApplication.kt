package aspen.spring.examples

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Bean
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

    @Bean
    open fun myMessage() = "world"
}

@RestController
class HelloController {

    private val myMessage: String

    @Autowired
    constructor(myMessage: String) {
        this.myMessage = myMessage
    }

    @RequestMapping("/hello")
    fun sayHello() = mapOf("hello" to myMessage)
}
