package io.damo.aspen

import org.junit.runner.Description
import org.junit.runner.Description.createSuiteDescription
import org.junit.runner.Description.createTestDescription
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import org.junit.runners.model.MultipleFailureException
import org.junit.runners.model.Statement
import java.util.*


open class TestTreeRunner<T : TestTree>(val testTreeClass: Class<T>)
    : ParentRunner<TestTreeNodeRunner>(testTreeClass) {


    val testTree by lazy { testTreeClass.newInstance() }
    private val _children by lazy { buildChildren() }


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


class TestBranchRunner<T : TestTree>(val testTreeClass: Class<T>, val testTreeNode: TestTreeNode, val runFocusedOnly: Boolean)
    : TestTreeNodeRunner, ParentRunner<TestTreeNodeRunner>(testTreeClass) {

    private val children = buildChildren()
    private val description = buildDescription()


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
        return createTestDescription(testLeaf.rootName(), testLeaf.testName, testLeaf.id)!!
    }

    override fun run(notifier: RunNotifier) {
        val description = getDescription()
        var statement = buildStatement()

        for (rule in testLeaf.allRules()) {
            statement = rule.apply(statement, description)
        }

        statement.evaluate()
    }


    private fun buildStatement(): Statement {
        val (befores, afters) = testLeaf.collectBeforesAndAfters()
        val errors = ArrayList<Throwable>()

        return object : Statement() {
            override fun evaluate() {
                try {
                    befores.forEach { it.invoke() }
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
