package haroldolivieri.currencyexchanger.repository.remote

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface CurrencyRatingService {
    @GET("/latest")
    fun getRatesByCurrencyBase(@Query("base") currencyBase : String) : Single<RateResponse>
}