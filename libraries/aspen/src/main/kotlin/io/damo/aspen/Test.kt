package io.damo.aspen

import org.junit.runner.RunWith

@RunWith(TestTreeRunner::class)
open class Test : TestTree {
    private val root = TestBranch.createRoot()
    private val body: Test.() -> Unit
    private val unnamedTestCounter: Counter

    constructor(body: Test.() -> Unit) {
        this.body = body
        this.unnamedTestCounter = Counter()
    }

    constructor() {
        this.body = {}
        this.unnamedTestCounter = Counter()
    }

    override fun getRoot() = root

    override fun readTestBody() {
        this.body.invoke(this)
    }

    fun before(block: () -> Unit) {
        root.before = block
    }

    fun after(block: () -> Unit) {
        root.after = block
    }

    fun describe(name: Any, block: TestDescription.() -> Unit)
        = TestDescription(root.addChildBranch(name), unnamedTestCounter).block()

    fun test(name: Any? = null, block: () -> Unit)
        = focusedTest(root, name, block, unnamedTestCounter)

    fun ftest(name: Any? = null, block: () -> Unit)
        = focusedTest(root, name, block, unnamedTestCounter, focused = true)

    fun <T : TestData> tableTest(tableData: List<T>, block: T.() -> Unit)
        = focusedTableTest(root, tableData, block)

    fun <T : TestData> ftableTest(tableData: List<T>, block: T.() -> Unit)
        = focusedTableTest(root, tableData, block, focused = true)
}


open class TestData(val name: Any)


class TestDescription(private val branch: TestBranch, private val unnamedTestCounter: Counter) {
    fun test(name: Any? = null, block: () -> Unit)
        = focusedTest(branch, name, block, unnamedTestCounter)

    fun ftest(name: Any = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit)
        = focusedTest(branch, name, block, unnamedTestCounter, focused = true)

    fun <T : TestData> tableTest(tableData: List<T>, block: T.() -> Unit)
        = focusedTableTest(branch, tableData, block)

    fun <T : TestData> ftableTest(tableData: List<T>, block: T.() -> Unit)
        = focusedTableTest(branch, tableData, block, focused = true)
}


private fun focusedTest(branch: TestBranch, name: Any?, block: () -> Unit, unnamedTestCounter: Counter, focused: Boolean = false) {
    val testName = name?.toString() ?: "unnamed test #${unnamedTestCounter.next()}"
    branch.addChildLeaf(testName, block, focused)
}

private fun <T : TestData> focusedTableTest(branch: TestBranch, tableData: List<T>, block: T.() -> Unit, focused: Boolean = false) {
    tableData.forEach { data ->
        branch.addChildLeaf(data.name, { data.block() }, focused)
    }
}
