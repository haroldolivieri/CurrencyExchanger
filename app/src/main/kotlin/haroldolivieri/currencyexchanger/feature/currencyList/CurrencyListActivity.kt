package haroldolivieri.currencyexchanger.feature.currencyList

import android.graphics.Typeface
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import kotlinx.android.synthetic.main.activity_main.*
import javax.inject.Inject
import haroldolivieri.currencyexchanger.view.KeyboardUtils
import android.support.v4.content.ContextCompat
import haroldolivieri.currencyexchanger.feature.BaseActivity

class CurrencyListActivity(override val layout : Int = R.layout.activity_main) :
        BaseActivity(), CurrencyListContract.View {

    override val internetChangesCallback: (Boolean) -> Unit = { connected ->
        if (!connected) {
            showSnackBar(currencyList, R.string.check_internet_conn, Snackbar.LENGTH_INDEFINITE)
        } else {
            hideSnackBar()
        }
    }

    @Inject
    lateinit var currencyPresenter: CurrencyListContract.Presenter

    private val currencyAdapter by lazy {
        CurrencyAdapter(changeSavedOrder = {
            currencyPresenter.saveNewSortList(it)
        }, changeInputtedAmount = {
            currencyPresenter.saveNewMultiplier(it)
        }, afterMoveAnimation = {
            currencyList.scrollToPosition(0)
            currencyList.adapter.notifyDataSetChanged()
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        currencyPresenter.onCreate()

        setupRecyclerView()
        setupCollapseToolbarBehavior()

        setupKeyboardListener({ isVisible ->
            if (!isVisible) {
                focusThief.requestFocus()
                currencyAdapter.resetSelectedCurrency()
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        currencyPresenter.onDestroy()
    }

    override fun showCurrencyList(rates: List<CurrencyItem>) {
        hideSnackBar()
        currencyAdapter.setRates(rates)
    }

    override fun updateInputtedAmount(cachedInputtedAmount: String) {
        currencyAdapter.setInputtedAmount(cachedInputtedAmount)
    }

    override fun showError(message: String?) {
        showSnackBar(currencyList, R.string.generic_error, Snackbar.LENGTH_INDEFINITE)
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
