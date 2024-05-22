package ru.metasharks.catm.step

/**
 * T - тип изначальных данных. Изначальные данные - это данные, которые могут быть проставлены
 * этому полю при инициализации
 */
abstract class BaseInput<T>(
    open val initialValue: T?
)
