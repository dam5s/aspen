package aspen.examples

import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

class TestUsingTestRulesExample : Test({

    val outputs = arrayListOf("Init")

    useRule(SomeTestRule(outputs, "Rule 1"))
    useRule(SomeTestRule(outputs, "Rule 2"))

    before {
        outputs.add("Before")
    }

    test {
        outputs.add("Test start")

        assertThat(outputs).isEqualTo(listOf(
            "Init",
            "Rule 2 start",
            "Rule 1 start",
            "Before",
            "Test start"
        ))
    }
})

class SomeTestRule(val outputs: MutableList<String>, val prefix: String) : TestRule {

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                outputs.add("$prefix start")
                base.evaluate()
                outputs.add("$prefix end")
            }
        }
    }
}
