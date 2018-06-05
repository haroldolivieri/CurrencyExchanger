package haroldolivieri.currencyexchanger.feature

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.remote.CurrencyRatingService
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject


class CurrencyListPresenter
@Inject constructor(private val view : CurrencyListContract.View,
        private val currencyRatingService: CurrencyRatingService):
        CurrencyListContract.Presenter {

    private var selectedBaseCurrency = Currency.EUR

    override fun onCreate() {
        fetchRates()
    }

    override fun onDestroy() {
        TODO("not implemented")
    }

    override fun changeBaseCurrency(currency: Currency) {
        selectedBaseCurrency = currency
    }

    override fun amountChanged(amount: Long, currentCurrency: Currency) {
        TODO("not implemented")
    }

    private fun fetchRates(currency : Currency = selectedBaseCurrency) {
        currencyRatingService.getRatesByCurrencyBase(currency.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ rate ->
                    val pairList = rate.rates.map{ Pair(it.key, it.value)}
                    view.showCurrencyList(pairList)
                    view.showRateInfo(rate.date, rate.currencyBase)
                }, {t ->
                    view.showError(t.message)
                })
    }

}