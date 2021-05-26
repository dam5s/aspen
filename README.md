# <img src="https://rawgithub.com/dam5s/aspen/master/images/aspen.svg" width="200px" />

Aspen is a JUnit runner for Kotlin. It's inspired by Ruby's RSpec syntax,
the default DSL has a lot more restrictions, but you can create your own DSL if it does not fit your needs.

## Gradle usage

```gradle
dependencies {
    testImplementation "com.github.dam5s.aspen:aspen:2.1.0"
    testImplementation "com.github.dam5s.aspen:aspen-spring:2.1.0"
}

repositories {
    maven { url 'https://jitpack.io' }
}
```

You can also download the JAR files from the release and add them to your path.

## Example

If you were to use AssertJ for assertions.

```kotlin
class CompanyControllerTest: Test({

    val mockRepo = mock(CompanyRepository::class.java)
    val controller = CompanyController(mockRepo)
    
    useRule(MyAwesomeRule())

    before {
        reset(mockRepo)
    }

    describe("#create") {
        test {
            val company = Company(name = "Wayne Enterprises")
            doReturn(company).upon(mockRepo).create(any())

            val response = controller.create("Wayne Ent.")

            assertThat(response).isEqualTo(Response(company, true))
            verify(mockRepo).create("Wayne Ent.")
        }

        test("repository creation error") {
            doReturn(null).upon(mockRepo).create(any())

            val response = controller.create("Wayne Ent.")

            assertThat(response).isEqualTo(Response(null as Company?, false))
        }
    }
})
```

You can focus a test by prefixing changing `test` to `ftest`. Then only this or these tests get run.
For example running the tests above after replacing the second test with

```kotlin
ftest("repository creation error") {
    doReturn(null).upon(mockRepo).create(anyString())

    val response = controller.create("Wayne Ent.")

    assertThat(response).isEqualTo(Response(null as Company?, false))
}
```

Only that second test will get run.

Writing table based tests (a.k.a. Parameterized tests) can be as simple as the following

```kotlin
class ReservationTestExample : Test({

    describe("#amount") {
        class AmountData
        (name: String, val status: Status, val amount: Int) : TestData(name)

        val data = listOf(
            AmountData("when status is OPEN", Status.OPEN, 0),
            AmountData("when status is STARTED", Status.STARTED, 0),
            AmountData("when status is BILLED", Status.BILLED, 100),
            AmountData("when status is PAID", Status.PAID, 100)
        )

        tableTest(data) {
            assertThat(Reservation(status).amount()).isEqualTo(amount)
        }
    }
})
```


## Restrictions

There can only be one `before` block and it has to be at the top level.
`describe` blocks can only contain `test` blocks and they cannot be nested further.
Unlike RSpec, there are no `it` blocks. We are trying to encourage having expressive assertions
that should describe correctly your expectations.

## Why all these restrictions?

The goal of this library is to make your tests a little easier to organize.
But we do believe that the JUnit style is sufficient for testing and
encourages making your test code clear and easy to read, so we are not adding any unnecessary complexity.

## But I want my own DSL!

We tried to make custom Domain Specific Languages easy to write.
You can see an example of a second DSL we created allowing nesting by following these links:

 * [Implementation](https://github.com/dam5s/aspen/blob/master/libraries/aspen/src/main/kotlin/io/damo/aspen/NestedTest.kt)
 * [Usage](https://github.com/dam5s/aspen/blob/master/libraries/aspen/src/test/kotlin/aspen/examples/NestedTestExample.kt)

## Spring Support

Aspen supports Spring!

```kotlin
@RunWith(SpringTestTreeRunner::class)
@SpringApplicationConfiguration(ExampleApplication::class)
@WebIntegrationTest("server.port:0")
class SpringApplicationTest : Test({

    val message: String = inject("myMessage")
    val port = injectValue("local.server.port", Int::class)

    val client = OkHttpClient()

    test("GET /hello") {
        val request = Request.Builder()
            .url("http://localhost:$port/hello")
            .build()

        val response = client.newCall(request).execute()

        val body = response.body().string()
        assertThat(body).isEqualTo("""{"hello":"$message"}""")
    }
})
```

## More examples

You will find actually up-to-date examples that are part of our test suite in the following locations:

 * [Plain Java](https://github.com/dam5s/aspen/tree/master/libraries/aspen/src/test/kotlin/aspen/examples)
 * [Spring Application](https://github.com/dam5s/aspen/tree/master/libraries/aspen-spring/src/test/kotlin/aspen/spring/examples)

## Blog posts

 * [Kotlin testing with Aspen and Aspen Spring](https://medium.com/@its_damo/kotlin-testing-with-aspen-and-aspen-spring-59a3d211a374)
 * [Testing Kotlin with a custom DSL for Aspen](https://medium.com/@its_damo/testing-kotlin-with-a-custom-dsl-for-aspen-ab4b04efe77a)
