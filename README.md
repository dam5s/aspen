# KSpec

KSpec is a JUnit runner for Kotlin. It's inspired by Ruby's RSpec syntax,
but it has a lot more restrictions and some features have been purposefully omitted.

## Example

If you were to use Hamcrest for assertions.

```
class CompanyControllerSpec: Spec({

    val mockRepo = mock(CompanyRepository::class.java)
    val controller = CompanyController(mockRepo)

    before {
        reset(mockRepo)
    }

    describe("#create") {
        context {
            val company = Company(name = "Wayne Enterprises")
            doReturn(company).`when`(mockRepo).create(anyString())

            val response = controller.create("Wayne Ent.")

            assertThat(response, equalTo(Response(company, true)))
            verify(mockRepo).create("Wayne Ent.")
        }

        context("repository creation error") {
            doReturn(null).`when`(mockRepo).create(anyString())

            val response = controller.create("Wayne Ent.")

            assertThat(response, equalTo(Response(null as Company?, false)))
        }
    }
})
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
