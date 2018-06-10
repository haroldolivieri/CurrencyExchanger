package haroldolivieri.currencyexchanger.repository

import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import haroldolivieri.currencyexchanger.repository.local.LocalStore
import haroldolivieri.currencyexchanger.repository.local.SharedPreferenceLocalStore
import haroldolivieri.currencyexchanger.repository.remote.CurrencyRatingService
import io.reactivex.Single
import io.reactivex.functions.BiFunction
import java.util.*
import javax.inject.Inject

class CurrencyRepository
@Inject constructor(private val service: CurrencyRatingService,
                    @SharedPreferenceLocalStore private val localStore: LocalStore)
    : Repository {

    override fun saveInputtedAmount(inputtedAmount: String) {
        localStore.saveInputtedAmount(inputtedAmount).subscribe()
    }

    override fun saveNewSortList(newCurrencyOrder: List<Currency>) {
        localStore.saveNewOrder(newCurrencyOrder).subscribe()
    }

    override fun fetchInputtedAmount(): Single<String> = localStore.fetchInputtedAmount()

    override fun fetchOrderedCurrencies(): Single<List<CurrencyItem>> =
            Single.zip(currencyItemsStream(), orderListStream(),
                    BiFunction<MutableList<CurrencyItem>,
                            List<Currency>, List<CurrencyItem>> { items, order ->

                        // Alphabetical sort of the main list
                        items.sortWith(Comparator { c1, c2 ->
                            c1.currency.name.compareTo(c2.currency.name)
                        })

                        //Check if there is no previous order cached
                        if (order.isEmpty()) {
                            return@BiFunction items
                        }

                        //Reorder list according cached order
                        val sortedRatesList = mutableListOf<CurrencyItem>()

                        try {
                            order.forEachIndexed { index, orderedCurrencyItem ->
                                val newCurrencyItem = items.find { orderedCurrencyItem == it.currency }
                                newCurrencyItem?.let { sortedRatesList.add(index, it) }
                            }
                        } catch (e : Exception) {
                            return@BiFunction items
                        }

                        if (sortedRatesList.size < items.size) {
                            items
                        } else {
                            sortedRatesList
                        }

                    })

    private fun currencyItemsStream():
            Single<MutableList<CurrencyItem>> =
            service.getRatesByCurrencyBase()
                    .flatMap { rate ->
                        /*
                            Adding EUR to the main list
                         */
                        val items = rate.rates.map { CurrencyItem(it.key, it.value) }
                                as MutableList<CurrencyItem>
                        items.add(CurrencyItem(rate.currencyBase, 1F))
                        Single.fromCallable { items }
                    }

    private fun orderListStream(): Single<List<Currency>> = localStore.fetchOrderList()
}