package io.damo.aspen.spring

import io.damo.aspen.TestTree
import javax.transaction.Transactional

@Transactional
class SpringTransactionalTestTreeRunner<T : TestTree>(testClass: Class<T>) : SpringTestTreeRunner<T>(testClass) {

    // This will allow for finding the @Transactional annotation.
    override val testMethod = SpringTransactionalTestTreeRunner::class.java.methods[0]
}
