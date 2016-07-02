package io.damo.aspen.spring

import io.damo.aspen.Test
import io.damo.aspen.TestTree
import io.damo.aspen.TestTreeNodeRunner
import io.damo.aspen.TestTreeRunner
import org.junit.runner.notification.RunNotifier
import org.springframework.context.ApplicationContext
import org.springframework.test.context.TestContextManager
import java.util.concurrent.ConcurrentHashMap


open class SpringTestTreeRunner<T : TestTree>(testClass: Class<T>) : TestTreeRunner<T>(testClass) {

    companion object {
        val appContexts = ConcurrentHashMap<TestTree, ApplicationContext>()
    }


    private val testContextManager = AspenTestContextManager(testClass)

    /**
     * This is a Hack to make TestContextManager happy. We do not run test Methods.
     * This is made open in order to deal with runners that will inspect annotations
     * on the given method or its class.
     *
     * @see SpringTransactionalTestTreeRunner
     */
    open val testMethod = Test::class.java.methods[0]

    override fun run(notifier: RunNotifier?) {
        testContextManager.beforeTestClass()

        try {
            super.run(notifier)
        } finally {
            testContextManager.afterTestClass()
        }
    }

    override fun buildChildren(): MutableList<TestTreeNodeRunner> {
        // This is where we expect the TestTree instance to be initialized and memoized.
        SpringTestTreeRunner.appContexts.put(testTree, testContextManager.applicationContext)

        // The TestTree is built by reading the body at that point.
        val children = super.buildChildren()

        // So we can now decorate the root.
        val root = testTree.getRoot()
        root.before = decorateBeforeBlock(root.before)
        root.after = decorateAfterBlock(root.after)

        return children
    }

    private fun decorateBeforeBlock(originalBeforeBlock: (() -> Unit)?): (() -> Unit)? {
        return {
            testContextManager.prepareTestInstance(testTree)
            testContextManager.beforeTestMethod(testTree, testMethod)
            originalBeforeBlock?.invoke()
        }
    }

    private fun decorateAfterBlock(originalAfterBlock: (() -> Unit)?): (() -> Unit)? {
        return {
            try {
                originalAfterBlock?.invoke()
            } finally {
                testContextManager.afterTestMethod(testTree, testMethod, null)
            }
        }
    }
}

class AspenTestContextManager<T : TestTree>(testClass: Class<T>) : TestContextManager(testClass) {
    val applicationContext: ApplicationContext
        get() {
            return testContext.applicationContext
        }
}
