package io.damo.aspen

import org.junit.runner.Description
import org.junit.runner.Description.createSuiteDescription
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import org.junit.runners.model.MultipleFailureException
import java.util.*


open class TestTreeRunner<T : TestTree> : ParentRunner<TestTreeNodeRunner> {

    val testTree by lazy { testTreeClass.newInstance() }
    val testTreeClass: Class<T>

    private val _children by lazy { buildChildren() }

    constructor(testTreeClass: Class<T>) : super(testTreeClass) {
        this.testTreeClass = testTreeClass
    }


    override fun getChildren() = _children

    override fun runChild(child: TestTreeNodeRunner, notifier: RunNotifier) {
        junitAction(describeChild(child), notifier) {
            child.run(notifier)
        }
    }

    override fun describeChild(child: TestTreeNodeRunner) = child.getDescription()

    open fun buildChildren(): MutableList<TestTreeNodeRunner> {

        testTree.readTestBody()

        val root = testTree.getRoot()
        val runFocusedOnly = root.isFocused()
        val rootBranchRunner = TestBranchRunner(testTreeClass, root, runFocusedOnly)

        return arrayListOf(rootBranchRunner)
    }
}


interface TestTreeNodeRunner {
    fun getDescription(): Description
    fun run(notifier: RunNotifier)
}


class TestBranchRunner<T : TestTree> : TestTreeNodeRunner, ParentRunner<TestTreeNodeRunner> {

    val testTreeNode: TestTreeNode
    val testTreeClass: Class<T>
    val runFocusedOnly: Boolean

    private val description: Description
    private val children: MutableList<TestTreeNodeRunner>

    constructor(testTreeClass: Class<T>, testTreeNode: TestTreeNode, runFocusedOnly: Boolean) : super(testTreeClass) {
        this.testTreeClass = testTreeClass
        this.testTreeNode = testTreeNode
        this.runFocusedOnly = runFocusedOnly

        this.children = buildChildren()
        this.description = buildDescription()
    }

    override fun getName(): String {
        if (testTreeNode.isRoot())
            return super.getName()

        return testTreeNode.name
    }

    override fun getChildren() = children

    override fun getDescription() = description

    override fun describeChild(child: TestTreeNodeRunner) = child.getDescription()

    override fun runChild(child: TestTreeNodeRunner, notifier: RunNotifier) {
        junitAction(describeChild(child), notifier) {
            child.run(notifier)
        }
    }

    private fun buildChildren(): MutableList<TestTreeNodeRunner> {
        return getFilteredNodeChildren().map {
            when (it) {
                is TestBranch -> TestBranchRunner(testTreeClass, it, runFocusedOnly)
                is TestLeaf -> TestLeafRunner(it)
                else -> throw RuntimeException("Encountered unexpected TestTreeNode type $it")
            }

        }.toMutableList()
    }

    private fun getFilteredNodeChildren(): Collection<TestTreeNode> {
        var nodeChildren: Collection<TestTreeNode> = testTreeNode.children

        if (runFocusedOnly) {
            nodeChildren = nodeChildren.filter { it.isFocused() }
        }

        return nodeChildren
    }

    private fun buildDescription(): Description {
        if (testTreeNode.isRoot())
            return super.getDescription()

        val desc = createSuiteDescription(name, testTreeNode.id)!!
        children.forEach {
            desc.addChild(describeChild(it))
        }

        return desc
    }
}


class TestLeafRunner(val testLeaf: TestLeaf) : TestTreeNodeRunner {

    override fun getDescription(): Description {
        return createSuiteDescription(testLeaf.testName, testLeaf.id)!!
    }

    override fun run(notifier: RunNotifier) {
        val (befores, afters) = testLeaf.collectBeforesAndAfters()
        val errors = ArrayList<Throwable>()

        try {
            befores.forEach {
                it.invoke()
            }
            testLeaf.block.invoke()

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
