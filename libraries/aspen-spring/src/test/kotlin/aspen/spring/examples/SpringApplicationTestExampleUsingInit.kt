package aspen.spring.examples

import io.damo.aspen.Test
import io.damo.aspen.spring.SpringTestTreeRunner
import okhttp3.OkHttpClient
import okhttp3.Request
import org.assertj.core.api.Assertions.assertThat
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest

/**
 * This is another Spring example, hitting the server and testing its response.
 * This one uses #init and annotated fields instead of using #inject() and #injectValue()
 */

@RunWith(SpringTestTreeRunner::class)
@SpringApplicationConfiguration(ExampleApplication::class)
@WebIntegrationTest("server.port:0")
class SpringApplicationTestExampleUsingInit : Test() {

    @Value("\${local.server.port}")
    var port = 0

    @Autowired
    lateinit var message: String

    init {
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
    }
}
