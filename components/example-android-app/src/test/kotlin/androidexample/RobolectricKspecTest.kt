package androidexample

import org.junit.Test
import org.hamcrest.Matchers.equalTo
import org.hamcrest.MatcherAssert.assertThat

class RobolectricKspecTest {

    @Test
    fun test() {
        assertThat(true, equalTo(true))
    }
}
