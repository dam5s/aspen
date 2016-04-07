package kspec.spring.examples

import io.damo.kspec.Spec
import io.damo.kspec.spring.SpringSpecTreeRunner
import okhttp3.OkHttpClient
import okhttp3.Request
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest

/**
 * This is another Spring example, hitting the server and testing its response.
 * This one uses #init and annotated fields instead of using #inject() and #injectValue()
 */

@RunWith(SpringSpecTreeRunner::class)
@SpringApplicationConfiguration(ExampleApplication::class)
@WebIntegrationTest("server.port:0")
class SpringApplicationSpecUsingInit : Spec() {

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
            assertThat(body, equalTo("""{"hello":"world"}"""))
            assertThat(body, equalTo("""{"hello":"$message"}"""))
        }

        test("GET /world") {
            val request = Request.Builder()
                .url("http://localhost:$port/world")
                .build()

            val response = client.newCall(request).execute()

            assertThat(response.code(), equalTo(200))
        }
    }
}
