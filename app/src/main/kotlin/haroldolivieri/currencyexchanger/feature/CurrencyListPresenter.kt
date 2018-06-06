package haroldolivieri.currencyexchanger.feature

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import haroldolivieri.currencyexchanger.remote.CurrencyRatingService
import io.reactivex.Observable
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

    private fun fetchRates(currency : Currency = selectedBaseCurrency) {
        worker.schedulePeriodically({
            currencyRatingService.getRatesByCurrencyBase(currency.name)
                    .toObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap { rate ->
                        view.showRateInfo(rate.date, rate.currencyBase)
                        Observable.fromArray(rate.rates
                                .map{CurrencyItem(it.key, it.value)} as MutableList<CurrencyItem>)
                    }
                    .subscribe({ items ->
                        view.showCurrencyList(items)
                    }, {t ->
                        view.showError(t.message)
                    })
        }, 0, 1, TimeUnit.SECONDS)
    }

}