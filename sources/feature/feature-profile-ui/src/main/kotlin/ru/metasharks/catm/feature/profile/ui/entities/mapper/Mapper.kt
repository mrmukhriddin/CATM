package ru.metasharks.catm.feature.profile.ui.entities.mapper

import ru.metasharks.catm.feature.profile.db.entities.UserDb
import ru.metasharks.catm.feature.profile.entities.CommonDocumentX
import ru.metasharks.catm.feature.profile.entities.MedicalExamX
import ru.metasharks.catm.feature.profile.entities.ProtectiveEquipmentCardX
import ru.metasharks.catm.feature.profile.entities.ToolX
import ru.metasharks.catm.feature.profile.entities.TrainingX
import ru.metasharks.catm.feature.profile.entities.UserBriefingX
import ru.metasharks.catm.feature.profile.entities.UserProfileX
import ru.metasharks.catm.feature.profile.entities.user.WorkerX
import ru.metasharks.catm.feature.profile.ui.entities.CommonDocumentUI
import ru.metasharks.catm.feature.profile.ui.entities.MedicalExamUI
import ru.metasharks.catm.feature.profile.ui.entities.ProfileUI
import ru.metasharks.catm.feature.profile.ui.entities.ProtectiveEquipmentCardUI
import ru.metasharks.catm.feature.profile.ui.entities.ToolUI
import ru.metasharks.catm.feature.profile.ui.entities.TrainingUI
import ru.metasharks.catm.feature.profile.ui.entities.UserBriefingUI
import ru.metasharks.catm.feature.profile.ui.entities.UserUI
import ru.metasharks.catm.feature.profile.ui.workers.recycler.WorkerUI
import javax.inject.Inject

internal class Mapper @Inject constructor() {

    fun mapWorker(user: WorkerX): WorkerUI {
        return WorkerUI(
            userId = user.id,
            firstName = user.firstName,
            lastName = user.lastName,
            position = user.position,
            avatar = user.avatar,
        )
    }

    fun mapProfileToWorker(user: UserProfileX): WorkerUI {
        return WorkerUI(
            userId = user.id.toInt(),
            firstName = user.firstName,
            lastName = user.lastName,
            position = user.position,
            avatar = user.avatar,
        )
    }

    fun mapUser(userDb: UserDb): UserUI {
        val user = userDb.userProfileX
        return UserUI(
            firstName = user.firstName,
            lastName = user.lastName,
            position = user.position,
            avatar = user.avatar,
        )
    }

    fun mapProfile(user: UserProfileX): ProfileUI {
        return ProfileUI(
            firstName = user.firstName,
            lastName = user.lastName,
            position = user.position,
            avatar = user.avatar,
            unitTitle = user.unit?.title,
            phoneNumber = user.phoneNumber,
            commonDocument = mapCommonDocument(user.commonDoc),
            medicalExam = mapMedicalExam(user.medicalExam),
            trainings = mapTrainings(user.trainings ?: emptyList()),
            protectiveEquipmentCard = mapProtectiveEquipmentCard(user.protectiveEquipmentCard),
            briefings = mapBriefings(user.briefings ?: emptyList()),
            tools = mapTools(user.tools ?: emptyList()),
        )
    }

    private fun mapCommonDocument(doc: CommonDocumentX?): CommonDocumentUI? {
        return doc?.let {
            return CommonDocumentUI(
                documentUri = doc.document,
                date = doc.date,
                time = doc.time,
            )
        }
    }

    private fun mapTrainings(trainings: List<TrainingX>): List<TrainingUI> {
        return trainings.map {
            TrainingUI(
                number = it.number,
                expirationDate = it.expirationDate,
                fileUri = it.fileUri,
            )
        }
    }

    private fun mapTools(tools: List<ToolX>): List<ToolUI> {
        return tools.map { tool ->
            ToolUI(
                tool = tool.title
            )
        }
    }

    private fun mapBriefings(briefings: List<UserBriefingX>): List<UserBriefingUI> {
        return briefings.map { briefing ->
            UserBriefingUI(
                category = briefing.category,
                signed = briefing.signed,
            )
        }
    }

    private fun mapProtectiveEquipmentCard(card: ProtectiveEquipmentCardX?): ProtectiveEquipmentCardUI? {
        if (card == null) return null
        return ProtectiveEquipmentCardUI(
            number = card.number,
            fileUri = card.fileUri,
        )
    }

    private fun mapMedicalExam(exam: MedicalExamX?): MedicalExamUI? {
        if (exam == null) return null
        return MedicalExamUI(
            number = exam.number,
            expirationDate = exam.expirationDate,
            fileUri = exam.fileUri
        )
    }
}
