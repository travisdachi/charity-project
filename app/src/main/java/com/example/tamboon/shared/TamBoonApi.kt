package com.example.tamboon.shared

import kotlinx.serialization.Serializable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST


interface TamBoonApi {
    @GET("charities")
    suspend fun getCharities(): Response<CharityResponse>

    @POST("donations")
    suspend fun postDonation(@Body request: DonationRequest): Response<DonationResponse>
}

@Serializable
data class Charity(val id: Int, val name: String, val logo_url: String)

@Serializable
data class CharityResponse(val total: Int, val data: List<Charity>)

@Serializable
data class DonationRequest(val name: String, val token: String, val amount: Int)

@Serializable
data class DonationResponse(val success: Boolean, val error_code: String, val error_message: String)