package haroldolivieri.currencyexchanger.feature

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.view.RxView
import com.jakewharton.rxbinding2.widget.RxTextView
import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.domain.CurrencyItem
import haroldolivieri.currencyexchanger.view.KeyboardUtils
import haroldolivieri.currencyexchanger.view.currencyImage
import haroldolivieri.currencyexchanger.view.currencyName
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

class CurrencyAdapter(private var adapterList: MutableList<CurrencyItem>? = null,
                      private val itemClick: () -> Unit) :
        RecyclerView.Adapter<CurrencyAdapter.CurrencyHolder>() {

    companion object {
        private val TAG = CurrencyAdapter::class.java.simpleName
    }

    private var selectedCurrency: Currency? = null
    private var multiplier = 0F

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency, parent, false)
        return CurrencyHolder(view)
    }

    fun resetSelectedCurrency() {
        selectedCurrency = null
    }

    fun setRates(rates: List<CurrencyItem>) {
        val sortedRatesList = mutableListOf<CurrencyItem>()

        this.adapterList?.forEachIndexed { index, oldCurrencyItem ->
            val newCurrencyItem = rates.find { oldCurrencyItem.currency == it.currency }
            newCurrencyItem?.let { sortedRatesList.add(index, it) }
        }

        this.adapterList = if (!sortedRatesList.isEmpty()) sortedRatesList
        else rates.toMutableList()

        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        adapterList?.get(position)?.let { holder.bind(it) }
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

    private var inputedAmount: String = ""

    inner class CurrencyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val currencyImage = itemView
                .findViewById<ImageView>(R.id.currencyImage)
        private val currencyName = itemView
                .findViewById<TextView>(R.id.currencyName)
        private val currencyDescription = itemView
                .findViewById<TextView>(R.id.currencyDescription)
        private val amountInput = itemView
                .findViewById<EditText>(R.id.amountInput)

        private var textChangesDisposable: Disposable? = null
        private var disposables: CompositeDisposable = CompositeDisposable()

        @SuppressLint("ClickableViewAccessibility")
        fun bind(currencyItem: CurrencyItem) {

            val currency = currencyItem.currency
            val rate = currencyItem.rate

            currencyImage.setImageResource(currency.currencyImage())
            currencyName.text = currency.name
            currencyDescription.setText(currency.currencyName())

            stopEmittingTextChanges()

            when (selectedCurrency) {
                currency -> {
                    amountInput.requestFocus()
                    amountInput.setText(inputedAmount)
                    amountInput.setSelection(amountInput.text.length)
                    startEmittingTextChanges(currencyItem, amountInput)
                }
                else -> {
                    val amount = multiplier * rate
                    if (amount > 0F) {
                        amountInput.setText("%.0f".format(amount))
                    } else {
                        amountInput.setText("")
                    }
                }
            }

            disposables.add(Observable.merge(RxView.touches(amountInput), RxView.clicks(itemView))
                    .toFlowable(BackpressureStrategy.LATEST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        itemSelected()
                    })
        }

        fun dispose() {
            stopEmittingTextChanges()
            disposables.clear()
        }

        private fun startEmittingTextChanges(currencyItem: CurrencyItem,
                                             input : EditText) {
            stopEmittingTextChanges()
            textChangesDisposable = RxTextView.textChanges(input)
                    .toFlowable(BackpressureStrategy.LATEST)
                    .subscribe {
                        if (it.isEmpty() || it.toString().toFloat() == 0F) {
                            inputedAmount = ""
                            multiplier = 0F
                            return@subscribe
                        }

                        inputedAmount = it.toString()
                        Log.e(TAG, "${currencyItem.currency} || $selectedCurrency => " +
                                "$inputedAmount/ ${currencyItem.rate} = " +
                                "${inputedAmount.toFloat()/currencyItem.rate}")

                        val amountInBaseCurrency = inputedAmount.toFloat() / currencyItem.rate
                        multiplier = amountInBaseCurrency
                        notifyDataSetChanged()
                    }
        }

        private fun itemSelected() {
            layoutPosition.also { currentPosition ->
                selectedCurrency = adapterList?.get(currentPosition)?.currency
                inputedAmount = amountInput.text.toString()
//                adapterList?.removeAt(currentPosition).also {
//                    adapterList?.add(0, it!!)
//                }
//                notifyItemMoved(currentPosition, 0)
//                itemClick.invoke()
            }

            amountInput.requestFocus()
            KeyboardUtils.showKeyboard(amountInput)
        }

        private fun stopEmittingTextChanges() {
            if (textChangesDisposable != null && !textChangesDisposable?.isDisposed!!) {
                textChangesDisposable?.dispose()
            }
        }
    }
}