package haroldolivieri.currencyexchanger.domain

data class CurrencyItem(val currency: Currency, val rate: Float, var rateMultiplier: Float = 0F)
