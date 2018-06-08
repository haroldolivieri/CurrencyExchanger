package haroldolivieri.currencyexchanger.feature

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.repository.CurrencyRepository
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CurrencyListPresenter
@Inject constructor(private val view: CurrencyListContract.View,
                    private val currencyRepository: CurrencyRepository) :
        CurrencyListContract.Presenter {

    override fun saveNewOrder(newCurrencyOrder: List<Currency>) {
        currencyRepository.saveNewOrder(newCurrencyOrder)
    }

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
            disposable.add(currencyRepository.fetchOrderedCurrencies()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ items ->
                        view.showCurrencyList(items)
                    }, { t ->
                        view.showError(t.message)
                    }))
        }, 0, 1, TimeUnit.SECONDS)
    }
}