package io.damo.aspen

import org.junit.runner.RunWith

@RunWith(TestTreeRunner::class)
open class NestedTest : TestTree {

    private val body: NestedTest.() -> Unit
    private val branch: TestBranch
    private val unnamedTestCounter: Counter

    constructor(body: NestedTest.() -> Unit) {
        this.branch = TestBranch.createRoot()
        this.unnamedTestCounter = Counter()
        this.body = body
    }

    constructor(branch: TestBranch, counter: Counter) {
        this.branch = branch
        this.unnamedTestCounter = counter
        this.body = {}
    }

    override fun getRoot() = branch

    override fun readTestBody() {
        this.body.invoke(this)
    }

    fun before(block: () -> Unit) {
        branch.before = block
    }

    fun after(block: () -> Unit) {
        branch.after = block
    }

    fun describe(name: String, block: NestedTest.() -> Unit) {
        val newBranch = branch.addChildBranch(name)
        NestedTest(newBranch, unnamedTestCounter).block()
    }

    fun test(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block, focused = true)
    }
}
