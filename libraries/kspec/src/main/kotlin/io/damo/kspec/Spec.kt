package io.damo.kspec

import org.junit.runner.RunWith

@RunWith(SpecTreeRunner::class)
open class Spec : SpecTree {
    private val root = SpecBranch.createRoot()
    private val body: Spec.() -> Unit
    private val unnamedTestCounter: Counter

    constructor(body: Spec.() -> Unit) {
        this.body = body
        this.unnamedTestCounter = Counter()
    }

    constructor() {
        this.body = {}
        this.unnamedTestCounter = Counter()
    }

    override fun getRoot() = root

    override fun readSpecBody() {
        this.body.invoke(this)
    }

    fun before(block: () -> Unit) {
        root.before = block
    }

    fun after(block: () -> Unit) {
        root.after = block
    }

    fun describe(name: String, block: SpecDescription.() -> Unit) {
        val newBranch = root.addChildBranch(name)
        SpecDescription(newBranch, unnamedTestCounter).block()
    }


    fun test(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        root.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        root.addChildLeaf(name, block, focused = true)
    }
}

class SpecDescription(private val branch: SpecBranch, private val unnamedTestCounter: Counter) {
    fun test(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block, focused = true)
    }
}
