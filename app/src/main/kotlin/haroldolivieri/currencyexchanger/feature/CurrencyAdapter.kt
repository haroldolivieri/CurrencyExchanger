package haroldolivieri.currencyexchanger.feature

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import haroldolivieri.currencyexchanger.view.currencyImage
import haroldolivieri.currencyexchanger.view.currencyName
import io.reactivex.BackpressureStrategy
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class CurrencyAdapter(private var adapterList: MutableList<CurrencyItem>? = null,
                      private val itemClick: (currency: Currency) -> Unit) :
        RecyclerView.Adapter<CurrencyAdapter.CurrencyHolder>() {

    companion object {
        private val TAG = CurrencyAdapter::class.java.simpleName
    }

    private val subject : BehaviorSubject<Float> = BehaviorSubject.create()
    private var selectedCurrency : Currency? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency, parent, false)
        return CurrencyHolder(view)
    }

    fun setRates(rates: List<CurrencyItem>) {
        val sortedRatesList = mutableListOf<CurrencyItem>()

        this.adapterList?.forEachIndexed { index, oldCurrencyItem ->
            val newCurrencyItem = rates.find { oldCurrencyItem.currency == it.currency }
            newCurrencyItem?.let { sortedRatesList.add(index, it) }
        }

        this.adapterList =  if (!sortedRatesList.isEmpty()) sortedRatesList
        else rates.toMutableList()

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        adapterList?.get(position)?.let { holder.bind(it, itemClick)}
    }

    override fun onViewRecycled(holder: CurrencyHolder) {
        super.onViewRecycled(holder)
        holder.dispose()
    }

    override fun onViewDetachedFromWindow(holder: CurrencyHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.dispose()
    }

    override fun getItemCount(): Int = adapterList?.size ?: 0

    inner class CurrencyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val currencyImage = itemView.findViewById<ImageView>(R.id.currencyImage)
        private val currencyName = itemView.findViewById<TextView>(R.id.currencyName)
        private val currencyDescription = itemView.findViewById<TextView>(R.id.currencyDescription)
        private val amountInput = itemView.findViewById<EditText>(R.id.amountInput)

        private var textChangesDisposable : Disposable? = null
        private var amountChangesDisposable : Disposable? = null


        @SuppressLint("ClickableViewAccessibility")
        fun bind(currencyItem: CurrencyItem, itemClick: (currency: Currency) -> Unit) {
            val currency = currencyItem.currency
            val rate = currencyItem.rate

            currencyImage.setImageResource(currency.currencyImage())
            currencyName.text = currency.name
            currencyDescription.setText(currency.currencyName())

            amountInput.setOnFocusChangeListener { _, focused ->
                if (focused) {
                    stopObservingItems()
                    startEmittingItems(rate)
                } else {
                    stopEmittingItems()
                }
            }

            amountInput.setOnTouchListener { _, _ ->
                onCurrencySelected(currencyItem, itemClick)
                true
            }

            itemView.setOnClickListener {
                onCurrencySelected(currencyItem, itemClick)
            }

            startObservingItems(currencyItem, rate)
        }

        fun dispose() {
            stopEmittingItems()
            stopObservingItems()
        }

        private fun stopObservingItems() {
            if (amountChangesDisposable != null && !amountChangesDisposable?.isDisposed!!) {
                amountChangesDisposable?.dispose()
            }
        }

        private fun stopEmittingItems() {
            if (textChangesDisposable != null && !textChangesDisposable?.isDisposed!!) {
                textChangesDisposable?.dispose()
            }
        }

        private fun startObservingItems(currencyItem: CurrencyItem, rate: Float) {
            stopObservingItems()

            amountChangesDisposable = subject
                    .toFlowable(BackpressureStrategy.LATEST)
                    .subscribe { amountInBaseCurrency ->
                        Log.i(TAG, "${currencyItem.currency.name} -> " +
                                "$rate * $amountInBaseCurrency = ${amountInBaseCurrency * rate}")

                        if (amountInBaseCurrency == 0F) {
                            amountInput.setText("")
                            return@subscribe
                        }
                        amountInput.setText("%.2f".format(amountInBaseCurrency * rate))
                    }
        }

        private fun startEmittingItems(rate: Float) {
            stopEmittingItems()
            textChangesDisposable = RxTextView.textChanges(amountInput)
                    .subscribe {

                        if (it.isEmpty()) {
                            subject.onNext(0F)
                            return@subscribe
                        }

                        val typedAmount = it.toString().toFloat()
                        val amountInBaseCurrency = typedAmount / rate
                        subject.onNext(amountInBaseCurrency)
                    }
        }

        private fun onCurrencySelected(currencyItem: CurrencyItem,
                                       itemClick: (currency: Currency) -> Unit) {
            amountInput.requestFocus()
            itemSelected(currencyItem)
            itemClick.invoke(currencyItem.currency)

            (itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE)
                    as InputMethodManager)
                    .showSoftInput(amountInput, InputMethodManager.SHOW_IMPLICIT)
        }

        private fun itemSelected(currencyItem: CurrencyItem) {
            layoutPosition.also { currentPosition ->
                selectedCurrency = currencyItem.currency
                adapterList?.removeAt(currentPosition).also {
                    adapterList?.add(0, it!!)
                }
                notifyItemMoved(currentPosition, 0)
            }
        }
    }
}