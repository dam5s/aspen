package io.damo.kspec

import org.junit.runner.RunWith

@RunWith(JUnitClassRunner::class)
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
    var tests = arrayListOf<Test>()
        private set

    var unnamedContexts = 0

    fun test(block: Test.() -> Unit) {
        test("unnamed test #${++unnamedContexts}", block)
    }

    fun test(name: String, block: Test.() -> Unit) {
        val context = Test(name, block)
        tests.add(context)
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
