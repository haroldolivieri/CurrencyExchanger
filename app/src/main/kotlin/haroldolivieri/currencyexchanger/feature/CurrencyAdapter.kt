package haroldolivieri.currencyexchanger.feature

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import com.jakewharton.rxbinding2.widget.RxTextView
import haroldolivieri.currencyexchanger.R
import haroldolivieri.currencyexchanger.domain.Currency
import haroldolivieri.currencyexchanger.view.currencyImage
import haroldolivieri.currencyexchanger.view.currencyName
import io.reactivex.subjects.PublishSubject

class CurrencyAdapter(private var rates: List<Pair<Currency, Float>>? = null,
                      private val itemClick: (position: Int) -> Unit) :
        RecyclerView.Adapter<CurrencyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CurrencyHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency, parent, false)
        return CurrencyHolder(view)
    }

    private val subject : PublishSubject<Float> = PublishSubject.create()

    fun setRates(rates: List<Pair<Currency, Float>>) {
        this.rates = rates
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        rates?.get(position)?.let { holder.bind(it, itemClick, subject)}
    }

    override fun getItemCount(): Int = rates?.size ?: 0
}

class CurrencyHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    private val currencyImage = itemView.findViewById<ImageView>(R.id.currencyImage)
    private val currencyName = itemView.findViewById<TextView>(R.id.currencyName)
    private val currencyDescription = itemView.findViewById<TextView>(R.id.currencyDescription)
    private val amountInput = itemView.findViewById<EditText>(R.id.amountInput)

    fun bind(pair: Pair<Currency, Float>,
             itemClick: (position: Int) -> Unit,
             subject: PublishSubject<Float>) {

        val currency = pair.first
        val amountInBaseCurrency = pair.second

        currencyImage.setImageResource(currency.currencyImage())
        currencyName.text = currency.name
        currencyDescription.setText(currency.currencyName())

        RxTextView.textChanges(amountInput)
                .filter{ it.toString().isNotEmpty() }
                .subscribe{

                    animateToFirstResponder()

                    val currentAmount = it.toString().toFloat()
                    val convertedToBaseCurrency = currentAmount/amountInBaseCurrency
                    subject.onNext(convertedToBaseCurrency)

                }

        subject.subscribe { typedAmountInBaseCurrency ->

            amountInput.setText("${typedAmountInBaseCurrency * amountInBaseCurrency}")
        }

        itemView.setOnClickListener {
            animateToFirstResponder()
            itemClick.invoke(position)
        }
    }

    private fun animateToFirstResponder() {}
}
