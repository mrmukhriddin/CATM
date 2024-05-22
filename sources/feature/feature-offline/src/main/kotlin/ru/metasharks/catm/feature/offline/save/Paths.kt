package ru.metasharks.catm.feature.offline.save

import java.util.StringJoiner

object Paths {

    fun getProfilePathForId(userId: Long, profileId: Long): String {
        return getBaseStringJoiner(userId)
            .add(PATH_PROFILE)
            .add(profileId.toString())
            .toString()
    }

    fun getBriefingPathForId(userId: Long, briefingId: Long): String {
        return getBaseStringJoiner(userId)
            .add(PATH_BRIEFINGS)
            .add(briefingId.toString())
            .toString()
    }

    fun getWorkPermitPathForId(userId: Long, workPermitId: Long): String {
        return getBaseStringJoiner(userId)
            .add(PATH_WORK_PERMITS)
            .add(workPermitId.toString())
            .toString()
    }

    fun getWorkPermitBuffer(userId: Long): String {
        return getBaseStringJoiner(userId)
            .add(PATH_WORK_PERMITS)
            .add(PATH_BUFFER)
            .toString()
    }

    fun getPathForUser(userId: Long): String {
        return getBaseStringJoiner(userId).toString()
    }

    private fun getBaseStringJoiner(userId: Long): StringJoiner {
        return StringJoiner("/")
            .add(userId.toString())
    }

    const val PATH_PROFILE = "profile"
    const val PATH_WORK_PERMITS = "work_permits"
    const val PATH_BRIEFINGS = "briefings"

    const val PATH_BUFFER = "buffer"
}
