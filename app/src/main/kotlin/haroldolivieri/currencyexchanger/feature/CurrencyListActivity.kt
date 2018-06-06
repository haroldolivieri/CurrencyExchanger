package haroldolivieri.currencyexchanger.feature

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import dagger.android.support.DaggerAppCompatActivity

import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*
import javax.inject.Inject

class CurrencyListActivity : DaggerAppCompatActivity(), CurrencyListContract.View {

    companion object {
        private val TAG = CurrencyListActivity::class.java.simpleName
    }

    @Inject lateinit var currencyPresenter : CurrencyListContract.Presenter

    private val currencyAdapter by lazy {
        CurrencyAdapter(itemClick = {
            currencyList.scrollToPosition(0)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setupRecyclerView()
        currencyPresenter.onCreate()
    }

    override fun onDestroy() {
        super.onDestroy()
        currencyPresenter.onDestroy()
    }
    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun showCurrencyList(rates: List<CurrencyItem>) {
        currencyAdapter.setRates(rates)
    }

    override fun showRateInfo(date: Date, base: Currency) {
        Log.i(TAG, "$date & $base")
    }

    override fun showError(message: String?) {
        Log.e(TAG, "$message")
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(currencyList.context,
                linearLayoutManager.orientation)

        currencyList.addItemDecoration(dividerItemDecoration)
        currencyList.layoutManager = linearLayoutManager
        currencyList.adapter = currencyAdapter
    }

}
