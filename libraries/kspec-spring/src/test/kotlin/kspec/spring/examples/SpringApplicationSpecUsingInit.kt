package kspec.spring.examples

import io.damo.kspec.spring.SpringSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest

@SpringApplicationConfiguration(ExampleApplication::class)
@WebIntegrationTest("server.port:0")
class SpringApplicationSpecUsingInit : SpringSpec() {

    @Value("\${local.server.port}")
    var port = 0

    @Autowired
    lateinit var message: String

    init {
        val client = OkHttpClient()

        describe("my API") {
            test("GET /hello") {
                val request = Request.Builder()
                    .url("http://localhost:$port/hello")
                    .build()

                val response = client.newCall(request).execute()

                val body = response.body().string()
                assertThat(body, equalTo("{\"hello\":\"world\"}"))
                assertThat(body, equalTo("{\"hello\":\"$message\"}"))
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
}
