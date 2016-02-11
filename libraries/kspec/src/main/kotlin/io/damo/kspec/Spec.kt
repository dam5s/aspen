package io.damo.kspec

open class Spec {
    var descriptions = arrayListOf<SpecDescription>()
        private set
    var beforeBlock: (() -> Unit)? = null
        private set

    fun before(block: () -> Unit) {
        beforeBlock = block
    }

    fun describe(name: String, block: SpecDescription.() -> Unit) {
        val description = SpecDescription(name).apply(block)
        descriptions.add(description)
    }
}

class SpecDescription(val name: String) {
    var contexts = arrayListOf<SpecContext>()
        private set

    fun context(block: SpecContext.() -> Unit) {
        context("happy path", block)
    }

    fun context(name: String, block: SpecContext.() -> Unit) {
        val context = SpecContext(name, block)
        contexts.add(context)
    }
}

class SpecContext(val name: String, val block: SpecContext.() -> Unit) {
    fun run(beforeBlock: (() -> Unit)?) {
        beforeBlock?.invoke()
        block()
    }
}
