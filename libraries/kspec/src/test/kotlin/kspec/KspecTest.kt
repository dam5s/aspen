package kspec

import io.damo.kspec.SpecTreeRunner
import io.damo.kspec.SpecTree
import io.damo.kspec.metatests.executeRunner
import kspec.examples.*
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.junit.Test
import kotlin.reflect.KClass

class KspecTest {

    @Test
    fun testControllerSpec() {
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
    fun testControllerSpecWithInit() {
        val listener = runSpec(BusinessControllerSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1 (#create)",
            "repository creation error (#create)"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "repository creation error (#create)"
        )))
    }

    @Test
    fun testDomainModelSpec() {
        val listener = runSpec(PersonSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1 (#fullName)",
            "with a middle name (#fullName)",
            "unnamed test #2 (#greeting)"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "with a middle name (#fullName)"
        )))
    }

    @Test
    fun testTopLevelTestSpec() {
        val listener = runSpec(RunnableSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1"
        )))

        assertThat(listener.failingTests.isEmpty(), equalTo(true))
    }

    @Test
    fun testFocusedSpec() {
        val listener = runSpec(FocusedSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "focused (#something)",
            "focused too (#somethingElse)"
        )))

        assertThat(listener.failingTests, equalTo(arrayListOf(
        )))
    }

    @Test
    fun testNestedSpec() {
        val listener = runSpec(NestedSpecExample::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1 (something)",
            "unnamed test #2 (nested something)",
            "unnamed test #3 (nested something)",
            "this test will fail in nested something (nested something)",
            "unnamed test #4 (nested something else)",
            "unnamed test #5 (something else)",
            "this test will fail in something else (something else)"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "this test will fail in nested something (nested something)",
            "this test will fail in something else (something else)"
        )))
    }

    @Test
    fun testTableBasedSpec() {
        val listener = runSpec(ReservationSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "when the status is OPEN (#amount - table based)",
            "when the status is STARTED (#amount - table based)",
            "when the status is BILLED (#amount - table based)",
            "when the status is PAID (#amount - table based)",
            "when the status is OPEN (#amount - map based)",
            "when the status is STARTED (#amount - map based)",
            "when the status is BILLED (#amount - map based)",
            "when the status is PAID (#amount - map based)"
        )))

        assertThat(listener.failingTests, equalTo(arrayListOf(
        )))
    }

    @Test
    fun testExpectedExceptionSpec() {
        val listener = runSpec(ExceptionThrowingServiceSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "with correct class and message",
            "with correct class, no message",
            "with correct parent class",
            "with correct message",
            "with incorrect class",
            "with incorrect message"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "with incorrect class",
            "with incorrect message"
        )))
    }
}

fun <T : SpecTree> runSpec(kClass: KClass<T>) = executeRunner(SpecTreeRunner(kClass.java))
