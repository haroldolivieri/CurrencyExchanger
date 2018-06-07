package haroldolivieri.currencyexchanger.remote

import com.google.gson.annotations.SerializedName
import haroldolivieri.currencyexchanger.domain.Currency
import java.util.Date

data class Rate (@SerializedName("base") val currencyBase : Currency,
                 val date: Date,
                 val rates : HashMap<Currency, Float>)
