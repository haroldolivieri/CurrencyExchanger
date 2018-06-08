package haroldolivieri.currencyexchanger.repository.local

import haroldolivieri.currencyexchanger.domain.Currency
import io.reactivex.Completable
import io.reactivex.Single

interface LocalStore {
    fun saveNewOrder(orderList: List<Currency>): Completable
    fun fetchOrderList(): Single<List<Currency>>
    fun saveMultiplier(multiplier : Float) : Completable
    fun fetchMultiplier() : Single<Float>
}