package io.damo.kspec

open class Spec {
    private var beforeBlock: (() -> Unit)? = null
    private var descriptions = arrayListOf<Description>()

    fun before(block: () -> Unit) {
        beforeBlock = block
    }

    fun describe(name: String, block: Description.() -> Unit) {
        val description = Description(name).apply(block)
        descriptions.add(description)
    }

    fun run() {
        descriptions.forEach {
            it.run(beforeBlock)
        }
    }
}

class Description(val name: String) {
    private var contexts = arrayListOf<Context>()

    fun context(block: Context.() -> Unit) {
        context("happy path", block)
    }

    fun context(name: String, block: Context.() -> Unit) {
        val context = Context(name, block)
        contexts.add(context)
    }

    fun run(beforeBlock: (() -> Unit)?) {
        contexts.forEach {
            beforeBlock?.invoke()
            it.run()
        }
    }
}

class Context(val name: String, val block: Context.() -> Unit) {
    fun run() {
        block()
    }
}
