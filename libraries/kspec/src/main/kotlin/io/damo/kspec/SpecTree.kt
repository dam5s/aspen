package io.damo.kspec


interface SpecTree {
    fun getRoot(): SpecBranch
    fun readSpecBody()
}

abstract class SpecTreeNode {
    companion object {
        private var currentId: Long = 0
        fun nextId() = currentId++
    }

    val id: Long
    val name: String
    val parent: SpecBranch?
    val children: MutableCollection<SpecTreeNode>

    constructor(
        name: String,
        parent: SpecBranch?,
        children: MutableCollection<SpecTreeNode>
    ) {
        this.id = nextId()
        this.name = name
        this.parent = parent
        this.children = children
    }

    fun isRoot() = (parent == null)

    abstract fun isFocused(): Boolean
}

class SpecBranch : SpecTreeNode {
    companion object {
        fun createRoot() = SpecBranch("", null, arrayListOf(), null, null)
    }

    var before: (() -> Unit)?
    var after: (() -> Unit)?

    constructor(
        name: String,
        parent: SpecBranch?,
        children: MutableCollection<SpecTreeNode> = arrayListOf(),
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

    fun addChildBranch(name: String): SpecBranch {
        val newBranch = SpecBranch(name, this)
        children.add(newBranch)
        return newBranch
    }

    fun addChildLeaf(name: String, block: () -> Unit, focused: Boolean = false): SpecLeaf {
        val newLeaf = SpecLeaf(name, this, block, focused = focused)
        children.add(newLeaf)
        return newLeaf
    }
}

class SpecLeaf : SpecTreeNode {

    val block: (() -> Unit)
    val focused: Boolean

    constructor(name: String, parent: SpecBranch, block: (() -> Unit), focused: Boolean) : super(name, parent, arrayListOf()) {
        this.block = block
        this.focused = focused
    }

    override fun isFocused() = focused
}
