package aspen.examples

import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat


/**
 * If your class only has one function,
 * you probably do not want to use a #describe block.
 */
class RunnableTestExample : Test({
    test {
        val runnable = MyRunnable()
        assertThat(runnable.value).isEqualTo(1)

        runnable.run()
        assertThat(runnable.value).isEqualTo(2)

        runnable.run()
        assertThat(runnable.value).isEqualTo(3)
    }
})

class MyRunnable : Runnable {

    var value = 1
        private set

    override fun run() {
        value++
    }
}
