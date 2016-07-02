package io.damo.aspen.spring

import io.damo.aspen.TestTree
import org.springframework.core.env.Environment
import kotlin.reflect.KClass


fun TestTree.getAppContext() = SpringTestTreeRunner.appContexts[this]!!

@Suppress("UNCHECKED_CAST")
fun <T> TestTree.inject(name: String): T = getAppContext().getBean(name) as T

fun <T> TestTree.inject(javaClass: Class<T>): T = getAppContext().getBean(javaClass)

fun <T> TestTree.inject(name: String, javaClass: Class<T>): T = getAppContext().getBean(name, javaClass)

fun <T : Any> TestTree.inject(kClass: KClass<T>): T = inject(kClass.java)

fun <T : Any> TestTree.inject(name: String, kClass: KClass<T>): T = inject(name, kClass.java)

fun <T : Any> TestTree.injectValue(name: String, kClass: KClass<T>): T {
    val env = getAppContext().getBean(Environment::class.java)
    return env.getRequiredProperty(name, kClass.java)
}
