package haroldolivieri.currencyexchanger.view

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.content.Context
import android.view.inputmethod.InputMethodManager

import java.util.HashMap

class KeyboardUtils private constructor(activity: Activity,
                                        private var callback: SoftKeyboardToggleListener?) :
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

    interface SoftKeyboardToggleListener {
        fun onToggleSoftKeyboard(isVisible: Boolean)
    }

    override fun onGlobalLayout() {
        val rect = Rect()
        rootView.getWindowVisibleDisplayFrame(rect)

        val heightDiff = rootView.rootView.height - (rect.bottom - rect.top)
        val dp = heightDiff / screenDensity
        val isVisible = dp > MAGIC_NUMBER

        if (callback != null && (previousValue == null || isVisible != previousValue)) {
            previousValue = isVisible
            callback!!.onToggleSoftKeyboard(isVisible)
        }
    }

    private fun removeListener() {
        callback = null
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    companion object {
        private const val MAGIC_NUMBER = 200
        private var listenerMap = HashMap<SoftKeyboardToggleListener, KeyboardUtils>()

        fun addKeyboardToggleListener(act: Activity, listener: SoftKeyboardToggleListener) {
            removeKeyboardToggleListener(listener)
            listenerMap[listener] = KeyboardUtils(act, listener)
        }

        private fun removeKeyboardToggleListener(listener: SoftKeyboardToggleListener) {
            if (listenerMap.containsKey(listener)) {
                val keyboardUtils : KeyboardUtils? = listenerMap[listener]
                keyboardUtils?.removeListener()
                listenerMap.remove(listener)
            }
        }

        fun removeAllKeyboardToggleListeners() {
            for (listener in listenerMap.keys)
                listenerMap[listener]?.removeListener()

            listenerMap.clear()
        }

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
