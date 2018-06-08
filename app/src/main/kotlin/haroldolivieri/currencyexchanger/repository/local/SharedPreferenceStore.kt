package haroldolivieri.currencyexchanger.repository.local

import android.annotation.SuppressLint
import android.content.SharedPreferences
import haroldolivieri.currencyexchanger.domain.Currency
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferenceStore
@Inject constructor(private val sharedPreferences: SharedPreferences) : LocalStore {

    companion object {
        private const val ORDERED_LIST = "orderedList"
        private const val MULTIPLIER = "multiplier"
    }

    private val gson = Gson()

    @SuppressLint("CommitPrefEdits")
    override fun saveNewOrder(orderList: List<Currency>): Completable {
        return Completable.fromCallable {
            val json = gson.toJson(orderList)
            sharedPreferences
                    .edit()
                    .putString(ORDERED_LIST, json.toString())
                    .apply()
        }
    }

    override fun fetchOrderList(): Single<List<Currency>> {
        val json = sharedPreferences.getString(ORDERED_LIST, "[]")
        val listType = object : TypeToken<List<Currency>>() {}.type

        return Single.fromCallable { gson.fromJson<List<Currency>>(json, listType) }
    }

    override fun saveMultiplier(multiplier: Float): Completable {
        return Completable.fromCallable {
            sharedPreferences
                    .edit()
                    .putFloat(MULTIPLIER, multiplier)
                    .apply()
        }
    }

    override fun fetchMultiplier(): Single<Float> {
        return Single.fromCallable { sharedPreferences.getFloat(MULTIPLIER, 0F) }
    }
}