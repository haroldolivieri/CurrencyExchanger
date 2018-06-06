package haroldolivieri.currencyexchanger.feature

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import haroldolivieri.currencyexchanger.remote.CurrencyRatingService
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CurrencyListPresenter
@Inject constructor(private val view : CurrencyListContract.View,
        private val currencyRatingService: CurrencyRatingService):
        CurrencyListContract.Presenter {

    private val worker = Schedulers.io().createWorker()

    private var selectedBaseCurrency = Currency.EUR

    override fun onCreate() {
        fetchRates()
    }

    override fun onDestroy() {
        worker.dispose()
    }

    override fun changeBaseCurrency(currency: Currency) {
        selectedBaseCurrency = currency
    }

    override fun amountChanged(amount: Long, currentCurrency: Currency) {
        TODO("not implemented")
    }

    private fun fetchRates(currency : Currency = selectedBaseCurrency) {
        worker.schedulePeriodically({
            currencyRatingService.getRatesByCurrencyBase(currency.name)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ rate ->
                        val currencyItems = rate.rates.map{CurrencyItem(it.key, it.value)}
                        view.showCurrencyList(currencyItems)
                        view.showRateInfo(rate.date, rate.currencyBase)
                    }, {t ->
                        view.showError(t.message)
                    })
        }, 0, 1, TimeUnit.SECONDS)
    }

}