package io.damo.kspec.spring

import io.damo.kspec.JUnitKSpecClassRunner
import io.damo.kspec.Spec
import io.damo.kspec.SpecDescription
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import org.springframework.test.context.TestContextManager


@RunWith(SpringKSpecClassRunner::class)
open class SpringSpec(body: Spec.() -> Unit) : Spec(body) {
}

open class SpringKSpecClassRunner<T : Spec>(specificationClass: Class<T>) : JUnitKSpecClassRunner<T>(specificationClass) {
    val testContextManager = TestContextManager(specificationClass)

    val testMethod = Spec::class.java.methods[0] // This is a Hack to make TestContextManager happy.
                                                 // We do not run test Methods.

    override fun run(notifier: RunNotifier?) {
        testContextManager.beforeTestClass()

        try {
            super.run(notifier)
        } finally {
            testContextManager.afterTestClass()
        }
    }

    override fun buildBeforeBlock(spec: T, specDescription: SpecDescription): (() -> Unit)? {
        val originalBeforeBlock = super.buildBeforeBlock(spec, specDescription)

        return {
            testContextManager.prepareTestInstance(spec)
            testContextManager.beforeTestMethod(specDescription, testMethod)
            originalBeforeBlock?.invoke()
        }
    }

    override fun buildAfterBlock(spec: T, specDescription: SpecDescription): (() -> Unit)? {
        val originalAfterBlock = super.buildAfterBlock(spec, specDescription)

        return {
            try {
                originalAfterBlock?.invoke()
            } finally {
                testContextManager.afterTestMethod(specDescription, testMethod, null)
            }
        }
    }
}
