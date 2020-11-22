package com.example.tamboon

import android.app.Application
import com.example.tamboon.charity_list.CharityListViewModel
import com.example.tamboon.donation.DonationViewModel
import com.example.tamboon.shared.TamBoonApi
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit

class TamBoonApp : Application() {
    @ExperimentalSerializationApi
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TamBoonApp)
            modules(appModule)
        }
    }
}

@ExperimentalSerializationApi
val appModule = module {
    single {
        Retrofit.Builder()
            .addConverterFactory(Json.asConverterFactory(MediaType.get("application/json")))
            .baseUrl("https://virtserver.swaggerhub.com/chakritw/tamboon-api/1.0.0/")
            .build()
    }
    single { get<Retrofit>().create(TamBoonApi::class.java) }
    viewModel { CharityListViewModel(get()) }
    viewModel { DonationViewModel(get()) }
}