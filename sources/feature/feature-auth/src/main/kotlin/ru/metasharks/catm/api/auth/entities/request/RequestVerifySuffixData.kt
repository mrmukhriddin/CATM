package ru.metasharks.catm.api.auth.entities.request

import kotlinx.serialization.Serializable

@Serializable
class RequestVerifySuffixData (
    val key : String
)