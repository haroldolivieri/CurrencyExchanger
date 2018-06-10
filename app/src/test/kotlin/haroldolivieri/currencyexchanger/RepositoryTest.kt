package haroldolivieri.currencyexchanger

import haroldolivieri.currencyexchanger.repository.CurrencyRepository
import haroldolivieri.currencyexchanger.repository.local.SharedPreferenceStore
import haroldolivieri.currencyexchanger.repository.remote.CurrencyRatingService
import org.junit.Rule
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.Currency.*
import haroldolivieri.currencyexchanger.repository.remote.RateResponse
import io.reactivex.Single.just
import org.mockito.Mockito.`when`
import java.util.*
import org.junit.Assert.*


class RepositoryTest {

    @JvmField @Rule var rule: MockitoRule = MockitoJUnit.rule()

    @Mock private lateinit var currencyRatingService: CurrencyRatingService
    @Mock private lateinit var localStore: SharedPreferenceStore

    @InjectMocks private lateinit var repository: CurrencyRepository

    private val serviceRates =
            hashMapOf(AUD to 4.3F, BGN to 2.4F, CAD to 1.5F, BRL to 2.4F)

    @Test
    fun test_zip_lists_with_same_number_of_retrieved_and_saved_items() {
        mockCurrencyRatingService()

        //items requested from server in the same order as saved
        val orderedList = listOf(BGN, BRL, AUD, CAD, EUR)
        mockLocalStore(orderedList)
        assertTestObserver(orderedList)
    }

    @Test
    fun test_zip_lists_with_more_retrieved_items_NOT_all_included() {
        mockCurrencyRatingService()
        mockLocalStore(listOf(BGN, BRL, AUD, CAD))

        //items requested from server in alphabetical order + euro
        assertTestObserver(listOf(AUD, BGN, BRL, CAD, EUR))
    }

    @Test
    fun test_zip_lists_with_less_retrieved_items_all_included() {
        mockCurrencyRatingService()
        mockLocalStore(listOf(BGN, BRL, AUD, CAD, EUR, RUB, HKD))

        //items requested from server in the same order as saved
        assertTestObserver(listOf(BGN, BRL, AUD, CAD, EUR))
    }

    @Test
    fun test_zip_lists_with_less_saved_items_NOT_all_included() {
        mockCurrencyRatingService()
        mockLocalStore(listOf(BGN, RUB, AUD))

        //items requested from server in alphabetical order + euro
        assertTestObserver(listOf(AUD, BGN, BRL, CAD, EUR))
    }

    @Test
    fun test_zip_lists_with_more_saved_items_NOT_all_included() {
        mockCurrencyRatingService()

        mockLocalStore(listOf(AUD, BGN, BRL, CAD, EUR, HKD))

        //items requested from server in alphabetical order + euro
        assertTestObserver(listOf(AUD, BGN, BRL, CAD, EUR))
    }

    @Test
    fun test_retrieve_data_based_in_other_currency() {
        mockCurrencyRatingService(MXN.name)
        mockLocalStore(listOf())

        //items requested from server in alphabetical order + mxn
        assertTestObserver(listOf(AUD, BGN, BRL, CAD, MXN))
    }

    private fun mockLocalStore(orderedList: List<Currency>) {
        `when`(localStore.fetchOrderList()).thenReturn(just(orderedList))
    }

    private fun mockCurrencyRatingService(baseCurrency: String = EUR.name) {
        `when`(currencyRatingService.getRatesByCurrencyBase())
                .thenReturn(just(RateResponse(valueOf(baseCurrency), Date(), serviceRates)))
    }

    private fun assertTestObserver(rightOrderList: List<Currency> = listOf()) {
        val testObserver = repository.fetchOrderedCurrencies().test()

        testObserver.assertComplete()
        testObserver.assertNoErrors()
        assertTrue(testObserver.values()[0].size == rightOrderList.size)

        val currencies = testObserver.values()[0].map { it.currency }
        assertTrue(currencies == rightOrderList)
    }
}