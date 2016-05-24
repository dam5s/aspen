package io.damo.kspec.robolectric

import io.damo.kspec.*
import org.junit.runner.notification.RunNotifier
import org.junit.runners.model.FrameworkMethod
import org.robolectric.internal.InstrumentingClassLoaderFactory
import org.robolectric.internal.SdkConfig
import java.lang.reflect.Method

open class RobolectricSpecTreeRunner<T : SpecTree>(specificationClass: Class<T>) : SpecTreeRunner<T>(specificationClass) {

    /**
     * This is a Hack to make RobolectricTestRunner happy. We do not run test Methods.
     * This is made open in order to let the runner inspect annotations
     * on the given method or its class.
     */
    private val testMethod = Spec::class.java.methods[0]

    private val helper = RobolectricHelper(specificationClass)
    private val config = helper.getConfig(testMethod)
    private val appManifest = helper.getAppManifest(config)
    private val instrumentingClassLoaderFactory = InstrumentingClassLoaderFactory(
        helper.createClassLoaderConfig(config),
        helper.jarResolver
    )
    private val sdkEnv = instrumentingClassLoaderFactory.getSdkEnvironment(SdkConfig(
        helper.pickSdkVersion(config, appManifest)
    ))

    override fun run(notifier: RunNotifier?) {
        //beforeTestClass

        try {
            super.run(notifier)
        } finally {
            //afterTestClass
        }
    }

    override fun runChild(child: SpecTreeNodeRunner, notifier: RunNotifier) {
        when (child) {
            is SpecBranchRunner<*> -> super.runChild(child, notifier)
            is SpecLeafRunner -> this.runLeaf(child)
            else -> throw IllegalArgumentException("Encountered unexpected SpecTreeNodeRunner type $child")
        }
    }

    fun runLeaf(runner: SpecLeafRunner) {
        val frameworkMethod = SpecLeafRunnerFrameworkMethod(runner, testMethod)

        helper.methodBlock(frameworkMethod, config, appManifest, sdkEnv).evaluate()
    }
}

internal class SpecLeafRunnerFrameworkMethod(val runner: SpecLeafRunner, method: Method) : FrameworkMethod(method) {
    override fun getName() = runner.specLeaf.testName
}
