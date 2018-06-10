package haroldolivieri.currencyexchanger

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.Currency.*
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import haroldolivieri.currencyexchanger.feature.currencyList.CurrencyListContract
import haroldolivieri.currencyexchanger.feature.currencyList.CurrencyListPresenter
import haroldolivieri.currencyexchanger.repository.Repository
import io.reactivex.Scheduler
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mock
import io.reactivex.schedulers.TestScheduler
import org.junit.Before
import io.reactivex.schedulers.Schedulers
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.annotations.NonNull
import org.junit.BeforeClass
import org.junit.Rule
import org.mockito.Mockito.*
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import java.util.concurrent.TimeUnit
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.internal.schedulers.ExecutorScheduler
import io.reactivex.disposables.Disposable
import java.util.concurrent.Executor


class CurrencyListPresenterTest {

    @JvmField
    @Rule
    var rule: MockitoRule = MockitoJUnit.rule()
    @Mock
    lateinit var repository: Repository
    @Mock
    lateinit var view: CurrencyListContract.View

    private lateinit var presenter : CurrencyListPresenter
    private val testScheduler = TestScheduler()

    companion object {
        @BeforeClass @JvmStatic
        fun setupClass() {
            val immediate = object : Scheduler() {
                override fun scheduleDirect(@NonNull run: Runnable,
                                            delay: Long,
                                            @NonNull unit: TimeUnit): Disposable {
                    return super.scheduleDirect(run, 0, unit)
                }

                override fun createWorker(): Worker {
                    return ExecutorScheduler.ExecutorWorker(Executor { it.run() })
                }
            }

            RxJavaPlugins.setInitIoSchedulerHandler { immediate }
            RxJavaPlugins.setInitComputationSchedulerHandler { immediate }
            RxJavaPlugins.setInitNewThreadSchedulerHandler { immediate }
            RxJavaPlugins.setInitSingleSchedulerHandler { immediate }
            RxAndroidPlugins.setInitMainThreadSchedulerHandler { immediate }
        }
    }

    private val currencies = listOf(CurrencyItem(AUD, 4.3F),
            CurrencyItem(EUR, 1.4F), CurrencyItem(MXN, 2.6F),
            CurrencyItem(MYR, 2.1F), CurrencyItem(PLN, 1.8F),
            CurrencyItem(BRL, 1.0F), CurrencyItem(GBP, 8.7F))

    @Before
    fun setup() {
        presenter = CurrencyListPresenter(view, repository, testScheduler)

        `when`(repository.fetchOrderedCurrencies()).thenReturn(Single.just(currencies))
        `when`(repository.fetchInputtedAmount()).thenReturn(Single.just("123"))
    }

    @Test
    fun betweenPeriodicSchedulesReportsIdle() {
        presenter.onCreate()

        testScheduler.advanceTimeBy(10, TimeUnit.SECONDS)
        testScheduler.triggerActions()

        verify(view).updateInputtedAmount("123")
        verify(view, times(11)).showCurrencyList(currencies)
    }
}