package haroldolivieri.currencyexchanger.feature.currencyList

import android.graphics.Typeface
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import javax.inject.Inject
import android.support.v4.content.ContextCompat
import android.util.Log
import haroldolivieri.currencyexchanger.feature.BaseActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.currency_list_content.*

class CurrencyListActivity(override val layout : Int = R.layout.activity_main) :
        BaseActivity(), CurrencyListContract.View {

    @Inject
    lateinit var currencyPresenter: CurrencyListContract.Presenter

    override val keyboardVisibilityCallback: (Boolean) -> Unit = { isVisible ->
        if (!isVisible) {
            focusThief.requestFocus()
            currencyAdapter.resetSelectedCurrency()
        }
    }

    override val internetChangesCallback: (Boolean) -> Unit = { connected ->
        if (!connected) {
            showSnackBar(currencyList, R.string.check_internet_conn, Snackbar.LENGTH_INDEFINITE)
        } else {
            hideSnackBar()
        }
    }

    private val currencyAdapter by lazy {
        CurrencyAdapter(changeInputtedAmount = {
            currencyPresenter.saveNewInputtedAmount(it)
        }, changeSavedOrder = {
            currencyPresenter.saveNewSortList(it)
        }, afterOrderChanged = {
            currencyList.adapter.notifyItemMoved(it, 0)
            Handler().postDelayed({currencyList.adapter.notifyDataSetChanged()}, 250)
        }, openKeyboard = {
            keyboardUtils.showKeyboard(it)
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currencyPresenter.onCreate()

        setupRecyclerView()
        setupCollapseToolbarBehavior()
    }

    override fun onDestroy() {
        super.onDestroy()
        currencyPresenter.onDestroy()
    }

    override fun showCurrencyList(rates: List<CurrencyItem>) {
        currencyAdapter.setRates(rates)
    }

    override fun updateInputtedAmount(cachedInputtedAmount: String) {
        currencyAdapter.setInputtedAmount(cachedInputtedAmount)
    }

    override fun showError(message: String?) {
        Log.d(TAG, "${getString(R.string.generic_error)} -> $message")
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
        currencyList.setEmptyView(emptyView)
        currencyList.addOnScrollListener(onScrollListener())
    }

    private fun onScrollListener(): RecyclerView.OnScrollListener {
        return object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                keyboardUtils.closeKeyboard(findViewById<View>(android.R.id.content))
            }
        }
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
}
