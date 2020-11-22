package com.example.tamboon.donation

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.tamboon.shared.DonationResponse
import com.example.tamboon.shared.TamBoonApi
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito
import retrofit2.Response

@ExperimentalCoroutinesApi
class DonationViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var viewModel: DonationViewModel
    private lateinit var api: TamBoonApi


    @Before
    fun setup() {
        api = Mockito.mock(TamBoonApi::class.java)
        viewModel = DonationViewModel(api)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun submitDonationSuccess() {
        runBlockingTest {
            whenever(api.postDonation(any())).thenReturn(Response.success(200, DonationResponse(true, "", "")))
            val result = viewModel.submitDonation("John Doe", 55555)
            verify(api).postDonation(argThat { name == "John Doe" && amount == 55555 })
            assertTrue(result is DonationResult.Success)
        }
    }

    @Test
    fun submitDonationFailure() {
        runBlockingTest {
            whenever(api.postDonation(any())).thenReturn(
                Response.success(
                    200,
                    DonationResponse(false, "insufficient_minerals", "Card has insufficient balance")
                )
            )
            val actual = viewModel.submitDonation("John Doe", 55555)
            verify(api).postDonation(argThat { name == "John Doe" && amount == 55555 })
            assertTrue(actual is DonationResult.Failure)
            assertEquals("Card has insufficient balance", (actual as DonationResult.Failure).message)
        }
    }

    @Test
    fun validateSuccess() {
        viewModel.validate("0123456789012345", "John Doe", 12, 2023, "123", 55555)
        val actual = viewModel.state.value
        assertNotNull(actual)
        assertTrue(actual!!.isValid)
    }

    @Test
    fun validateFailureCardNumber() {
        viewModel.validate("", "John Doe", 12, 2023, "123", 55555)
        val actual = viewModel.state.value
        assertNotNull(actual)
        assertFalse(actual!!.isValid)
    }

    @Test
    fun validateFailureAmount() {
        viewModel.validate("0123456789012345", "John Doe", 12, 2023, "123", -55555)
        val actual = viewModel.state.value
        assertNotNull(actual)
        assertFalse(actual!!.isValid)
    }
}