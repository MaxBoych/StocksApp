package bmstu.max.stocks.view.stocks

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import bmstu.max.stocks.R
import bmstu.max.stocks.entities.StockInfo

class StocksListAdapter(
    private var items: MutableList<StockInfo>,
    private var context: Context
) :
    RecyclerView.Adapter<StocksListAdapter.BaseViewHolder<*>>() {

    var onItemClick: ((StockInfo) -> Unit)? = null

    fun updateData(newItems: List<StockInfo>) {
        items = newItems.toMutableList()
    }

    abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
        abstract fun bind(item: T)
    }

    inner class SearchStockViewHolder(itemView: View) : BaseViewHolder<StockInfo>(itemView) {
        private val companyName: TextView = itemView.findViewById(R.id.company_name)

        init {
            itemView.setOnClickListener {
                val position: Int = adapterPosition
                if (position != -1) {
                    onItemClick?.invoke(items[position])
                } else {
                    Log.d("StocksApp", "search adapter position is -1")
                }
            }
        }

        override fun bind(item: StockInfo) {
            companyName.text = "(${item.symbol}) ${item.name}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder<*> {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.search_stock_layout, parent, false)

       return SearchStockViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder<*>, position: Int) {
        val element = items[position]
        if (holder is SearchStockViewHolder) {
            holder.bind(element)
        }
    }
}