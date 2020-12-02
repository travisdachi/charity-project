package com.example.tamboon.donation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.omise.android.api.Client
import co.omise.android.api.RequestListener
import co.omise.android.models.CardParam
import co.omise.android.models.Token
import com.example.tamboon.shared.DonationRequest
import com.example.tamboon.shared.TamBoonApi
import org.joda.time.DateTime
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DonationViewModel(private val api: TamBoonApi) : ViewModel() {
    private val _state: MutableLiveData<DonationState> = MutableLiveData(DonationState(isValid = false, isLoading = false))
    val state: LiveData<DonationState> = _state
    suspend fun submitDonation(
        name: String,
        amount: Int,
        cardNo: String,
        expirationMonth: Int,
        expirationYear: Int,
        securityCode: String
    ): DonationResult {
        _state.value = _state.value!!.copy(isLoading = true)
        val cardParam = CardParam(
            name = name,
            number = cardNo,
            expirationMonth = expirationMonth,
            expirationYear = expirationYear,
            securityCode = securityCode
        )
        val token = tokenize(cardParam)
        val response = api.postDonation(DonationRequest(name, token.id!!, amount))
        _state.value = _state.value!!.copy(isLoading = false)
        return if (response.isSuccessful) {
            val body = response.body()
            if (body?.success == true) {
                DonationResult.Success
            } else {
                DonationResult.Failure(body?.error_message ?: response.message())
            }
        } else {
            DonationResult.Failure(response.errorBody()?.string() ?: response.message())
        }
    }

    fun validate(cardNo: String, name: String, expirationMonth: Int, expirationYear: Int, securityCode: String, amount: Int) {
        val isValid = cardNo.length == 16
            && name.isNotEmpty()
            && expirationMonth in 1..12
            && expirationYear >= DateTime.now().year
            && securityCode.length in 3..4
            && amount > 0
        _state.value = _state.value!!.copy(isValid = isValid)
    }

    private suspend fun tokenize(cardParam: CardParam): Token {
        val client = Client("pkey_test_5m2deowgbfufl9ywibq")
        val request = Token.CreateTokenRequestBuilder(cardParam).build()
        return suspendCoroutine<Token> { continuation ->
            client.send(request, object : RequestListener<Token> {
                override fun onRequestFailed(throwable: Throwable) {
                    continuation.resumeWithException(throwable)
                }

                override fun onRequestSucceed(model: Token) {
                    continuation.resume(model)
                }
            })
        }
    }
}

data class DonationState(val isValid: Boolean, val isLoading: Boolean)

sealed class DonationResult {
    object Success : DonationResult()
    data class Failure(val message: String) : DonationResult()
}