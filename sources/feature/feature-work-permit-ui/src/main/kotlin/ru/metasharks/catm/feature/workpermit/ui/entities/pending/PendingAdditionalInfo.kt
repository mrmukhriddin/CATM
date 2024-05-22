package ru.metasharks.catm.feature.workpermit.ui.entities.pending

import ru.metasharks.catm.feature.workpermit.ui.entities.AdditionalInfo
import ru.metasharks.catm.feature.workpermit.entities.SignerRole

data class PendingAdditionalInfo(
    val rolesList: List<Pair<SignerRole, Boolean?>>,
    val anyRejected: Boolean,
) : AdditionalInfo
