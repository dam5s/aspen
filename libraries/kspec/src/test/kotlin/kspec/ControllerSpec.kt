package kspec

import io.damo.kspec.Spec
import org.hamcrest.Matchers.equalTo
import org.junit.Assert.assertThat
import org.mockito.Mockito.*

class CompanyControllerSpec: Spec({

    val mockRepo = mock(CompanyRepository::class.java)
    val controller = CompanyController(mockRepo)

    after {
        reset(mockRepo)
    }

    describe("#create") {
        test {
            val company = Company(name = "Wayne Enterprises")
            doReturn(company).`when`(mockRepo).create(anyString())

            val response = controller.create("Wayne Ent.")

            assertThat(response, equalTo(Response(company, true)))
            verify(mockRepo).create("Wayne Ent.")
        }

        test("repository creation error") {
            doReturn(null).`when`(mockRepo).create(anyString())

            val response = controller.create("Wayne Ent.")

            assertThat(response, equalTo(Response(null as Company?, false)))
        }
    }
})

class CompanyController(val repository: CompanyRepository) {
    fun create(companyName: String) = Response(repository.create(companyName), true)
}

interface CompanyRepository {
    fun create(companyName: String): Company?
}

data class Response<T>(val value: T?, val success: Boolean)

data class Company(val name: String)
