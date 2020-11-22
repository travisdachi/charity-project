package com.example.tamboon.charity_list

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tamboon.shared.Charity
import com.example.tamboon.shared.TamBoonApi
import kotlinx.coroutines.launch

class CharityListViewModel(val api: TamBoonApi) : ViewModel() {
    private val _charityListState: MutableLiveData<CharityListState> = MutableLiveData(
        CharityListState.Loading
    )
    val charityListState: LiveData<CharityListState> = _charityListState
    fun getCharities() {
        viewModelScope.launch {
            val response = api.getCharities()
            if (response.isSuccessful) {
                val body = response.body()
                when {
                    body == null -> _charityListState.value =
                        CharityListState.Error("Something went wrong")
                    body.data.isEmpty() -> _charityListState.value = CharityListState.Empty
                    else -> _charityListState.value = CharityListState.Success(body.data)
                }
            } else {
                _charityListState.value = CharityListState.Error(response.message())
            }
        }
    }
}

sealed class CharityListState {
    object Loading : CharityListState()
    object Empty : CharityListState()
    data class Error(val message: String?) : CharityListState()
    data class Success(val list: List<Charity>) : CharityListState()
}