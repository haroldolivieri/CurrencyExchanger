package haroldolivieri.currencyexchanger.feature.currencyList

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

    private val worker = Schedulers.io().createWorker()
    private val disposable = CompositeDisposable()

    override fun onCreate() {
        fetchRates()
        fetchMultiplier()
    }

    override fun onDestroy() {
        worker.dispose()
        disposable.clear()
    }

    override fun saveNewSortList(newCurrencyOrder: List<Currency>) {
        currencyRepository.saveNewSortList(newCurrencyOrder)
    }

    override fun saveNewMultiplier(multiplier: Float) {
        currencyRepository.saveInputtedAmount(multiplier)
    }

    private fun fetchMultiplier() {
        currencyRepository
                .fetchInputtedAmount()
                .subscribe({
                    val typedAmount = if (it == 0F) { "0" } else { it.toString() }
                    view.updateInputtedAmount(typedAmount)
                }, {t -> view.showError(t.message)})
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