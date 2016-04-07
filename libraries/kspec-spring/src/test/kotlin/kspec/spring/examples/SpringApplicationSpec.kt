package kspec.spring.examples

import io.damo.kspec.Spec
import io.damo.kspec.spring.SpringSpecTreeRunner
import io.damo.kspec.spring.inject
import io.damo.kspec.spring.injectValue
import okhttp3.OkHttpClient
import okhttp3.Request
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.runner.RunWith
import org.springframework.boot.test.SpringApplicationConfiguration
import org.springframework.boot.test.WebIntegrationTest

@RunWith(SpringSpecTreeRunner::class)
@SpringApplicationConfiguration(ExampleApplication::class)
@WebIntegrationTest("server.port:0")
class SpringApplicationSpec : Spec({

    val message: String = inject("myMessage")
    val port = injectValue("local.server.port", Int::class)
    val client = OkHttpClient()

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
})
