package haroldolivieri.currencyexchanger.domain

import java.util.Date

data class Rate(val base : String, val date: Date, val rates : Map<String, String>)
