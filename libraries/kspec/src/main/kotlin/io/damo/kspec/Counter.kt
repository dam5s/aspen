package io.damo.kspec


class Counter {
    private var unnamedTests = 0
    internal fun nextUnnamedTestNumber() = ++unnamedTests
}
