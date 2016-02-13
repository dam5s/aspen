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
    var contexts = arrayListOf<SpecContext>()
        private set

    var unnamedContexts = 0

    fun context(block: SpecContext.() -> Unit) {
        context("unnamed #${++unnamedContexts}", block)
    }

    fun context(name: String, block: SpecContext.() -> Unit) {
        val context = SpecContext(name, block)
        contexts.add(context)
    }
}

class SpecContext(val name: String, val block: SpecContext.() -> Unit) {
    fun run(beforeBlock: (() -> Unit)?, afterBlock: (() -> Unit)?) {
        beforeBlock?.invoke()
        try {
            block()
        } finally {
            afterBlock?.invoke()
        }
    }
}
