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

    /**
     * This is a Hack to make TestContextManager happy. We do not run test Methods.
     * This is made open in order to deal with runners that will inspect annotations
     * on the given method or its class.
     *
     * @see SpringTransactionalSpecTreeRunner
     */
    open val testMethod = Spec::class.java.methods[0]

    override fun run(notifier: RunNotifier?) {
        testContextManager.beforeTestClass()

        try {
            super.run(notifier)
        } finally {
            testContextManager.afterTestClass()
        }
    }

    override fun buildChildren(): MutableList<SpecTreeNodeRunner> {
        // This is where we expect the SpecTree instance to be initialized and memoized.
        SpringSpecTreeRunner.appContexts.put(specTree, testContextManager.applicationContext)

        // The SpecTree is built by reading the body at that point.
        val children = super.buildChildren()

        // So we can now decorate the root.
        val root = specTree.getRoot()
        root.before = decorateBeforeBlock(root.before)
        root.after = decorateAfterBlock(root.after)

        return children
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
