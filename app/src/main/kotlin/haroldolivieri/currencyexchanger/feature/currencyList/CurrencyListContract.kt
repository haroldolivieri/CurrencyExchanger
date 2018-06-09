package haroldolivieri.currencyexchanger.feature.currencyList

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import java.util.*

object CurrencyListContract {
    interface View {
        fun showCurrencyList(rates: List<CurrencyItem>)
        fun showError(message: String?)
        fun updateInputtedAmount(cachedInputtedAmount: String)
    }

    interface Presenter {
        fun onCreate()
        fun onDestroy()
        fun saveNewMultiplier(multiplier: Float)
        fun saveNewSortList(newCurrencyOrder: List<Currency>)
    }
}