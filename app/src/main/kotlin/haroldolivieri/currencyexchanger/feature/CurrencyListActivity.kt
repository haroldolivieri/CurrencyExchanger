package haroldolivieri.currencyexchanger.feature

import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity

import haroldolivieri.currencyexchanger.R

class CurrencyListActivity : DaggerAppCompatActivity(), CurrencyListContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun showCurrencyList() {
        TODO("not implemented")
    }
}
