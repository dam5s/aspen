package aspen.examples

import io.damo.aspen.Test

/**
 * Running this test will only run the focused test.
 * Tests can be focused across the entire test structure.
 *
 * If any focused test is encountered, only those will be run.
 */
class FocusedTestExample : Test({
    describe("#something") {
        test {

        }

        ftest("focused") {

        }
    }

    describe("#somethingElse") {
        ftest("focused too") {

        }
    }
})
