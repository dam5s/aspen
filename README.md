# KSpec

KSpec is a JUnit runner for Kotlin. It's inspired by Ruby's RSpec syntax,
but it has a lot more restrictions and some features have been purposefully omitted.

## Example

If you were to use Hamcrest for assertions.

```
class PersonSpec: Spec() {
    lateinit var person: Person

    init {
        before {
            person = buildPerson(firstName = "Jane", lastName = "Doe")
        }

        describe("#fullName") {
            context {
                assertThat(person.fullName(), equalTo("Jane Doe"))
            }

            context("with a middle name") {
                person = buildPerson(
                    firstName = "John",
                    middleName = "William",
                    lastName = "Doe"
                )

                assertThat(person.fullName(), equalTo("John W. Doe"))
            }
        }

        describe("#greeting") {
            context {
                assertThat(person.greeting(), equalTo("Greetings Jane!"))
            }
        }
    }
}
```

## Restrictions

There can only be one `before` block and it has to be at the top level.
`Describe` blocks can only contain `context` blocks and they cannot be nested further.
Unlike RSpec, there are no `it` blocks. We are trying to encourage having expressive assertions
that should describe correctly your expectations.

## Why all these restrictions?

The goal of this library is to make your tests a little easier to organize.
But we do believe that the JUnit style is sufficient for testing and
encourages making your test code clear and easy to read, so we are not adding any unnecessary complexity.
