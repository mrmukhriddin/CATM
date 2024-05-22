package ru.metasharks.catm.feature.feedback.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.schedulers.Schedulers
import ru.metasharks.catm.feature.feedback.entities.SendFeedbackRequestX
import ru.metasharks.catm.feature.feedback.usecase.SendFeedbackUseCase
import javax.inject.Inject

@HiltViewModel
internal class FeedbackViewModel @Inject constructor(
    private val sendFeedbackUseCase: SendFeedbackUseCase,
) : ViewModel() {

    private var sentMessages = 0

    private val _error = MutableLiveData<Throwable>()
    val error: LiveData<Throwable> = _error

    private val _sent = MutableLiveData<Int>()
    val sent: LiveData<Int> = _sent

    fun sendFeedback(data: FeedbackData) {
        sendFeedbackUseCase(mapDataToRequest(data))
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    _sent.value = ++sentMessages
                }, {
                    _error.value = it
                }
            )
    }

    private fun mapDataToRequest(data: FeedbackData): SendFeedbackRequestX = SendFeedbackRequestX(
        theme = data.theme,
        email = data.email,
        message = data.message,
    )

    class FeedbackData(
        val theme: String,
        val email: String,
        val message: String,
    )
}
