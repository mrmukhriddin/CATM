package ru.metasharks.catm.feature.offline.save.profile

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.internal.functions.Functions
import ru.metasharks.catm.feature.profile.entities.user.WorkerX
import ru.metasharks.catm.feature.profile.usecase.GetWorkersUseCase
import javax.inject.Inject

fun interface SaveWorkersUseCase {

    operator fun invoke(): Completable
}

internal class SaveWorkersUseCaseImpl @Inject constructor(
    private val saveProfileUseCase: SaveProfileUseCase,
    private val getWorkersUseCase: GetWorkersUseCase,
) : SaveWorkersUseCase {

    override fun invoke(): Completable {
        val workerObservable = Observable.create<List<WorkerX>> { emitter ->
            var next: String? = null
            do {
                val envelope = getWorkersUseCase(next).blockingGet()
                emitter.onNext(envelope.workers)
                next = envelope.next
            } while (next != null)
            emitter.onComplete()
        }

        return workerObservable.reduce(emptyList<WorkerX>()) { a, next ->
            a + next
        }
            .flattenAsObservable(Functions.identity())
            .flatMapCompletable {
                saveProfileUseCase(it.id.toLong())
            }
    }
}
