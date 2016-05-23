package androidexample

import io.damo.kspec.androidexample.BuildConfig
import io.damo.kspec.androidexample.MainActivity
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric.*
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.*

@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class)
class ActivityTest {

    @Test
    fun testCreation() {
        val activity = setupActivity(MainActivity::class.java)

        assertThat(activity.title.toString(), equalTo("Hello World!"))
    }
}

