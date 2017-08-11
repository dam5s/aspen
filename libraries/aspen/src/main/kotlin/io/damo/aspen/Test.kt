package io.damo.aspen

import org.junit.rules.TestRule
import org.junit.runner.RunWith

@RunWith(TestTreeRunner::class)
open class Test : TestTree {

    private val root = TestBranch.createRoot(javaClass.name)
    private val body: Test.() -> Unit

    constructor(body: Test.() -> Unit) {
        this.body = body
    }

    constructor() {
        this.body = {}
    }

    override fun getRoot() = root

    override fun readTestBody() {
        this.body.invoke(this)
    }

    fun useRule(rule: TestRule)
        = root.addRule(rule)

    fun before(block: () -> Unit) {
        root.before = block
    }

    fun after(block: () -> Unit) {
        root.after = block
    }

    fun describe(name: Any, block: TestDescription.() -> Unit)
        = TestDescription(root.addChildBranch(name)).block()

    fun test(name: Any? = null, block: () -> Unit)
        = focusedTest(root, name, block)

    fun ftest(name: Any? = null, block: () -> Unit)
        = focusedTest(root, name, block, focused = true)

    fun <T : TestData> tableTest(tableData: List<T>, block: T.() -> Unit)
        = focusedTableTest(root, tableData, block)

    fun <T : TestData> ftableTest(tableData: List<T>, block: T.() -> Unit)
        = focusedTableTest(root, tableData, block, focused = true)
}


open class TestData(val name: Any)


class TestDescription(private val branch: TestBranch) {
    fun test(name: Any? = null, block: () -> Unit)
        = focusedTest(branch, name, block)

    fun ftest(name: Any = "test", block: () -> Unit)
        = focusedTest(branch, name, block, focused = true)

    fun <T : TestData> tableTest(tableData: List<T>, block: T.() -> Unit)
        = focusedTableTest(branch, tableData, block)

    fun <T : TestData> ftableTest(tableData: List<T>, block: T.() -> Unit)
        = focusedTableTest(branch, tableData, block, focused = true)
}


private fun focusedTest(branch: TestBranch, name: Any?, block: () -> Unit, focused: Boolean = false) {
    val testName = name?.toString() ?: "test"
    branch.addChildLeaf(testName, block, focused)
}

private fun <T : TestData> focusedTableTest(branch: TestBranch, tableData: List<T>, block: T.() -> Unit, focused: Boolean = false) {
    tableData.forEach { data ->
        branch.addChildLeaf(data.name, { data.block() }, focused)
    }
}
