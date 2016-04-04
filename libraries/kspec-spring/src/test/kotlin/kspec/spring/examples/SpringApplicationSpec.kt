package kspec.spring.examples

import io.damo.kspec.spring.SpringSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest

@SpringApplicationConfiguration(ExampleApplication::class)
@WebIntegrationTest("server.port:9090")
class SpringApplicationSpec : SpringSpec({

    val client = OkHttpClient()

    describe("my API") {
        test("GET /hello") {
            val request = Request.Builder()
                .url("http://localhost:9090/hello")
                .build()

            val response = client.newCall(request).execute()

            assertThat(response.body().string(), equalTo("{\"hello\":\"world\"}"))
        }

        test("GET /world") {
            val request = Request.Builder()
                .url("http://localhost:9090/world")
                .build()

            val response = client.newCall(request).execute()

            assertThat(response.code(), equalTo(200))
        }
    }
})
