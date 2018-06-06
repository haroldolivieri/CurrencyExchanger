package haroldolivieri.currencyexchanger.domain

data class CurrencyItem(val currency: Currency,
                        val rate: Float = 0F) {
    override fun equals(other: Any?): Boolean {
        if (other is CurrencyItem) {
            return this.currency == other.currency
        }

        return false
    }

    override fun hashCode(): Int = currency.hashCode()
}
