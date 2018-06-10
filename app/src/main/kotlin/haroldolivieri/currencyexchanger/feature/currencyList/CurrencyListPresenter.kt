package haroldolivieri.currencyexchanger.feature.currencyList

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.repository.Repository
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class CurrencyListPresenter
@Inject constructor(private val view: CurrencyListContract.View,
                    private val repository: Repository,
                    private val workerScheduler: Scheduler) :
        CurrencyListContract.Presenter {

    private val worker = workerScheduler.createWorker()
    private val disposable = CompositeDisposable()

    override fun onCreate() {
        fetchRates()
        fetchInputtedAmount()
    }

    override fun onDestroy() {
        worker.dispose()
        disposable.clear()
    }

    override fun saveNewSortList(newCurrencyOrder: List<Currency>) {
        repository.saveNewSortList(newCurrencyOrder)
    }

    override fun saveNewInputtedAmount(inputtedAmount: String) {
        repository.saveInputtedAmount(inputtedAmount)
    }

    private fun fetchInputtedAmount() {
        repository
                .fetchInputtedAmount()
                .subscribe({
                    val typedAmount = if (it.isEmpty()) { "0" } else { it.toString() }
                    view.updateInputtedAmount(typedAmount)
                }, {t -> view.showError(t.message)})
    }

    private fun fetchRates() {
        worker.schedulePeriodically({
            disposable.add(repository.fetchOrderedCurrencies()
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ items ->
                        view.showCurrencyList(items)
                    }, { t ->
                        view.showError(t.message)
                    }))
        }, 0, 1, TimeUnit.SECONDS)
    }

}