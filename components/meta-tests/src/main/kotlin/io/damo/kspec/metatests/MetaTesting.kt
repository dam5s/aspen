package io.damo.kspec.metatests

import org.junit.runner.Description
import org.junit.runner.Runner
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunListener
import org.junit.runner.notification.RunNotifier
import java.util.*

fun executeRunner(runner: Runner): MemorizingRunListener {
    val listener = MemorizingRunListener()
    val notifier = RunNotifier().apply { addListener(listener) }

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
