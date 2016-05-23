package androidexample

import io.damo.kspec.Spec
import io.damo.kspec.androidexample.MainActivity
import io.damo.kspec.robolectric.RobolectricSpecTreeRunner
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.equalTo
import org.junit.runner.RunWith
import org.robolectric.Robolectric

@RunWith(RobolectricSpecTreeRunner::class)
class ActivitySpec : Spec({

    test {
        val activity = Robolectric.setupActivity(MainActivity::class.java)

        assertThat(activity.title.toString(), equalTo("Hello World!"))
    }
})
