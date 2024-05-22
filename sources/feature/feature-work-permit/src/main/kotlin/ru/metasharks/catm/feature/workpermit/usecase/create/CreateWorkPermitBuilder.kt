package ru.metasharks.catm.feature.workpermit.usecase.create

import android.net.Uri
import android.webkit.MimeTypeMap
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.workpermit.entities.create.CreateWorkPermitInfo
import java.io.File

class CreateWorkPermitBuilder(val fileManager: FileManager) {

    private val multipartBody = MultipartBody.Builder()

    fun putString(name: String, value: String): CreateWorkPermitBuilder {
        val part = MultipartBody.Part.createFormData(name, value)
        multipartBody.addPart(part)
        return this
    }

    fun putInt(name: String, value: Int): CreateWorkPermitBuilder {
        return putString(name, value.toString())
    }

    fun putLong(name: String, value: Long): CreateWorkPermitBuilder {
        return putString(name, value.toString())
    }

    fun putFile(name: String, value: File): CreateWorkPermitBuilder {
        val fileExtension = value.extension
        val mimeType =
            requireNotNull(MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension))
        val filePart = MultipartBody.Part.createFormData(
            name,
            value.name,
            value.asRequestBody(mimeType.toMediaType())
        )
        multipartBody.addPart(filePart)
        return this
    }

    fun <T> putList(name: String, value: List<T>): CreateWorkPermitBuilder {
        if (value.isEmpty()) {
            return this
        }
        when (value.first()) {
            is Int -> {
                value.forEach { integer ->
                    putInt(name, integer as Int)
                }
            }
            is Long -> {
                value.forEach { long ->
                    putLong(name, long as Long)
                }
            }
            is String -> {
                value.forEach { string ->
                    putString(name, string as String)
                }
            }
        }
        return this
    }

    fun putCreateInfo(createWorkPermitInfo: CreateWorkPermitInfo): CreateWorkPermitBuilder {
        putString(ORGANIZATION, createWorkPermitInfo.organization)
        putString(PLACE, createWorkPermitInfo.place)
        putString(START_TIME, createWorkPermitInfo.startTime)
        putString(EXPIRATION_TIME, createWorkPermitInfo.expirationTime)
        putString(SHIFT, createWorkPermitInfo.shift)
        putLong(
            RESPONSIBLE_MANAGER,
            createWorkPermitInfo.responsibleManagerId
        )
        putLong(
            RESPONSIBLE_EXECUTOR,
            createWorkPermitInfo.responsibleExecutorId
        )
        putLong(PERMIT_ISSUER, createWorkPermitInfo.permitIssuerId)
        putLong(PERMIT_ACCEPTOR, createWorkPermitInfo.permitAcceptorId)
        putInt(WORK_TYPE, createWorkPermitInfo.workTypeId)
        putList(
            DANGEROUS_FACTORS,
            createWorkPermitInfo.chosenDangerousFactors
        )
        putList(ANOTHER_FACTORS, createWorkPermitInfo.chosenAnotherFactors)
        putList(SAVE_EQUIPMENT, createWorkPermitInfo.chosenSaveEquipment)
        putList(USED_EQUIPMENT, createWorkPermitInfo.chosenUsedEquipment)
        putList(WORK_SCHEME, createWorkPermitInfo.chosenWorkScheme)
        putList(WORKERS, createWorkPermitInfo.workersIds)
        val file = fileManager.getFileFromUri(Uri.parse(createWorkPermitInfo.fileUriString))
        putFile(
            BRIEFING_FILE,
            file,
        )
        putLong(
            MANAGER_EXECUTOR,
            createWorkPermitInfo.approvalResponsibleManagerId
        )
        putLong(ADMITTING_PERSON, createWorkPermitInfo.admittingPersonId)
        putLong(
            INDUSTRY_SAFETY_OFFICER,
            createWorkPermitInfo.industrySafetyOfficerId
        )
        putLong(
            WORK_SAFETY_OFFICER,
            createWorkPermitInfo.workSafetyOfficerId
        )
        putLong(PERMIT_APPROVER, createWorkPermitInfo.approverId)
        return this
    }

    fun build(): MultipartBody {
        return multipartBody.build()
    }

    companion object {

        const val ORGANIZATION = "organization"
        const val PLACE = "place"
        const val WORK_TYPE = "work_type"
        const val START_TIME = "start_time"
        const val EXPIRATION_TIME = "expiration_time"
        const val SHIFT = "shift"

        const val DANGEROUS_FACTORS = "dangerous_factors"
        const val ANOTHER_FACTORS = "another_factors"
        const val SAVE_EQUIPMENT = "save_equipment"
        const val USED_EQUIPMENT = "used_equipment"
        const val WORK_SCHEME = "work_scheme"

        const val WORKERS = "workers"
        const val BRIEFING_FILE = "briefing_file" // may be "document"

        const val RESPONSIBLE_MANAGER = "responsible_manager"
        const val RESPONSIBLE_EXECUTOR = "responsible_executor"
        const val PERMIT_ISSUER = "permit_issuer"
        const val PERMIT_ACCEPTOR = "permit_acceptor"
        const val MANAGER_EXECUTOR = "manager_executor"
        const val ADMITTING_PERSON = "admitting_person"
        const val WORK_SAFETY_OFFICER = "work_safety_officer"
        const val INDUSTRY_SAFETY_OFFICER = "industry_safety_officer"
        const val PERMIT_APPROVER = "permit_approver"
    }
}
