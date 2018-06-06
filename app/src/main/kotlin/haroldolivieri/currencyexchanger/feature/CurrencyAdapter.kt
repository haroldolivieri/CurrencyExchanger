package haroldolivieri.currencyexchanger.feature

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
import io.reactivex.subjects.PublishSubject

class CurrencyAdapter(private var rates: List<CurrencyItem>? = null,
                      private val itemClick: (position: Int) -> Unit) :
        RecyclerView.Adapter<CurrencyHolder>() {

    private val subject : BehaviorSubject<Float> = BehaviorSubject.create()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency, parent, false)
        return CurrencyHolder(view, subject)
    }

    fun setRates(rates: List<CurrencyItem>) {
        this.rates = rates
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        rates?.get(position)?.let { holder.bind(it, itemClick)}
    }

    override fun onViewRecycled(holder: CurrencyHolder) {
        super.onViewRecycled(holder)
        holder.stopEmittingItems()
        holder.stopObservingItems()
    }

    override fun getItemCount(): Int = rates?.size ?: 0
}

class CurrencyHolder(itemView: View, private val subject: BehaviorSubject<Float>)
    : RecyclerView.ViewHolder(itemView) {

    private val currencyImage = itemView.findViewById<ImageView>(R.id.currencyImage)
    private val currencyName = itemView.findViewById<TextView>(R.id.currencyName)
    private val currencyDescription = itemView.findViewById<TextView>(R.id.currencyDescription)
    private val amountInput = itemView.findViewById<EditText>(R.id.amountInput)

    private var textChangesDisposable : Disposable? = null
    private var amountChangesDisposable : Disposable? = null

    fun bind(currencyItem: CurrencyItem, itemClick: (position: Int) -> Unit) {

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
                startObservingItems(currencyItem, rate)
                (itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromInputMethod(amountInput.windowToken, InputMethodManager.SHOW_FORCED)
            }
        }

        itemView.setOnClickListener {
            animateToFirstResponder()
            itemClick.invoke(position)
            amountInput.requestFocus()
            (itemView.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .showSoftInput(amountInput, InputMethodManager.SHOW_FORCED)
        }

        startObservingItems(currencyItem, rate)
    }

    fun stopObservingItems() {
        if (amountChangesDisposable != null && !amountChangesDisposable?.isDisposed!!) {
            amountChangesDisposable?.dispose()
        }
    }

    fun stopEmittingItems() {
        if (textChangesDisposable != null && !textChangesDisposable?.isDisposed!!) {
            textChangesDisposable?.dispose()
        }
    }

    private fun startObservingItems(currencyItem: CurrencyItem, rate: Float) {
        stopObservingItems()

        amountChangesDisposable = subject
                .toFlowable(BackpressureStrategy.LATEST)
                .subscribe { amountInBaseCurrency ->
            Log.i("CurrencyHolder", "${currencyItem.currency.name} -> " +
                    "$rate * $amountInBaseCurrency = ${amountInBaseCurrency * rate}")

            if (amountInBaseCurrency == 0F) {
                amountInput.setText("")
                return@subscribe
            }

            currencyItem.rateMultiplier = amountInBaseCurrency
            amountInput.setText("%.2f".format(amountInBaseCurrency * rate))
        }
    }

    private fun startEmittingItems(rate: Float) {
        stopEmittingItems()
        textChangesDisposable = RxTextView.textChanges(amountInput)
                .subscribe {

                    animateToFirstResponder()

                    if (it.isEmpty()) {
                        subject.onNext(0F)
                        return@subscribe
                    }

                    val typedAmount = it.toString().toFloat()
                    val amountInBaseCurrency = typedAmount / rate
                    subject.onNext(amountInBaseCurrency)
                }
    }

    private fun animateToFirstResponder() {}
}
