package kspec.examples

import io.damo.kspec.Spec
import org.junit.Assert.assertThat
import org.hamcrest.Matchers.equalTo

/**
 * It is very easy to write table based tests using the DSL.
 * This is similar to JUnit's Parameterized tests.
 */
class ReservationSpec: Spec({

    describe("#amount - table based") {
        val statusesWithoutAmount = arrayOf(Status.OPEN, Status.STARTED)
        val statusesWithAmount = arrayOf(Status.BILLED, Status.PAID)

        statusesWithoutAmount.forEach { status ->
            test("when the status is $status") {
                val reservation = Reservation(status)
                assertThat(reservation.amount(), equalTo(0))
            }
        }

        statusesWithAmount.forEach { status ->
            test("when the status is $status") {
                val reservation = Reservation(status)
                assertThat(reservation.amount(), equalTo(100))
            }
        }
    }

    val expectedAmountsForStatus = mapOf(
        Status.OPEN to 0,
        Status.STARTED to 0,
        Status.BILLED to 100,
        Status.PAID to 100
    )

    describe("#amount - map based") {
        expectedAmountsForStatus.forEach { entry ->
            val status = entry.key
            val expectedAmount = entry.value

            test("when the status is $status") {
                val reservation = Reservation(status)
                assertThat(reservation.amount(), equalTo(expectedAmount))
            }
        }
    }
})

data class Reservation(val status: Status) {
    fun amount(): Int {
        return when (status) {
            Status.OPEN, Status.STARTED -> 0
            Status.BILLED, Status.PAID -> 100
        }
    }
}

enum class Status {
    OPEN, STARTED, BILLED, PAID
}


