package kspec.examples

import io.damo.kspec.NestedSpec
import org.junit.Assert.assertThat
import org.hamcrest.Matchers.equalTo

class NestedSpecExample: NestedSpec({

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
                assertThat(someString, equalTo("Hello World. How is it going?"))
            }

            test {
                assertThat(someString, equalTo("Hello World. How is it going?"))
            }

            test("this test will fail in nested something") {
                assertThat(someString, equalTo("Hello World. How is it NOT going?"))
            }
        }

        describe("nested something else") {
            before {
                someString += " What's up?"
            }

            test {
                assertThat(someString, equalTo("Hello World. What's up?"))
            }
        }
    }

    describe("something else") {
        before {
            someString += " Kotlin."
        }

        test {
            assertThat(someString, equalTo("Hello Kotlin."))
        }

        test("this test will fail in something else") {
            assertThat(someString, equalTo("Hello Java."))
        }
    }
})
