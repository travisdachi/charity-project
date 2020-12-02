package com.example.tamboon

import android.app.Application
import com.example.tamboon.charity_list.CharityListViewModel
import com.example.tamboon.donation.DonationViewModel
import com.example.tamboon.shared.TamBoonApi
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.context.startKoin
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory

class TamBoonApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@TamBoonApp)
            modules(appModule)
        }
    }
}

val appModule = module {
    single {
        Retrofit.Builder()
            .addConverterFactory(JacksonConverterFactory.create(ObjectMapper().registerKotlinModule()))
            .baseUrl("http://10.0.2.2:8080/")
            .build()
    }
    single { get<Retrofit>().create(TamBoonApi::class.java) }
    viewModel { CharityListViewModel(get()) }
    viewModel { DonationViewModel(get()) }
}