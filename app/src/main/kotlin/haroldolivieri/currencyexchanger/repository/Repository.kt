package haroldolivieri.currencyexchanger.repository

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import io.reactivex.Single

interface Repository {
    fun fetchOrderedCurrencies(): Single<List<CurrencyItem>>
    fun saveNewSortList(newCurrencyOrder: List<Currency>)
    fun fetchInputtedAmount() : Single<String>
    fun saveInputtedAmount(inputtedAmount : String)
}