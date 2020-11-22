package com.example.tamboon.charity_list

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.tamboon.shared.Charity
import com.example.tamboon.shared.CharityResponse
import com.example.tamboon.shared.TamBoonApi
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Response

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class CharityListViewModelTest {

    @get:Rule
    val rule = InstantTaskExecutorRule()
    private val testDispatcher = TestCoroutineDispatcher()
    private lateinit var viewModel: CharityListViewModel
    private lateinit var api: TamBoonApi


    @Before
    fun setup() {
        api = Mockito.mock(TamBoonApi::class.java)
        viewModel = CharityListViewModel(api)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun teardown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun getCharitiesSuccess() {
        runBlockingTest {
            whenever(api.getCharities())
                .thenReturn(Response.success(200, CharityResponse(1, listOf(Charity(id = 1, name = "my pocket", logo_url = "some url")))))
            viewModel.getCharities()
            val actual = viewModel.charityListState.value
            assertNotNull(actual)
            assertTrue(actual is CharityListState.Success)
            assertEquals(1, (actual as CharityListState.Success).list.size)
            assertEquals("my pocket", actual.list[0].name)
        }
    }

    @Test
    fun getCharitiesFailure() {
        runBlockingTest {
            whenever(api.getCharities()).thenReturn(Response.error(500, ResponseBody.create(MediaType.get("text/plain"), "test error")))
            viewModel.getCharities()
            val actual = viewModel.charityListState.value
            assertTrue(actual is CharityListState.Failure)
        }
    }

    @Test
    fun getCharitiesEmpty() {
        runBlockingTest {
            whenever(api.getCharities()).thenReturn(Response.success(200, CharityResponse(10, emptyList())))
            viewModel.getCharities()
            assertTrue(viewModel.charityListState.value is CharityListState.Empty)
        }
    }
}