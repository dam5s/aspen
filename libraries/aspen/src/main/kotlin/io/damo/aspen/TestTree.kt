package io.damo.aspen

import org.junit.rules.TestRule


interface TestTree {
    fun getRoot(): TestBranch
    fun readTestBody()
}


abstract class TestTreeNode(val name: String, val parent: TestBranch?) {

    companion object {
        private var currentId: Long = 0
        fun nextId() = currentId++
    }

    val id: Long = nextId()
    val children: MutableCollection<TestTreeNode> = arrayListOf()


    fun isRoot() = (parent == null)

    fun rootName(): String = parent?.rootName() ?: name

    abstract fun allRules(): List<TestRule>

    abstract fun isFocused(): Boolean
}


class TestBranch(name: String, parent: TestBranch?) : TestTreeNode(name, parent) {

    companion object {
        fun createRoot(name: String) = TestBranch(name, null)
    }

    private val rules: MutableList<TestRule> = arrayListOf()

    var before: (() -> Unit)? = null
    var after: (() -> Unit)? = null


    override fun isFocused() = children.any(TestTreeNode::isFocused)

    override fun allRules(): List<TestRule> {
        val parentRules = parent?.rules ?: arrayListOf()
        parentRules.addAll(rules)
        return parentRules
    }

    fun addRule(rule: TestRule) {
        rules.add(rule)
    }

    fun addChildBranch(name: Any): TestBranch {
        val newBranch = TestBranch(name.toString(), this)
        children.add(newBranch)
        return newBranch
    }

    fun addChildLeaf(name: Any, block: () -> Unit, focused: Boolean = false): TestLeaf {
        val newLeaf = TestLeaf(name.toString(), this, block, focused = focused)
        children.add(newLeaf)
        return newLeaf
    }
}


class TestLeaf(name: String, parent: TestBranch, val block: (() -> Unit), val focused: Boolean) : TestTreeNode(name, parent) {

    override fun isFocused() = focused

    override fun allRules(): List<TestRule> = parent?.allRules() ?: arrayListOf()


    fun collectBeforesAndAfters(): Hooks {
        val befores = arrayListOf<() -> Unit>()
        val afters = arrayListOf<() -> Unit>()
        var currentNode = parent

        while (currentNode != null) {
            currentNode.before?.let {
                befores.add(it)
            }
            currentNode.after?.let {
                afters.add(it)
            }
            currentNode = currentNode.parent
        }

        return Hooks(befores.reversed(), afters)
    }

    val testName: String
        get() {
            if (parent!!.isRoot()) {
                return name
            }

            return "${parent.name} $name"
        }
}

data class Hooks(
    val befores: List<() -> Unit>,
    val afters: List<() -> Unit>
)
