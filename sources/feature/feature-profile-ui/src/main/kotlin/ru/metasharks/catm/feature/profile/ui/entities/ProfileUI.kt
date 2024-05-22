package ru.metasharks.catm.feature.profile.ui.entities

class ProfileUI(
    val firstName: String,
    val lastName: String,
    val position: String?,
    val avatar: String?,
    val unitTitle: String?,
    val phoneNumber: String?,
    val medicalExam: MedicalExamUI?,
    val trainings: List<TrainingUI>,
    val protectiveEquipmentCard: ProtectiveEquipmentCardUI?,
    val commonDocument: CommonDocumentUI?,
    val briefings: List<UserBriefingUI>,
    val tools: List<ToolUI>,
)
