package haroldolivieri.currencyexchanger.view

import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.domain.Currency

fun Currency.currencyName() : Int {
    return when(this) {
        Currency.AUD -> R.string.aud_currency_name
        Currency.BGN -> R.string.bgn_currency_name
        Currency.BRL -> R.string.brl_currency_name
        Currency.CAD -> R.string.cad_currency_name
        Currency.CHF -> R.string.chf_currency_name
        Currency.CNY -> R.string.cny_currency_name
        Currency.CZK -> R.string.czk_currency_name
        Currency.DKK -> R.string.dkk_currency_name
        Currency.GBP -> R.string.gbp_currency_name
        Currency.HKD -> R.string.hkd_currency_name
        Currency.HRK -> R.string.hrk_currency_name
        Currency.HUF -> R.string.huf_currency_name
        Currency.IDR -> R.string.idr_currency_name
        Currency.ILS -> R.string.ils_currency_name
        Currency.INR -> R.string.inr_currency_name
        Currency.ISK -> R.string.isk_currency_name
        Currency.JPY -> R.string.jpy_currency_name
        Currency.KRW -> R.string.krw_currency_name
        Currency.MXN -> R.string.mxn_currency_name
        Currency.MYR -> R.string.myr_currency_name
        Currency.NOK -> R.string.nok_currency_name
        Currency.NZD -> R.string.nzd_currency_name
        Currency.PHP -> R.string.php_currency_name
        Currency.PLN -> R.string.pln_currency_name
        Currency.RON -> R.string.ron_currency_name
        Currency.RUB -> R.string.rub_currency_name
        Currency.SEK -> R.string.sek_currency_name
        Currency.SGD -> R.string.sgd_currency_name
        Currency.THB -> R.string.thb_currency_name
        Currency.TRY -> R.string.try_currency_name
        Currency.USD -> R.string.usd_currency_name
        Currency.ZAR -> R.string.zar_currency_name
    }
}

fun Currency.currencyImage() : Int {
    return when(this) {
        Currency.AUD -> R.drawable.aud
        Currency.BGN -> R.drawable.bgn
        Currency.BRL -> R.drawable.brl
        Currency.CAD -> R.drawable.cad
        Currency.CHF -> R.drawable.chf
        Currency.CNY -> R.drawable.cny
        Currency.CZK -> R.drawable.czk
        Currency.DKK -> R.drawable.dkk
        Currency.GBP -> R.drawable.gbp
        Currency.HKD -> R.drawable.hkd
        Currency.HRK -> R.drawable.hrk
        Currency.HUF -> R.drawable.huf
        Currency.IDR -> R.drawable.idr
        Currency.ILS -> R.drawable.ils
        Currency.INR -> R.drawable.inr
        Currency.ISK -> R.drawable.isk
        Currency.JPY -> R.drawable.jpy
        Currency.KRW -> R.drawable.krw
        Currency.MXN -> R.drawable.mxn
        Currency.MYR -> R.drawable.myr
        Currency.NOK -> R.drawable.nok
        Currency.NZD -> R.drawable.nzd
        Currency.PHP -> R.drawable.php
        Currency.PLN -> R.drawable.pln
        Currency.RON -> R.drawable.ron
        Currency.RUB -> R.drawable.rub
        Currency.SEK -> R.drawable.sek
        Currency.SGD -> R.drawable.sgd
        Currency.THB -> R.drawable.thb
        Currency.TRY -> R.drawable.try_c
        Currency.USD -> R.drawable.usd
        Currency.ZAR -> R.drawable.zar
    }
}