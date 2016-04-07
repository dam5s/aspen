package io.damo.kspec.spring

import io.damo.kspec.Spec
import io.damo.kspec.SpecTree
import io.damo.kspec.SpecTreeNodeRunner
import io.damo.kspec.SpecTreeRunner
import org.junit.runner.notification.RunNotifier
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestContextManager
import java.util.concurrent.ConcurrentHashMap


open class SpringSpecTreeRunner<T : SpecTree>(specificationClass: Class<T>) : SpecTreeRunner<T>(specificationClass) {

    companion object {
        val appContexts = ConcurrentHashMap<SpecTree, ApplicationContext>()
    }


    private val testContextManager = KSpecTestContextManager(specificationClass)
    private val testMethod = Spec::class.java.methods[0]
    // This is a Hack to make TestContextManager happy. We do not run test Methods.

    override fun run(notifier: RunNotifier?) {
        testContextManager.beforeTestClass()

        try {
            super.run(notifier)
        } finally {
            testContextManager.afterTestClass()
        }
    }

    override fun buildChildren(): MutableList<SpecTreeNodeRunner> {
        val root = specTree.getRoot()
        root.before = decorateBeforeBlock(root.before)
        root.after = decorateAfterBlock(root.after)

        // This is where we expect spec to be initialized and memoized.
        appContexts.put(specTree, testContextManager.applicationContext)

        return super.buildChildren()
    }

    private fun decorateBeforeBlock(originalBeforeBlock: (() -> Unit)?): (() -> Unit)? {
        return {
            testContextManager.prepareTestInstance(specTree)
            testContextManager.beforeTestMethod(specTree, testMethod)
            originalBeforeBlock?.invoke()
        }
    }

    private fun decorateAfterBlock(originalAfterBlock: (() -> Unit)?): (() -> Unit)? {
        return {
            try {
                originalAfterBlock?.invoke()
            } finally {
                testContextManager.afterTestMethod(specTree, testMethod, null)
            }
        }
    }
}

class KSpecTestContextManager<T : SpecTree>(specClass: Class<T>) : TestContextManager(specClass) {
    val applicationContext: ApplicationContext
        get() {
            return testContext.applicationContext
        }
}
