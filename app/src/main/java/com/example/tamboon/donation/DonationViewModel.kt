package com.example.tamboon.donation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.tamboon.shared.DonationRequest
import com.example.tamboon.shared.TamBoonApi
import org.joda.time.DateTime

class DonationViewModel(private val api: TamBoonApi) : ViewModel() {
    private val _state: MutableLiveData<DonationState> = MutableLiveData(DonationState(isValid = false, isLoading = false))
    val state: LiveData<DonationState> = _state
    suspend fun submitDonation(name: String, amount: Int): DonationResult {
        _state.value = _state.value!!.copy(isLoading = true)
        val response = api.postDonation(DonationRequest(name, "token_test_deadbeef", amount))
        _state.value = _state.value!!.copy(isLoading = false)
        return if (response.isSuccessful) {
            val body = response.body()
            if (body?.success == true) {
                DonationResult.Success
            } else {
                DonationResult.Failure(body?.error_message ?: response.message())
            }
        } else {
            DonationResult.Failure(response.message())
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
}

data class DonationState(val isValid: Boolean, val isLoading: Boolean)

sealed class DonationResult {
    object Success : DonationResult()
    data class Failure(val message: String) : DonationResult()
}