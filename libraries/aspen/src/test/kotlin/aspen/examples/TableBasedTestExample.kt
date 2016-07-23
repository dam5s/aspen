package aspen.examples

import io.damo.aspen.Test
import io.damo.aspen.TestData
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat

/**
 * It is very easy to write table based tests using the DSL.
 * This is similar to JUnit's Parameterized tests.
 */
class ReservationTestExample : Test({

    describe("#amount - table based") {
        val statusesWithoutAmount = arrayOf(Status.OPEN, Status.STARTED)
        val statusesWithAmount = arrayOf(Status.BILLED, Status.PAID)

        statusesWithoutAmount.forEach { status ->
            test("when status is $status") {
                assertThat(Reservation(status).amount(), equalTo(0))
            }
        }

        statusesWithAmount.forEach { status ->
            test("when status is $status") {
                assertThat(Reservation(status).amount(), equalTo(100))
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

            test("when status is $status") {
                assertThat(Reservation(status).amount(), equalTo(expectedAmount))
            }
        }
    }

    describe("#amount - test data based") {
        class AmountData
        (name: String, val status: Status, val amount: Int) : TestData(name)

        val data = listOf(
            AmountData("when status is OPEN", Status.OPEN, 0),
            AmountData("when status is STARTED", Status.STARTED, 0),
            AmountData("when status is BILLED", Status.BILLED, 100),
            AmountData("when status is PAID", Status.PAID, 100)
        )

        tableTest(data) {
            assertThat(Reservation(status).amount(), equalTo(amount))
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

