package aspen.examples

import io.damo.aspen.Test
import org.assertj.core.api.Assertions.assertThat



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
            assertThat(person.fullName()).isEqualTo("Jane Doe")
        }

        test("with a middle name") {
            val person = buildPerson(
                firstName = "John",
                middleName = "William",
                lastName = "Doe"
            )
            assertThat(person.fullName()).isEqualTo("John W. Doe")
        }
    }

    describe("#greeting") {
        test {
            val person = buildPerson(
                firstName = "Jane",
                lastName = "Doe"
            )
            assertThat(person.greeting()).isEqualTo("Greetings Jane!")
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
