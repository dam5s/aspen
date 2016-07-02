package io.damo.aspen

import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import kotlin.reflect.KClass

fun <T : Throwable> expectException(expectedClass: KClass<T>, message: String? = null, block: () -> Unit) {
    try {
        block()
        fail("Expected to throw an exception of type ($expectedClass), but none was thrown.")

    } catch (exception: Throwable) {
        val expectedJavaClass = expectedClass.java
        val actualJavaClass = exception.javaClass

        if (!expectedJavaClass.isAssignableFrom(actualJavaClass)) {
            fail("Expected to throw an exception of type ($expectedJavaClass), but ($exception) was thrown.")
        }

        message?.let {
            assertEquals("Exception message does not match expected", message, exception.message)
        }
    }
}

fun expectException(message: String, block: () -> Unit) {
    try {
        block()
        fail("Expected to throw an exception, but none was thrown.")
    } catch (exception: Throwable) {
        assertEquals("Exception message does not match expected", message, exception.message)
    }
}
