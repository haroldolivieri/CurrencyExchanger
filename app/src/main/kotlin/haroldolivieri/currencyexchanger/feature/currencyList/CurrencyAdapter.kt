package haroldolivieri.currencyexchanger.feature.currencyList

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
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
import io.reactivex.BackpressureStrategy
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject
import haroldolivieri.currencyexchanger.view.*
import java.text.DecimalFormat


class CurrencyAdapter(private var adapterList: MutableList<CurrencyItem>? = null,
                      private val changeSavedOrder: (List<Currency>) -> Unit,
                      private val changeInputtedAmount: (String) -> Unit,
                      private val afterMoveAnimation: () -> Unit) :
        RecyclerView.Adapter<CurrencyAdapter.CurrencyHolder>() {

    private val subject: BehaviorSubject<Float> = BehaviorSubject.create()
    private var selectedCurrency: Currency? = null

    private var inputtedAmount: String = ""
        get() { return if (field == "0") ""  else field }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency, parent, false)
        return CurrencyHolder(view)
    }

    fun resetSelectedCurrency() {
        selectedCurrency = null
    }

    fun setInputtedAmount(cachedAmount : String) {
        inputtedAmount = cachedAmount
    }

    fun setRates(rates: List<CurrencyItem>) {
        this.adapterList = rates.toMutableList()
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
        private var amountChangesDisposable: Disposable? = null
        private var disposables: CompositeDisposable = CompositeDisposable()

        @SuppressLint("ClickableViewAccessibility")
        fun bind(currencyItem: CurrencyItem) {
            val currency = currencyItem.currency
            val rate = currencyItem.rate

            if (layoutPosition == 0) {
                amountInput.setText(inputtedAmount)
                stopObservingItems()
                startEmittingItems(rate)

                if (selectedCurrency == currency) {
                    openKeyboard()
                    amountInput.limitLength(9, 2)
                }

            } else {
                amountInput.clearFocus()
                amountInput.resetLimitLength()
                startObservingItems(rate)
                stopEmittingItems()
            }

            currencyImage.setImageResource(currency.currencyImage())
            currencyName.text = currency.name
            currencyDescription.setText(currency.currencyName())

            disposables.add(Observable.merge(RxView.touches(amountInput), RxView.clicks(itemView))
                    .toFlowable(BackpressureStrategy.LATEST)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe {
                        onCurrencySelected()
                    })
        }

        fun dispose() {
            stopEmittingItems()
            stopObservingItems()
            disposables.clear()
        }

        private fun startObservingItems(rate: Float) {
            stopObservingItems()

            amountChangesDisposable = subject
                    .toFlowable(BackpressureStrategy.LATEST)
                    .subscribe { amountInBaseCurrency ->
                        if (amountInBaseCurrency <= 0F) {
                            amountInput.setText("")
                            return@subscribe
                        }

                        val df = DecimalFormat("#.##")
                        amountInput.setText(df.format(amountInBaseCurrency * rate))
                    }
        }

        private fun startEmittingItems(rate: Float) {
            stopEmittingItems()

            textChangesDisposable = RxTextView.textChanges(amountInput)
                    .map { if (it.isEmpty()) "0" else it}
                    .toFlowable(BackpressureStrategy.LATEST)
                    .subscribe {
                        val typedAmount = it.toString()
                        val multiplier = typedAmount.toFloat() / rate

                        subject.onNext(multiplier)
                        inputtedAmount = typedAmount
                        changeInputtedAmount.invoke(typedAmount)
                    }
        }

        private fun onCurrencySelected() {
            openKeyboard()

            layoutPosition.also { currentPosition ->
                selectedCurrency = adapterList?.get(currentPosition)?.currency
                inputtedAmount = amountInput.text.toString()
                adapterList?.removeAt(currentPosition).also {
                    adapterList?.add(0, it!!)
                    changeSavedOrder.invoke(adapterList?.map { it.currency }!!)
                    notifyItemMoved(currentPosition, 0)
                }

                android.os.Handler().postDelayed({ afterMoveAnimation.invoke() }, 300)
            }
        }

        private fun openKeyboard() {
            amountInput.requestFocus()
            amountInput.setSelection(amountInput.text.length)
            KeyboardUtils.showKeyboard(amountInput)
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
    }
}