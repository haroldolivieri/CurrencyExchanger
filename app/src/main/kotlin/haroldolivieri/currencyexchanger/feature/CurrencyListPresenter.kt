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

    override fun onCreate() {
        fetchRates()
    }

    override fun onDestroy() {
        TODO("not implemented")
    }

    override fun changeBaseCurrency() {
        TODO("not implemented")
    }

    override fun amountChanged(amount: Long, currentCurrency: Currency) {
        TODO("not implemented")
    }

    private fun fetchRates(currency : Currency = Currency.EUR) {
        currencyRatingService.getRatesbyCurrencyBase(currency.name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ rate ->
                    view.showRateInfo(rate.date, rate.currencyBase)

                }, {t -> })
    }

}