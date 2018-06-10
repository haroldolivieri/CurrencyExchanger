package haroldolivieri.currencyexchanger.feature.currentList

import android.content.SharedPreferences
import com.google.gson.Gson
import haroldolivieri.currencyexchanger.domain.Currency.*
import haroldolivieri.currencyexchanger.repository.local.SharedPreferenceStore
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Spy
import org.mockito.InjectMocks
import org.mockito.junit.MockitoJUnit
import org.mockito.junit.MockitoRule
import org.mockito.Mockito.*
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner

@RunWith(PowerMockRunner::class)
@PrepareForTest(Gson::class)
class LocalStoreTest {

    @JvmField
    @Rule
    var rule: MockitoRule = MockitoJUnit.rule()

    @Mock
    private lateinit var sharedPreferences: SharedPreferences
    @Mock
    lateinit var editor: SharedPreferences.Editor

    @InjectMocks
    @Spy
    private lateinit var sharedPreferencesStore: SharedPreferenceStore

    private val orderList = listOf(HKD, PLN, EUR, BRL, THB, TRY, RON, ZAR, BGN)
    private val orderListJson by lazy { Gson().toJson(orderList) }

    @Test
    fun test_persist_new_inputted_amount() {
        val inputtedAmount = "123"
        `when`(sharedPreferences.edit()).thenReturn(editor)

        sharedPreferencesStore.saveInputtedAmount(inputtedAmount).subscribe()
        verify(editor).putString("inputtedAmount", inputtedAmount)
    }

    @Test
    fun test_read_inputted_amount() {
        `when`(sharedPreferences.getString("inputtedAmount", "0")).thenReturn("123")
        val testObserver = sharedPreferencesStore.fetchInputtedAmount().test()
        testObserver.assertComplete()
        testObserver.assertNoErrors()
        testObserver.assertValue {
            it == "123"
        }
    }

    @Test
    fun test_persist_new_order_list() {
        val gson = PowerMockito.mock(Gson::class.java)
        `when`(gson.toJson(orderList)).thenReturn(orderListJson)

        `when`(sharedPreferences.edit()).thenReturn(editor)

        sharedPreferencesStore.saveNewOrder(orderList).subscribe()
        verify(editor).putString("orderedList", orderListJson)
    }

    @Test
    fun test_read_order_list() {
        `when`(sharedPreferences.getString("orderedList", "[]"))
                .thenReturn(orderListJson)

        val testObserver = sharedPreferencesStore.fetchOrderList().test()
        testObserver.assertComplete()
        testObserver.assertNoErrors()

        testObserver.assertValue {
            it.size == 9 && it == orderList
        }
    }
}
