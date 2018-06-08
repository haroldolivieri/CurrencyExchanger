package haroldolivieri.currencyexchanger.feature

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.View
import dagger.android.support.DaggerAppCompatActivity

import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import kotlinx.android.synthetic.main.activity_main.*
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper
import java.util.*
import javax.inject.Inject
import haroldolivieri.currencyexchanger.view.KeyboardUtils
import android.support.v4.content.ContextCompat
import android.os.Build
import android.view.WindowManager




class CurrencyListActivity : DaggerAppCompatActivity(), CurrencyListContract.View {

    companion object {
        private val TAG = CurrencyListActivity::class.java.simpleName
    }

    @Inject
    lateinit var currencyPresenter: CurrencyListContract.Presenter

    private val currencyAdapter by lazy {
        CurrencyAdapter(itemClick = {
            currencyList.scrollToPosition(0)
            currencyList.adapter.notifyDataSetChanged()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        currencyPresenter.onCreate()

        setupRecyclerView()
        setupFullscreenMode()
        setupKeyboardBehavior()
        setupCollapseToolbarBehavior()
    }

    override fun onDestroy() {
        super.onDestroy()
        KeyboardUtils.removeAllKeyboardToggleListeners()
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

    private fun setupCollapseToolbarBehavior() {
        setAppBarLayoutBehavior()
        setToolbarTitle()
    }

    private fun setupRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(this)
        val dividerItemDecoration = DividerItemDecoration(currencyList.context,
                linearLayoutManager.orientation)

        currencyList.addItemDecoration(dividerItemDecoration)
        currencyList.layoutManager = linearLayoutManager
        currencyList.adapter = currencyAdapter
        currencyList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                KeyboardUtils.closeKeyboard(findViewById<View>(android.R.id.content))
            }
        })
    }

    private fun setupKeyboardBehavior() {
        KeyboardUtils.addKeyboardToggleListener(this@CurrencyListActivity,
                object : KeyboardUtils.SoftKeyboardToggleListener {
                    override fun onToggleSoftKeyboard(isVisible: Boolean) {
                        if (!isVisible) {
                            focusThief.requestFocus()
                            currencyAdapter.resetSelectedCurrency()
                        }
                    }
                })
    }

    private fun setToolbarTitle() {
        collapsingToolbar.title = getString(R.string.app_name)
        val tf = Typeface.createFromAsset(assets, getString(R.string.font_montserrat_semi_bold))
        collapsingToolbar.setCollapsedTitleTypeface(tf)
        collapsingToolbar.setExpandedTitleTypeface(tf)
    }

    private fun setAppBarLayoutBehavior() {
        appBarLayout.addOnOffsetChangedListener { _, verticalOffSet ->
            if (Math.abs(verticalOffSet) == appBarLayout.totalScrollRange) {
                toolBar.background = ContextCompat.getDrawable(this,
                        R.drawable.last_revolut_gradient)
            } else {
                toolBar.setBackgroundColor(ContextCompat.getColor(this,
                        android.R.color.transparent))
            }
        }

        appBarLayout.background = ContextCompat
                .getDrawable(this, R.drawable.revolut_gradient)
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setupFullscreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}
