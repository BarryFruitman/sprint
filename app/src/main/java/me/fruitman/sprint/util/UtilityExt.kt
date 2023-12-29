package me.fruitman.sprint.util

fun <T> List<T>.replacedWhere(replaceWith: T, predicate: (T) -> Boolean): List<T> =
    map { if (predicate.invoke(it)) replaceWith else it}
