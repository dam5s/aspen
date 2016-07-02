package aspen.examples

import io.damo.aspen.Test
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.mockito.Mockito.*

/**
 * This example illustrates testing with mocks
 * and stateless service type of objects.
 */
class CompanyControllerTestExample : Test({

    // Beware here, these variables are only instantiated once for all the tests.
    // You should avoid mutable/stateful variables here to prevent tests pollution.
    val mockRepo = mock(CompanyRepository::class.java)
    val controller = CompanyController(mockRepo)

    after {
        // Ensure the mock is reset between tests
        reset(mockRepo)
    }

    describe("#create") {
        test {
            val company = Company(name = "Wayne Enterprises")
            doReturn(company).`when`(mockRepo).create(anyString())

            val response = controller.create("Wayne Ent.")

            assertThat(response, equalTo(CompanyResponse(company, true)))
            verify(mockRepo).create("Wayne Ent.")
        }

        test("repository creation error") {
            doReturn(null).`when`(mockRepo).create(anyString())

            val response = controller.create("Wayne Ent.")

            assertThat(response, equalTo(CompanyResponse(null as Company?, false)))
        }
    }
})


class CompanyController(val repository: CompanyRepository) {
    fun create(companyName: String) = CompanyResponse(repository.create(companyName), true)
}

interface CompanyRepository {
    fun create(companyName: String): Company?
}

data class CompanyResponse<T>(val value: T?, val success: Boolean)

data class Company(val name: String)
