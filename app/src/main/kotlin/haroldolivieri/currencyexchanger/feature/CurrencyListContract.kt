package haroldolivieri.currencyexchanger.feature

import haroldolivieri.currencyexchanger.domain.Currency
import java.util.*


object CurrencyListContract {
    interface View {
        fun showCurrencyList(rates : List<Pair<Currency, String>>)
        fun showRateInfo(date : Date, base : Currency)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun changeBaseCurrency()
        fun amountChanged(amount: Long, currentCurrency : Currency)
    }
}