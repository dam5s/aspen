package io.damo.kspec.spring

import io.damo.kspec.SpecTree
import javax.transaction.Transactional

@Transactional
class SpringTransactionalSpecTreeRunner<T : SpecTree>(specificationClass: Class<T>) : SpringSpecTreeRunner<T>(specificationClass) {

    // This will allow for finding the @Transactional annotation.
    override val testMethod = SpringTransactionalSpecTreeRunner::class.java.methods[0]
}
