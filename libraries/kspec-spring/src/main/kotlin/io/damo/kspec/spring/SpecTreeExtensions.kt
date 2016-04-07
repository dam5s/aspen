package io.damo.kspec.spring

import io.damo.kspec.SpecTree
import org.springframework.core.env.Environment
import kotlin.reflect.KClass


fun SpecTree.getAppContext() = SpringSpecTreeRunner.appContexts[this]!!

@Suppress("UNCHECKED_CAST")
fun <T> SpecTree.inject(name: String): T = getAppContext().getBean(name) as T

fun <T> SpecTree.inject(javaClass: Class<T>): T = getAppContext().getBean(javaClass)

fun <T> SpecTree.inject(name: String, javaClass: Class<T>): T = getAppContext().getBean(name, javaClass)

fun <T : Any> SpecTree.inject(kClass: KClass<T>): T = inject(kClass.java)

fun <T : Any> SpecTree.inject(name: String, kClass: KClass<T>): T = inject(name, kClass.java)

fun <T : Any> SpecTree.injectValue(name: String, kClass: KClass<T>): T {
    val env = getAppContext().getBean(Environment::class.java)
    return env.getRequiredProperty(name, kClass.java)
}
