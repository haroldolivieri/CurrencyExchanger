package haroldolivieri.currencyexchanger.feature.currentList

import haroldolivieri.currencyexchanger.RxImmediateSchedulerRule
import haroldolivieri.currencyexchanger.domain.Currency.*
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import haroldolivieri.currencyexchanger.feature.currencyList.CurrencyListContract
import haroldolivieri.currencyexchanger.feature.currencyList.CurrencyListPresenter
import haroldolivieri.currencyexchanger.repository.Repository
import io.reactivex.Single
import org.mockito.Mock
import io.reactivex.schedulers.TestScheduler
import org.mockito.Mockito.*
import java.util.concurrent.TimeUnit
import org.junit.*
import org.mockito.MockitoAnnotations

class CurrencyListPresenterTest {

    @Rule @JvmField val schedulers = RxImmediateSchedulerRule()

    @Mock
    private
    lateinit var repository: Repository

    @Mock
    lateinit var view: CurrencyListContract.View

    private lateinit var presenter : CurrencyListPresenter
    private val testScheduler = TestScheduler()

    private val currencies = listOf(CurrencyItem(AUD, 4.3F),
            CurrencyItem(EUR, 1.4F), CurrencyItem(MXN, 2.6F),
            CurrencyItem(MYR, 2.1F), CurrencyItem(PLN, 1.8F),
            CurrencyItem(BRL, 1.0F), CurrencyItem(GBP, 8.7F))

    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        presenter = CurrencyListPresenter(view, repository, testScheduler)
    }

    @Test
    fun test_request_each_second() {
        `when`(repository.fetchOrderedCurrencies()).thenReturn(Single.just(currencies))
        `when`(repository.fetchInputtedAmount()).thenReturn(Single.just("123"))

        presenter.onCreate()
        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)

        verify(view).updateInputtedAmount("123")
        verify(view, times(11)).showCurrencyList(currencies)
    }

    @Test
    fun test_inputted_amount_empty() {
        `when`(repository.fetchInputtedAmount()).thenReturn(Single.just(""))
        presenter.onCreate()
        verify(view).updateInputtedAmount("0")
    }
}