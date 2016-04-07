package kspec.examples

import io.damo.kspec.Spec

/**
 * Running this spec will only run the focused test.
 * Tests can be focused across the entire spec structure.
 *
 * If any focused test is encountered, only those will be run.
 */
class FocusedSpec : Spec({
    describe("#something") {
        test {

        }

        ftest("focused") {

        }
    }
})
