package haroldolivieri.currencyexchanger.remote

import haroldolivieri.currencyexchanger.domain.Rate
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatingService {
    @GET("/latest")
    fun getRatesbyCurrencyBase(@Query("currencyBase") base : String) : Single<Rate>
}