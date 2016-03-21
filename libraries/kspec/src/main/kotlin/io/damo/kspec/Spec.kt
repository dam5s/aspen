package io.damo.kspec

import org.junit.runner.RunWith

@RunWith(JUnitKSpecClassRunner::class)
open class Spec {
    var descriptions = arrayListOf<SpecDescription>()
        private set
    var beforeBlock: (() -> Unit)? = null
        private set
    var afterBlock: (() -> Unit)? = null
        private set

    constructor(body: Spec.() -> Unit) {
        this.body()
    }

    fun before(block: () -> Unit) {
        beforeBlock = block
    }

    fun after(block: () -> Unit) {
        afterBlock = block
    }

    fun describe(name: String, block: SpecDescription.() -> Unit) {
        val description = SpecDescription(name).apply(block)
        descriptions.add(description)
    }
}

class SpecDescription(val name: String) {
    private var tests = arrayListOf<Test>()
    private var focusedTests = arrayListOf<Test>()

    val testsToRun: List<Test>
        get() {
            if (focusedTests.size > 0)
                return focusedTests
            else
                return tests
        }

    var unnamedContexts = 0

    fun test(name: String = "unnamed test #${++unnamedContexts}", block: Test.() -> Unit) {
        tests.add(Test(name, block))
    }

    fun ftest(name: String = "unnamed test #${++unnamedContexts}", block: Test.() -> Unit) {
        focusedTests.add(Test(name, block))
    }
}

class Test(val name: String, val block: Test.() -> Unit) {
    fun run(beforeBlock: (() -> Unit)?, afterBlock: (() -> Unit)?) {
        beforeBlock?.invoke()
        try {
            block()
        } finally {
            afterBlock?.invoke()
        }
    }
}
