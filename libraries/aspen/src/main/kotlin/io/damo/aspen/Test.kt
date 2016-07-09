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

    fun describe(name: Any, block: TestDescription.() -> Unit) {
        val newBranch = root.addChildBranch(name.toString())
        TestDescription(newBranch, unnamedTestCounter).block()
    }

    fun test(name: Any = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        root.addChildLeaf(name.toString(), block)
    }

    fun ftest(name: Any = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        root.addChildLeaf(name.toString(), block, focused = true)
    }

    fun <T : TestData> tableTest(tableData: List<T>, block: T.() -> Unit) {
        tableData.forEach { data ->
            val newBranch = root.addChildBranch(data.context.toString())

            data.apply {
                branch = newBranch
                unnamedTestCounter = this@Test.unnamedTestCounter
                block()
            }
        }
    }
}

open class TestData(val context:Any) {
    internal lateinit var branch: TestBranch
    internal lateinit var unnamedTestCounter: Counter

    fun test(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block, focused = true)
    }
}

class TestDescription(private val branch: TestBranch, private val unnamedTestCounter: Counter) {
    fun test(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block)
    }

    fun ftest(name: String = "unnamed test #${unnamedTestCounter.next()}", block: () -> Unit) {
        branch.addChildLeaf(name, block, focused = true)
    }
}
