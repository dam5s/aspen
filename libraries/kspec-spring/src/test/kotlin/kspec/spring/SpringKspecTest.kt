package kspec.spring

import io.damo.kspec.Spec
import io.damo.kspec.metatests.MetaTesting.executeRunner
import io.damo.kspec.spring.SpringSpecTreeRunner
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
            "GET /hello",
            "GET /world"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "GET /world"
        )))
    }

    @Test
    fun testSpringApplicationSpecUsingInit() {
        val listener = runSpringSpec(SpringApplicationSpecUsingInit::class)

        assertThat(listener.tests, equalTo(listOf(
            "GET /hello",
            "GET /world"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "GET /world"
        )))
    }
}

fun <T : Spec> runSpringSpec(kClass: KClass<T>) = executeRunner(SpringSpecTreeRunner(kClass.java))
