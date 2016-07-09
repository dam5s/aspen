package aspen.examples

import io.damo.aspen.Test
import io.damo.aspen.TestData
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo

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
        test("it ${running.shouldOrShouldNot()} be running") {
            assertThat(hero.isRunning, equalTo(running))
        }

        test("it ${jumping.shouldOrShouldNot()} be running") {
            assertThat(hero.isJumping, equalTo(jumping))
        }

        test("it ${standing.shouldOrShouldNot()} be running") {
            assertThat(hero.isStanding, equalTo(standing))
        }
    }
})


fun Boolean.shouldOrShouldNot() = if (this) "should" else "shouldn't"

class Hero() {
    var isRunning = false
        private set

    var isJumping = false
        private set

    var isStanding = true
        private set

    fun run() {
    }

    fun stand() {
    }

    fun jump() {
    }
}
