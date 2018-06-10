package haroldolivieri.currencyexchanger.feature

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork
import dagger.android.support.DaggerAppCompatActivity
import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.feature.currencyList.CurrencyListActivity
import haroldolivieri.currencyexchanger.view.KeyboardUtils
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper


abstract class BaseActivity : DaggerAppCompatActivity() {

    protected val TAG : String = this::class.java.simpleName

    abstract val layout: Int
    abstract val internetChangesCallback : (Boolean) -> Unit
    abstract val keyboardVisibilityCallback : (Boolean) -> Unit

    private var snackBar: Snackbar? = null
    internal lateinit var keyboardUtils: KeyboardUtils

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setupFullscreenMode()

        ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { it -> internetChangesCallback.invoke(it)}

        keyboardUtils = KeyboardUtils(this, keyboardVisibilityCallback)
    }

    internal fun showSnackBar(view: View, message: Int, duration: Int = Snackbar.LENGTH_LONG) {
        if (snackBar != null && snackBar!!.isShown) {
            return
        }

        snackBar = Snackbar.make(view, message, duration)
        val snackBarView = snackBar?.view
        snackBarView?.setBackgroundColor(
                ContextCompat.getColor(this, R.color.colorGrayTransparent))

        val snackText = snackBar?.view?.
                findViewById<TextView>(android.support.design.R.id.snackbar_text)
        snackText?.setTextColor(ContextCompat.getColor(this, android.R.color.white))
        snackBar?.show()
    }

    internal  fun hideSnackBar() {
        snackBar?.dismiss()
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun setupFullscreenMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN)
        }
    }
}