package io.damo.kspec.spring

import io.damo.kspec.JUnitDescriptionRunner
import io.damo.kspec.JUnitKSpecClassRunner
import io.damo.kspec.Spec
import io.damo.kspec.SpecDescription
import org.junit.runner.RunWith
import org.junit.runner.notification.RunNotifier
import org.springframework.context.ApplicationContext
import org.springframework.core.env.Environment
import org.springframework.test.context.TestContextManager
import kotlin.reflect.KClass


@RunWith(SpringKSpecClassRunner::class)
open class SpringSpec : Spec {

    lateinit var appContext: ApplicationContext
        internal set

    private val env: Environment by lazy {
        appContext.getBean(Environment::class.java)
    }

    private val body: (SpringSpec.() -> Unit)?


    constructor(body: SpringSpec.() -> Unit) : super({}) {
        this.body = body
    }

    constructor() : super() {
        this.body = null
    }


    @Suppress("UNCHECKED_CAST")
    fun <T> inject(name: String): T = appContext.getBean(name) as T

    fun <T> inject(javaClass: Class<T>): T = appContext.getBean(javaClass)

    fun <T> inject(name: String, javaClass: Class<T>): T = appContext.getBean(name, javaClass)

    fun <T: Any> inject(kClass: KClass<T>): T = inject(kClass.java)

    fun <T: Any> inject(name: String, kClass: KClass<T>): T = inject(name, kClass.java)

    fun <T: Any> injectValue(name: String, kClass: KClass<T>): T = env.getRequiredProperty(name, kClass.java)


    internal fun setupBody() {
        this.body?.invoke(this)
    }
}

open class SpringKSpecClassRunner<T : SpringSpec>(specificationClass: Class<T>) : JUnitKSpecClassRunner<T>(specificationClass) {

    private val testContextManager = KSpecTestContextManager(specificationClass)
    private val testMethod = Spec::class.java.methods[0] // This is a Hack to make TestContextManager happy.
                                                         // We do not run test Methods.

    override fun getChildren(): MutableList<JUnitDescriptionRunner<T>> {
        // This is where we expect spec to be initialized and memoized.
        spec.appContext = testContextManager.applicationContext
        spec.setupBody()

        return super.getChildren()
    }

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

class KSpecTestContextManager<T: SpringSpec>(specClass: Class<T>): TestContextManager(specClass) {
    val applicationContext: ApplicationContext
        get() { return testContext.applicationContext }
}
