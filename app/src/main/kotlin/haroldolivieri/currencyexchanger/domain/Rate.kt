package haroldolivieri.currencyexchanger.domain

import java.util.Date

data class Rate (val currencyBase : Currency,
                 val date: Date,
                 val rates : LinkedHashMap<Currency, String>)
