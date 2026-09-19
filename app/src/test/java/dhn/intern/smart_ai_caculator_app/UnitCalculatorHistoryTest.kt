package dhn.intern.smart_ai_caculator_app

import dhn.intern.smart_ai_caculator_app.data.repository.CalculatorHistoryRepository
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import dhn.intern.smart_ai_caculator_app.ui.currency.CurrencyCalculatorViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UnitCalculatorHistoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val currencyRepository: CurrencyRepository = mockk(relaxed = true)
    private val historyRepository: CalculatorHistoryRepository = mockk(relaxed = true)

    private lateinit var viewModel: CurrencyCalculatorViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { currencyRepository.getRates() } returns mapOf("USD" to 1.0, "EUR" to 0.9)
        coEvery { currencyRepository.getLastUpdatedTimestamp() } returns System.currentTimeMillis()

        viewModel = CurrencyCalculatorViewModel(
            currencyRepository = currencyRepository,
            historyRepository = historyRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `debounce prevents multiple inserts when typing quickly and saves only final value`() = runTest {
        val capturedExpression = slot<String>()
        coEvery {
            historyRepository.saveUnitConverter(capture(capturedExpression), any())
        } returns Unit

        // Mô phỏng người dùng gõ liên tục: 1 -> 10 -> 100 trong khoảng cách ngắn (< 1500ms)
        viewModel.onAmountChanged(1.0)
        advanceTimeBy(500L)

        viewModel.onAmountChanged(10.0)
        advanceTimeBy(500L)

        viewModel.onAmountChanged(100.0)

        // Dừng gõ, chờ vượt mốc debounce 1500ms
        advanceTimeBy(1600L)

        // Xác minh chỉ gọi saveUnitConverter đúng 1 lần duy nhất với giá trị cuối
        coVerify(exactly = 1) { historyRepository.saveUnitConverter(any(), any()) }
        assertEquals("100.0 USD", capturedExpression.captured)
    }

    @Test
    fun `do not insert history when input value is zero or negative`() = runTest {
        viewModel.onAmountChanged(0.0)
        advanceTimeBy(2000L)

        // Xác minh không được lưu vào DB khi số tiền <= 0
        coVerify(exactly = 0) { historyRepository.saveUnitConverter(any(), any()) }
    }
}
