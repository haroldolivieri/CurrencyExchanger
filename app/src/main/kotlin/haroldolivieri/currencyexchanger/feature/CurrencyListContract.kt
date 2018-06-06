package haroldolivieri.currencyexchanger.feature

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import java.util.*


object CurrencyListContract {
    interface View {
        fun showCurrencyList(rates : List<CurrencyItem>)
        fun showRateInfo(date : Date, base : Currency)
        fun showError(message: String?)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun changeBaseCurrency(currency: Currency)
        fun amountChanged(amount: Long, currentCurrency : Currency)
    }
}