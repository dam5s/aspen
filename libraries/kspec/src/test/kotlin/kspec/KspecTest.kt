package kspec

import io.damo.kspec.SpecTreeRunner
import io.damo.kspec.SpecTree
import io.damo.kspec.metatests.MetaTesting.executeRunner
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
            "unnamed test #1",
            "repository creation error"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "repository creation error"
        )))
    }

    @Test
    fun testControllerSpecWithInit() {
        val listener = runSpec(BusinessControllerSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1",
            "repository creation error"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "repository creation error"
        )))
    }

    @Test
    fun testDomainModelSpec() {
        val listener = runSpec(PersonSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1",
            "with a middle name",
            "unnamed test #2"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "with a middle name"
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
            "focused"
        )))
    }

    @Test
    fun testNestedSpec() {
        val listener = runSpec(NestedSpecExample::class)

        assertThat(listener.tests, equalTo(listOf(
            "unnamed test #1",
            "unnamed test #2",
            "unnamed test #3",
            "this test will fail in nested something",
            "unnamed test #4",
            "unnamed test #5",
            "this test will fail in something else"
        )))

        assertThat(listener.failingTests, equalTo(listOf(
            "this test will fail in nested something",
            "this test will fail in something else"
        )))
    }

    @Test
    fun testTableBasedSpec() {
        val listener = runSpec(ReservationSpec::class)

        assertThat(listener.tests, equalTo(listOf(
            "when the status is OPEN",
            "when the status is STARTED",
            "when the status is BILLED",
            "when the status is PAID",
            "when the status is OPEN",
            "when the status is STARTED",
            "when the status is BILLED",
            "when the status is PAID"
        )))
    }
}

fun <T : SpecTree> runSpec(kClass: KClass<T>) = executeRunner(SpecTreeRunner(kClass.java))
