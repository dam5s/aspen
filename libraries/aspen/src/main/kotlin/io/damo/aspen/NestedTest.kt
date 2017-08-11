package io.damo.aspen

import org.junit.runner.RunWith

@RunWith(TestTreeRunner::class)
open class NestedTest : TestTree {

    private val body: NestedTest.() -> Unit
    private val branch: TestBranch

    constructor(body: NestedTest.() -> Unit) {
        this.branch = TestBranch.createRoot(javaClass.name)
        this.body = body
    }

    constructor(branch: TestBranch) {
        this.branch = branch
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
        NestedTest(newBranch).block()
    }

    fun test(name: String = "test", block: () -> Unit) {
        branch.addChildLeaf(name, block)
    }

    fun ftest(name: String = "test", block: () -> Unit) {
        branch.addChildLeaf(name, block, focused = true)
    }
}
