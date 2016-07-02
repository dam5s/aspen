package io.damo.aspen


data class Counter(var count: Int = 0) {
    fun next() = ++count
}
