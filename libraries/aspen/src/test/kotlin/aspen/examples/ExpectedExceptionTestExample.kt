package aspen.examples

import io.damo.aspen.Test
import io.damo.aspen.expectException

/**
 * With usual JUnit runners we would test exception using ExpectedException Rule,
 * or we would use a try catch and write the test manually.
 *
 * The following tests can be applied to any DSL.
 */

class ExceptionThrowingServiceTestExample : Test({

    test("with correct class and message") {
        val service = ExceptionThrowingService()

        expectException(IllegalStateException::class, "this is going badly.") {
            service.failAtSomething()
        }
    }

    test("with correct class, no message") {
        val service = ExceptionThrowingService()

        expectException(IllegalStateException::class) {
            service.failAtSomething()
        }
    }

    test("with correct parent class") {
        val service = ExceptionThrowingService()

        expectException(RuntimeException::class) {
            service.failAtSomething()
        }
    }

    test("with correct message") {
        val service = ExceptionThrowingService()

        expectException("this is going badly.") {
            service.failAtSomething()
        }
    }

    test("with incorrect class") {
        val service = ExceptionThrowingService()

        expectException(IllegalArgumentException::class, "this is going badly.") {
            service.failAtSomething()
        }
    }

    test("with incorrect message") {
        val service = ExceptionThrowingService()

        expectException("this is going great.") {
            service.failAtSomething()
        }
    }
})

class ExceptionThrowingService {
    fun failAtSomething() {
        throw IllegalStateException("this is going badly.")
    }
}
