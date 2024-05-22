package ru.metasharks.catm.feature.createworkpermit.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.core.navigation.ApplicationRouter
import ru.metasharks.catm.core.navigation.screens.result.CreateWorkPermitScreenForResult
import ru.metasharks.catm.core.network.offline.OfflineModeProvider
import ru.metasharks.catm.core.storage.file.FileManager
import ru.metasharks.catm.feature.createworkpermit.ui.result.OutputsWrapper
import ru.metasharks.catm.feature.createworkpermit.ui.steps.approval.ApprovalStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.approval.ApprovalStepOutput
import ru.metasharks.catm.feature.createworkpermit.ui.steps.attachment.AttachmentStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.attachment.AttachmentStepOutput
import ru.metasharks.catm.feature.createworkpermit.ui.steps.dangerousfactors.DangerousFactorsStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.dangerousfactors.DangerousFactorsStepOutput
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.EmployeesStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.employees.EmployeesStepOutput
import ru.metasharks.catm.feature.createworkpermit.ui.steps.equipment.EquipmentStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.equipment.EquipmentStepOutput
import ru.metasharks.catm.feature.createworkpermit.ui.steps.generalinfo.GeneralInfoStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.generalinfo.GeneralInfoStepOutput
import ru.metasharks.catm.feature.createworkpermit.ui.steps.workscheme.WorkSchemeStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.workscheme.WorkSchemeStepOutput
import ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.WorkTypeStep
import ru.metasharks.catm.feature.createworkpermit.ui.steps.worktype.WorkTypeStepOutput
import ru.metasharks.catm.feature.offline.pending.entities.PendingActionPayload
import ru.metasharks.catm.feature.offline.pending.usecases.SavePendingActionUseCase
import ru.metasharks.catm.feature.offline.save.Paths
import ru.metasharks.catm.feature.workpermit.entities.create.CreateWorkPermitInfo
import ru.metasharks.catm.feature.workpermit.usecase.create.CreateWorkPermitUseCase
import ru.metasharks.catm.feature.workpermit.usecase.create.GenerateWorkPermitPdfUseCase
import ru.metasharks.catm.step.entities.ProcessEntity
import ru.metasharks.catm.step.entities.RestoreData
import ru.metasharks.catm.utils.strings.StringResWrapper
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class CreateWorkPermitViewModel @Inject constructor(
    private val createWorkPermitUseCase: CreateWorkPermitUseCase,
    private val generateWorkPermitPdfUseCase: GenerateWorkPermitPdfUseCase,
    private val appRouter: ApplicationRouter,
    private val fileManager: FileManager,
    private val offlineModeProvider: OfflineModeProvider,
    private val savePendingActionUseCase: SavePendingActionUseCase,
) : ViewModel() {

    private var workType: String? = null

    private var outputsWrapper = OutputsWrapper()

    private val _process = MutableLiveData<ProcessEntity>()
    val process: LiveData<ProcessEntity> = _process

    private val currentProcess: ProcessEntity
        get() = requireNotNull(_process.value)

    val currentProgress
        get() = currentProcess.currentProgress

    private val _updatePayload = MutableLiveData<Payload>()
    val updatePayload: LiveData<Payload> = _updatePayload

    fun exit() {
        appRouter.exit()
    }

    private fun exitAndRefresh() {
        appRouter.sendResultBy(CreateWorkPermitScreenForResult.KEY, true)
        exit()
    }

    fun load() {
        _process.value = getProcessEntity()
    }

    fun saveOutput(output: Any?, restoreData: RestoreData?) {
        processOutput(output, restoreData)
        _updatePayload.value = Payload.Forward()
    }

    private fun processOutput(output: Any?, restoreData: RestoreData?) {
        check(output != null) {
            "Are you sure that saving output should be null"
        }
        currentProcess.currentStep.restoreData = restoreData
        outputsWrapper.addOutput(output)
    }

    fun back() {
        _updatePayload.value = Payload.Back()
    }

    @Suppress("MagicNumber")
    private fun getProcessEntity(): ProcessEntity {
        val processEntity = ProcessEntity()
        processEntity.addSteps(
            WorkTypeStep(0, StringResWrapper(R.string.step_work_type)),
            GeneralInfoStep(1, StringResWrapper(R.string.step_general_info)),
            DangerousFactorsStep(2, StringResWrapper(R.string.step_dangerous_factors)),
            EquipmentStep(3, StringResWrapper(R.string.step_equipment)),
            WorkSchemeStep(4, StringResWrapper(R.string.step_work_scheme)),
            EmployeesStep(5, StringResWrapper(R.string.step_employees)),
            AttachmentStep(6, StringResWrapper(R.string.step_attachment)),
            ApprovalStep(7, StringResWrapper(R.string.step_approval)),
        )
        return processEntity
    }

    fun createWorkPermit() {
        val outputsList = outputsWrapper.getListOf(
            GeneralInfoStepOutput::class,
            WorkTypeStepOutput::class,
            DangerousFactorsStepOutput::class,
            EquipmentStepOutput::class,
            WorkSchemeStepOutput::class,
            EmployeesStepOutput::class,
            AttachmentStepOutput::class,
            ApprovalStepOutput::class
        )
        val createWorkPermitInfo = collectCreateInfo(outputsList)
        if (offlineModeProvider.isInOfflineMode) {
            createWorkPermitOffline(createWorkPermitInfo)
        } else {
            createWorkPermitUseCase(createWorkPermitInfo)
                .flatMapCompletable { generateWorkPermitPdfUseCase(it.id) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Timber.d("Successfully created work-permit!")
                        exitAndRefresh()
                    }, {
                        Timber.e(it)
                    }
                )
        }
    }

    private inline fun <reified T> List<Any>.getOutput(): T {
        return first { it is T } as T
    }

    private fun collectCreateInfo(outputs: List<Any>): CreateWorkPermitInfo {
        val general: GeneralInfoStepOutput = outputs.getOutput()
        val workType: WorkTypeStepOutput = outputs.getOutput()
        val dangerousFactors: DangerousFactorsStepOutput = outputs.getOutput()
        val equipment: EquipmentStepOutput = outputs.getOutput()
        val workScheme: WorkSchemeStepOutput = outputs.getOutput()
        val employees: EmployeesStepOutput = outputs.getOutput()
        val attachments: AttachmentStepOutput = outputs.getOutput()
        val approval: ApprovalStepOutput = outputs.getOutput()

        val uri = attachments.fileUri
        val fileInformation = fileManager.loadFileInformationByUri(uri)
        val file = fileManager.getFileFromUri(uri)
        fileManager.saveFile(
            file = file,
            fileName = fileInformation.first,
            filePath = Paths.getWorkPermitBuffer(general.responsibleManagerId)
        )
        val newFileUri = fileManager.getFileUri(
            filePath = Paths.getWorkPermitBuffer(general.responsibleManagerId),
            fileName = fileInformation.first
        )

        return CreateWorkPermitInfo(
            organization = general.organization,
            place = general.place,
            startTime = general.startTime,
            expirationTime = general.expirationTime,
            shift = general.shift,
            responsibleManagerId = general.responsibleManagerId,
            responsibleExecutorId = general.responsibleExecutorId,
            permitIssuerId = general.permitIssuerId,
            permitAcceptorId = general.permitAcceptorId,
            workTypeId = workType.workTypeId,
            chosenDangerousFactors = dangerousFactors.chosenDangerousFactors,
            chosenAnotherFactors = dangerousFactors.chosenAnotherFactors,
            chosenSaveEquipment = dangerousFactors.chosenSaveEquipment,
            chosenUsedEquipment = equipment.chosenUsedEquipment,
            chosenWorkScheme = workScheme.chosenWorkScheme,
            workersIds = employees.workersIds,
            fileUriString = newFileUri.toString(),
            approvalResponsibleManagerId = approval.approvalResponsibleManagerId,
            admittingPersonId = approval.admittingPersonId,
            industrySafetyOfficerId = approval.industrySafetyOfficerId,
            workSafetyOfficerId = approval.workSafetyOfficerId,
            approverId = approval.approverId,
        )
    }

    private fun createWorkPermitOffline(createWorkPermitInfo: CreateWorkPermitInfo) {
        savePendingActionUseCase(PendingActionPayload.CreateWorkPermit(createWorkPermitInfo))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    exitAndRefresh()
                }, { error ->
                    Timber.e(error)
                }
            )
    }

    fun setWorkType(workType: String) {
        this.workType = workType
    }
}

sealed class Payload(code: Int) {

    class Forward : Payload(1)
    class Back : Payload(2)
}
