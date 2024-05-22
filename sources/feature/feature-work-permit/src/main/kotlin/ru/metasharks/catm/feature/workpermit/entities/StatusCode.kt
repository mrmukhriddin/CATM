package ru.metasharks.catm.feature.workpermit.entities

import ru.metasharks.catm.feature.workpermit.entities.statuses.StatusX

enum class StatusCode(val code: String) {

    NEW("new"), PENDING("pending"), SIGNED("signed"), ARCHIVED("archived"), ALL(StatusX.ALL)
}
