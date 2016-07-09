package aspen.examples

import io.damo.aspen.Test
import io.damo.aspen.TestData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

class HeroTest : Test({


    class HeroStanceData(
        context: String,
        val running: Boolean,
        val jumping: Boolean,
        val standing: Boolean,
        val hero: Hero
    ): TestData(context)

    val testData = listOf(
        HeroStanceData("initial state", false, false, true, Hero()),
        HeroStanceData("when running", false, false, true, Hero().apply { run() }),
        HeroStanceData("when jumping", false, false, true, Hero().apply { jump() }),
        HeroStanceData("when standing", false, false, true, Hero().apply { stand() })
    )

    tableTest(testData) {
        fun testLabel(flag: Boolean, action: String)
            = "it ${flag.shouldOrNot()} be $action"

        test(testLabel(running, "running")) {
            assertThat(hero.isRunning, equalTo(running))
        }

        test(testLabel(jumping, "jumping")) {
            assertThat(hero.isJumping, equalTo(jumping))
        }

        test(testLabel(standing, "standing")) {
            assertThat(hero.isStanding, equalTo(standing))
        }
    }
})


fun Boolean.shouldOrNot() = if (this) "should" else "shouldn't"

class Hero() {
    val isRunning: Boolean get() = false
    val isJumping: Boolean get() = false
    val isStanding: Boolean get() = true

    fun run() {
    }

    fun stand() {
    }

    fun jump() {
    }
}
