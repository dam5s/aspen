package kspec

import io.damo.kspec.JUnitKSpecClassRunner
import io.damo.kspec.Spec
import io.damo.kspec.metatests.MetaTesting.executeRunner
import kspec.examples.CompanyControllerSpec
import kspec.examples.FocusedSpec
import kspec.examples.PersonSpec
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.reflect.KClass

class KspecTest {

    @Test
    fun testCompanyControllerSpec() {
        val listener = runSpec(CompanyControllerSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1 (#create)",
            "repository creation error (#create)"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "repository creation error (#create)"
        )))
    }

    @Test
    fun testPersonSpec() {
        val listener = runSpec(PersonSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1 (#fullName)",
            "with a middle name (#fullName)",
            "unnamed test #1 (#greeting)"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "with a middle name (#fullName)"
        )))
    }

    @Test
    fun testFocusedSpec() {
        val listener = runSpec(FocusedSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "focused (#something)"
        )))
    }
}

fun <T : Spec> runSpec(kClass: KClass<T>) = executeRunner(JUnitKSpecClassRunner(kClass.java))
