package io.damo.aspen


interface TestTree {
    fun getRoot(): TestBranch
    fun readTestBody()
}

abstract class TestTreeNode {
    companion object {
        private var currentId: Long = 0
        fun nextId() = currentId++
    }

    val id: Long
    val name: String
    val parent: TestBranch?
    val children: MutableCollection<TestTreeNode>

    constructor(
        name: String,
        parent: TestBranch?,
        children: MutableCollection<TestTreeNode>
    ) {
        this.id = nextId()
        this.name = name
        this.parent = parent
        this.children = children
    }

    fun isRoot() = (parent == null)

    abstract fun isFocused(): Boolean
}

class TestBranch : TestTreeNode {
    companion object {
        fun createRoot() = TestBranch("", null, arrayListOf(), null, null)
    }

    var before: (() -> Unit)?
    var after: (() -> Unit)?

    constructor(
        name: String,
        parent: TestBranch?,
        children: MutableCollection<TestTreeNode> = arrayListOf(),
        before: (() -> Unit)? = null,
        after: (() -> Unit)? = null
    ) : super(name, parent, children) {

        this.before = before
        this.after = after
    }

    override fun isFocused(): Boolean {
        return children.any {
            it.isFocused()
        }
    }

    fun addChildBranch(name: String): TestBranch {
        val newBranch = TestBranch(name, this)
        children.add(newBranch)
        return newBranch
    }

    fun addChildLeaf(name: String, block: () -> Unit, focused: Boolean = false): TestLeaf {
        val newLeaf = TestLeaf(name, this, block, focused = focused)
        children.add(newLeaf)
        return newLeaf
    }
}

class TestLeaf : TestTreeNode {

    val block: (() -> Unit)
    val focused: Boolean

    constructor(name: String, parent: TestBranch, block: (() -> Unit), focused: Boolean) : super(name, parent, arrayListOf()) {
        this.block = block
        this.focused = focused
    }

    override fun isFocused() = focused

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
            val parentName = parent?.name ?: ""

            if (parentName.isEmpty()) {
                return name
            } else {
                return "$name ($parentName)"
            }
        }
}

data class Hooks(
    val befores: List<() -> Unit>,
    val afters: List<() -> Unit>
)
