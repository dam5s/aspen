package io.damo.kspec

import org.junit.runner.RunWith

@RunWith(SpecTreeRunner::class)
open class Spec: SpecTree {

    private val root = SpecBranch.createRoot()
    private val body: Spec.() -> Unit

    constructor(body: Spec.() -> Unit) {
        this.body = body
    }

    constructor() {
        this.body = {}
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
        SpecDescription(newBranch).block()
    }

    private var unnamedTests = 0

    fun test(name: String = "unnamed test #${++unnamedTests}", block: () -> Unit) {
        root.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${++unnamedTests}", block: () -> Unit) {
        root.addChildLeaf(name, block, focused = true)
    }
}

class SpecDescription(private var branch: SpecBranch) {
    private var unnamedTests = 0

    fun test(name: String = "unnamed test #${++unnamedTests}", block: () -> Unit) {
        branch.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${++unnamedTests}", block: () -> Unit) {
        branch.addChildLeaf(name, block, focused = true)
    }
}
