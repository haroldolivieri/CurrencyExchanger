package haroldolivieri.currencyexchanger.repository

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import io.reactivex.Single

interface Repository {
    fun fetchOrderedCurrencies(): Single<List<CurrencyItem>>
    fun saveNewSortList(newCurrencyOrder: List<Currency>)
    fun fetchMultiplier() : Single<Float>
    fun saveMultiplier(multiplier : Float)
}