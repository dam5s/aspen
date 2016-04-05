package kspec.spring

import io.damo.kspec.metatests.MetaTesting.executeRunner
import io.damo.kspec.spring.SpringKSpecClassRunner
import io.damo.kspec.spring.SpringSpec
import kspec.spring.examples.SpringApplicationSpec
import kspec.spring.examples.SpringApplicationSpecUsingInit
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.reflect.KClass

class SpringKspecTest {

    @Test
    fun testSpringApplicationSpec() {
        val listener = runSpringSpec(SpringApplicationSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "GET /hello (my API)",
            "GET /world (my API)"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "GET /world (my API)"
        )))
    }

    @Test
    fun testSpringApplicationSpecUsingInit() {
        val listener = runSpringSpec(SpringApplicationSpecUsingInit::class)

        assertThat(listener.tests, equalTo(listOf(
            "GET /hello (my API)",
            "GET /world (my API)"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "GET /world (my API)"
        )))
    }
}

fun <T : SpringSpec> runSpringSpec(kClass: KClass<T>) = executeRunner(SpringKSpecClassRunner(kClass.java))
