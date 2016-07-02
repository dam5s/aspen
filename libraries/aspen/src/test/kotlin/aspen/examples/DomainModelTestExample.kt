package aspen.examples

import io.damo.aspen.Test
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat


/**
 * This is probably the most basic example.
 */
class PersonTestExample : Test({
    describe("#fullName") {
        test {
            val person = buildPerson(
                firstName = "Jane",
                lastName = "Doe"
            )
            assertThat(person.fullName(), equalTo("Jane Doe"))
        }

        test("with a middle name") {
            val person = buildPerson(
                firstName = "John",
                middleName = "William",
                lastName = "Doe"
            )
            assertThat(person.fullName(), equalTo("John W. Doe"))
        }
    }

    describe("#greeting") {
        test {
            val person = buildPerson(
                firstName = "Jane",
                lastName = "Doe"
            )
            assertThat(person.greeting(), equalTo("Greetings Jane!"))
        }
    }
})

fun buildPerson(
    firstName: String = "John",
    lastName: String = "Doe",
    middleName: String? = null
) = Person(firstName, lastName, middleName)

data class Person(
    val firstName: String,
    val lastName: String,
    val middleName: String? = null
)

fun Person.fullName() = "$firstName $lastName"

fun Person.greeting() = "Greetings $firstName!"
