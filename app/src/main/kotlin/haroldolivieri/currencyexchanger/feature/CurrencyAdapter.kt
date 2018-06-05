package haroldolivieri.currencyexchanger.feature

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.View
import java.util.*

class CurrencyAdapter(private var rates: List<Pair<Currency, String>>? = null,
                      private val context: Context) : RecyclerView.Adapter<CurrencyHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): CurrencyHolder {
        val view = LayoutInflater.from(parent?.context)
                .inflate(android.R.layout.simple_list_item_1, parent, false)
        return CurrencyHolder(view, context)
    }


    override fun onBindViewHolder(holder: CurrencyHolder, position: Int) {
        rates?.get(0)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = rates?.size ?: 0

}

class CurrencyHolder(view: View, val context: Context) : RecyclerView.ViewHolder(view) {
    fun bind (pair: Pair<Currency, String>) {

    }
}
