package io.damo.kspec


data class Counter(var count: Int = 0) {
    fun next() = ++count
}
