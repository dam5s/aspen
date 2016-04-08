package io.damo.kspec

import org.junit.runner.Description
import org.junit.runner.Description.createSuiteDescription
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import org.junit.runners.model.MultipleFailureException
import java.util.*


open class SpecTreeRunner<T : SpecTree> : ParentRunner<SpecTreeNodeRunner> {

    val specTree by lazy { specTreeClass.newInstance() }
    val specTreeClass: Class<T>

    private val _children by lazy { buildChildren() }

    constructor(specTreeClass: Class<T>) : super(specTreeClass) {
        this.specTreeClass = specTreeClass
    }


    override fun getChildren() = _children

    override fun runChild(child: SpecTreeNodeRunner, notifier: RunNotifier) {
        junitAction(describeChild(child), notifier) {
            child.run(notifier)
        }
    }

    override fun describeChild(child: SpecTreeNodeRunner) = child.getDescription()

    open fun buildChildren(): MutableList<SpecTreeNodeRunner> {

        specTree.readSpecBody()

        val root = specTree.getRoot()
        val runFocusedOnly = root.isFocused()
        val rootBranchRunner = SpecBranchRunner(specTreeClass, root, runFocusedOnly)

        return arrayListOf(rootBranchRunner)
    }
}


interface SpecTreeNodeRunner {
    fun getDescription(): Description
    fun run(notifier: RunNotifier)
}


class SpecBranchRunner<T : SpecTree> : SpecTreeNodeRunner, ParentRunner<SpecTreeNodeRunner> {

    val specTreeNode: SpecTreeNode
    val specTreeClass: Class<T>
    val runFocusedOnly: Boolean

    private val description: Description
    private val children: MutableList<SpecTreeNodeRunner>

    constructor(specTreeClass: Class<T>, specTreeNode: SpecTreeNode, runFocusedOnly: Boolean) : super(specTreeClass) {
        this.specTreeClass = specTreeClass
        this.specTreeNode = specTreeNode
        this.runFocusedOnly = runFocusedOnly

        this.children = buildChildren()
        this.description = buildDescription()
    }

    override fun getName(): String {
        if (specTreeNode.isRoot())
            return super.getName()

        return specTreeNode.name
    }

    override fun getChildren() = children

    override fun getDescription() = description

    override fun describeChild(child: SpecTreeNodeRunner) = child.getDescription()

    override fun runChild(child: SpecTreeNodeRunner, notifier: RunNotifier) {
        junitAction(describeChild(child), notifier) {
            child.run(notifier)
        }
    }

    private fun buildChildren(): MutableList<SpecTreeNodeRunner> {
        return getFilteredNodeChildren().map {
            when (it) {
                is SpecBranch -> SpecBranchRunner(specTreeClass, it, runFocusedOnly)
                is SpecLeaf -> SpecLeafRunner(it)
                else -> throw RuntimeException("Encountered unexpected SpecTreeNode type $it")
            }

        }.toMutableList()
    }

    private fun getFilteredNodeChildren(): Collection<SpecTreeNode> {
        var nodeChildren: Collection<SpecTreeNode> = specTreeNode.children

        if (runFocusedOnly) {
            nodeChildren = nodeChildren.filter { it.isFocused() }
        }

        return nodeChildren
    }

    private fun buildDescription(): Description {
        if (specTreeNode.isRoot())
            return super.getDescription()

        val desc = createSuiteDescription(name, specTreeNode.id)!!
        children.forEach {
            desc.addChild(describeChild(it))
        }

        return desc
    }
}


class SpecLeafRunner(val specLeaf: SpecLeaf) : SpecTreeNodeRunner {

    override fun getDescription(): Description {
        return createSuiteDescription(specLeaf.testName, specLeaf.id)!!
    }

    override fun run(notifier: RunNotifier) {
        val (befores, afters) = specLeaf.collectBeforesAndAfters()
        val errors = ArrayList<Throwable>()

        try {
            befores.forEach {
                it.invoke()
            }
            specLeaf.block.invoke()

        } finally {
            afters.forEach {
                try {
                    it.invoke()
                } catch(e: Throwable) {
                    errors.add(e)
                }
            }

            MultipleFailureException.assertEmpty(errors)
        }
    }
}


fun junitAction(description: Description, notifier: RunNotifier, action: () -> Unit) {
    if (description.isTest)
        notifier.fireTestStarted(description)

    try {
        action()
    } catch(e: Throwable) {
        notifier.fireTestFailure(Failure(description, e))
    } finally {
        if (description.isTest) notifier.fireTestFinished(description)
    }
}
