package io.damo.kspec

import org.junit.runner.RunWith

@RunWith(SpecTreeRunner::class)
open class NestedSpec : SpecTree {

    private val body: NestedSpec.() -> Unit
    private val branch: SpecBranch
    private val unnamedTestCounter: Counter

    constructor(body: NestedSpec.() -> Unit) {
        this.branch = SpecBranch.createRoot()
        this.unnamedTestCounter = Counter()
        this.body = body
    }

    constructor(branch: SpecBranch, counter: Counter) {
        this.branch = branch
        this.unnamedTestCounter = counter
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
        NestedSpec(newBranch, unnamedTestCounter).block()
    }

    fun test(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block, focused = true)
    }
}
