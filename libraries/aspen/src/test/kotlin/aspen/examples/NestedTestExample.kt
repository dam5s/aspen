package aspen.examples

import io.damo.aspen.NestedTest
import org.assertj.core.api.Assertions.assertThat

/**
 * We do not want to encourage nesting tests like this,
 * but the NestedTest is a good example of how you can write your own DSL for Aspen.
 */
class NestedTestExample : NestedTest({

    var someString = ""

    before {
        someString = "Hello"
    }

    describe("something") {
        before {
            someString += " World."
        }

        test {

        }

        describe("nested something") {
            before {
                someString += " How is it going?"
            }

            test {
                assertThat(someString).isEqualTo("Hello World. How is it going?")
            }

            test {
                assertThat(someString).isEqualTo("Hello World. How is it going?")
            }

            test("this test will fail in nested something") {
                assertThat(someString).isEqualTo("Hello World. How is it NOT going?")
            }
        }

        describe("nested something else") {
            before {
                someString += " What's up?"
            }

            test {
                assertThat(someString).isEqualTo("Hello World. What's up?")
            }
        }
    }

    describe("something else") {
        before {
            someString += " Kotlin."
        }

        test {
            assertThat(someString).isEqualTo("Hello Kotlin.")
        }

        test("this test will fail in something else") {
            assertThat(someString).isEqualTo("Hello Java.")
        }
    }
})
