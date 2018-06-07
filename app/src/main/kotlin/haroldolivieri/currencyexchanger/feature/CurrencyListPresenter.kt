package haroldolivieri.currencyexchanger.feature

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import haroldolivieri.currencyexchanger.remote.CurrencyRatingService
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject


class CurrencyListPresenter
@Inject constructor(private val view: CurrencyListContract.View,
                    private val currencyRatingService: CurrencyRatingService) :
        CurrencyListContract.Presenter {

    private val worker = Schedulers.io().createWorker()
    private val disposable = CompositeDisposable()

    override fun onCreate() {
        fetchRates()
    }

    override fun onDestroy() {
        worker.dispose()
        disposable.clear()
    }

    private fun fetchRates() {
        worker.schedulePeriodically({
            disposable.add(currencyRatingService.getRatesByCurrencyBase(Currency.EUR.name)
                    .toObservable()
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMap { rate ->
                        view.showRateInfo(rate.date, rate.currencyBase)
                        Observable
                                .fromArray(rate.rates.map { CurrencyItem(it.key, it.value) }
                                        as MutableList<CurrencyItem>)
                    }
                    .subscribe({ items ->
                        view.showCurrencyList(items)
                    }, { t ->
                        view.showError(t.message)
                    }))
        }, 0, 1, TimeUnit.SECONDS)
    }

}