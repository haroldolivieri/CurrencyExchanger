package haroldolivieri.currencyexchanger.view

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.content.Context
import android.view.inputmethod.InputMethodManager

import java.util.HashMap

class KeyboardUtils (activity: Activity, private var onToggleSoftKeyboard: (Boolean) -> Unit) :
        ViewTreeObserver.OnGlobalLayoutListener {

    private val rootView: View by lazy {
        (activity.findViewById<View>(android.R.id.content) as ViewGroup).getChildAt(0)
    }

    private var previousValue: Boolean? = null
    private val screenDensity: Float

    init {
        rootView.viewTreeObserver.addOnGlobalLayoutListener(this)
        screenDensity = activity.resources.displayMetrics.density
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)

        val heightDiff = rootView.rootView.height - (rect.bottom - rect.top)
        val dp = heightDiff / screenDensity
        val isVisible = dp > MAGIC_NUMBER

        if (previousValue == null || isVisible != previousValue) {
            previousValue = isVisible
            onToggleSoftKeyboard.invoke(isVisible)
        }
    }

    companion object {
        private const val MAGIC_NUMBER = 200

        fun showKeyboard(activeView: View) {
            val inputMethodManager = activeView.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.showSoftInput(activeView, InputMethodManager.SHOW_IMPLICIT)
        }

        fun closeKeyboard(activeView: View) {
            val inputMethodManager = activeView.context
                    .getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activeView.windowToken, 0)
        }
    }
}
