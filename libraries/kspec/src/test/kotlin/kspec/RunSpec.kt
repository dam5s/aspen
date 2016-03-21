package kspec

import io.damo.kspec.JUnitKSpecClassRunner
import io.damo.kspec.Spec
import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import java.util.*
import kotlin.reflect.KClass

fun <T : Spec> runSpec(kClass: KClass<T>): MemorizingRunListener {
    val listener = MemorizingRunListener()
    val notifier = RunNotifier().apply { addListener(listener) }
    val runner = JUnitKSpecClassRunner(kClass.java)

    runner.run(notifier)

    return listener
}

class MemorizingRunListener : RunListener() {

    val tests: MutableList<String> = ArrayList()
    val failingTests: MutableList<String> = ArrayList()

    override fun testStarted(description: Description) {
        tests.add(description.displayName)
    }

    override fun testFailure(failure: Failure) {
        failingTests.add(failure.description.displayName)
    }
}

