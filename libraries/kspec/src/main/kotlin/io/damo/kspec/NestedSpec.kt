package io.damo.kspec

import org.junit.runner.RunWith

@RunWith(SpecTreeRunner::class)
open class NestedSpec: SpecTree {

    private val body: NestedSpec.() -> Unit
    private val branch: SpecBranch
    private var unnamedTests = 0

    constructor(body: NestedSpec.() -> Unit) {
        this.branch = SpecBranch.createRoot()
        this.body = body
    }

    constructor(branch: SpecBranch) {
        this.branch = branch
        this.body = {}
    }

    override fun getRoot() = branch

    override fun readSpecBody() {
        this.body.invoke(this)
    }

    fun before(block: () -> Unit) {
        branch.before = block
    }

    fun after(block: () -> Unit) {
        branch.after = block
    }

    fun describe(name: String, block: NestedSpec.() -> Unit) {
        val newBranch = branch.addChildBranch(name)
        NestedSpec(newBranch).block()
    }

    fun test(name: String = "unnamed test #${++unnamedTests}", block: () -> Unit) {
        branch.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${++unnamedTests}", block: () -> Unit) {
        branch.addChildLeaf(name, block, focused = true)
    }
}
