package io.damo.kspec

import org.junit.runner.Description
import org.junit.runner.notification.Failure
import org.junit.runner.notification.RunNotifier
import org.junit.runners.ParentRunner
import java.io.Serializable

class JUnitDescriptionRunner<T>(
    val specificationClass: Class<T>,
    val specDescription: SpecDescription,
    val beforeBlock: (() -> Unit)?,
    val afterBlock: (() -> Unit)?
) : ParentRunner<Test>(specificationClass) {

    override fun getChildren(): List<Test> = specDescription.testsToRun

    override fun runChild(child: Test, notifier: RunNotifier) {
        junitAction(describeChild(child), notifier) { child.run(beforeBlock, afterBlock) }
    }

    override fun describeChild(child: Test) =
        Description.createSuiteDescription("${child.name} (${specDescription.name})", JUnitUniqueId.next())

    override fun getDescription(): Description {
        val desc = Description.createSuiteDescription(specDescription.name, JUnitUniqueId.next())!!
        for (item in children) {
            desc.addChild(describeChild(item))
        }
        return desc
    }
}

class JUnitClassRunner<T>(val specificationClass: Class<T>): ParentRunner<JUnitDescriptionRunner<T>>(specificationClass) {
    override fun getChildren(): MutableList<JUnitDescriptionRunner<T>> {
        if (Spec::class.java.isAssignableFrom(specificationClass) && !specificationClass.isLocalClass) {
            val spec = specificationClass.newInstance() as Spec

            return spec.descriptions
                .map { JUnitDescriptionRunner(specificationClass, it, spec.beforeBlock, spec.afterBlock) }
                .toMutableList()
        }

        return arrayListOf()
    }

    override fun runChild(child: JUnitDescriptionRunner<T>, notifier: RunNotifier) {
        junitAction(describeChild(child), notifier) {
            child.run(notifier)
        }
    }

    override fun describeChild(child: JUnitDescriptionRunner<T>) = child.description
}

data class JUnitUniqueId(val id: Int) : Serializable {
    companion object {
        private var id = 0
        fun next() = JUnitUniqueId(id++)
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
