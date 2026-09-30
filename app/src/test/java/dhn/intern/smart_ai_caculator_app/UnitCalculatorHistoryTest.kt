package dhn.intern.smart_ai_caculator_app

import dhn.intern.smart_ai_caculator_app.data.local.dao.CalculatorHistoryDao
import dhn.intern.smart_ai_caculator_app.data.repository.CurrencyRepository
import dhn.intern.smart_ai_caculator_app.data.source.currencies.DefaultCurrencyRates
import dhn.intern.smart_ai_caculator_app.ui.viewmodel.UnitCalculatorViewModel
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class UnitCalculatorHistoryTest {

    private val testDispatcher = StandardTestDispatcher()
    private val currencyRepository: CurrencyRepository = mockk(relaxed = true)
    private val historyDao: CalculatorHistoryDao = mockk(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { currencyRepository.getRates() } returns DefaultCurrencyRates.FALLBACK_RATES
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testDebouncedSaveHistoryExecutesAfterDelay() = runTest(testDispatcher) {
        val viewModel = UnitCalculatorViewModel(
            currencyRepository = currencyRepository,
            historyDao = historyDao,
            ioDispatcher = testDispatcher
        )

        viewModel.onKeyPress("5")

        // 1. Chỉ tua đúng 1000ms (chưa đủ 1500ms debounce), không dùng advanceUntilIdle() ở đây
        advanceTimeBy(1000)
        testScheduler.runCurrent()
        coVerify(exactly = 0) { historyDao.insert(any()) }

        // 2. Tua thêm 600ms (tổng 1600ms > 1500ms), debounce đã kích hoạt
        advanceTimeBy(600)
        advanceUntilIdle()
        coVerify(atLeast = 1) { historyDao.insert(any()) }
    }

    @Test
    fun testSwapUnitsSavesImmediately() = runTest(testDispatcher) {
        val viewModel = UnitCalculatorViewModel(
            currencyRepository = currencyRepository,
            historyDao = historyDao,
            ioDispatcher = testDispatcher
        )

        viewModel.onKeyPress("⇅")
        advanceUntilIdle()

        coVerify(atLeast = 1) { historyDao.insert(any()) }
    }
}
