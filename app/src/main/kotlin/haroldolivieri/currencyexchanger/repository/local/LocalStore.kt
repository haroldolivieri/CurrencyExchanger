package haroldolivieri.currencyexchanger.repository.local

import haroldolivieri.currencyexchanger.domain.Currency
import io.reactivex.Completable
import io.reactivex.Single

interface LocalStore {
    fun saveNewOrder(orderList: List<Currency>): Completable
    fun fetchOrderList(): Single<List<Currency>>
    fun saveInputtedAmount(inputtedAmount : Float) : Completable
    fun fetchInputtedAmount() : Single<Float>
}