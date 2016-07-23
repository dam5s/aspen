package aspen.spring.examples

import io.damo.aspen.Test
import io.damo.aspen.spring.SpringTestTreeRunner
import io.damo.aspen.spring.inject
import io.damo.aspen.spring.injectValue
import okhttp3.OkHttpClient
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest

/**
 * This is a basic example, hitting the server and testing its response.
 * In addition to inheriting from Test, you will need to use the custom SpringTestTreeRunner.
 *
 * You can inject Spring @Beans or @Components using #inject()
 * You can inject @Value properties using #injectValue()
 */

@RunWith(SpringTestTreeRunner::class)
@SpringApplicationConfiguration(ExampleApplication::class)
@WebIntegrationTest("server.port:0")
class SpringApplicationTestExample : Test({

    val message: String = inject("myMessage")
    val port = injectValue("local.server.port", Int::class)
    val client = OkHttpClient()

    test("GET /hello") {
        val request = Request.Builder()
            .url("http://localhost:$port/hello")
            .build()

        val response = client.newCall(request).execute()

        val body = response.body().string()
        assertThat(body).isEqualTo("""{"hello":"world"}""")
        assertThat(body).isEqualTo("""{"hello":"$message"}""")
    }

    test("GET /world") {
        val request = Request.Builder()
            .url("http://localhost:$port/world")
            .build()

        val response = client.newCall(request).execute()

        assertThat(response.code()).isEqualTo(200)
    }
})
