package bmstu.max.stocks.viewmodel

import android.util.Log
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import bmstu.max.stocks.entities.StockChangeData
import bmstu.max.stocks.entities.StockInfo
import bmstu.max.stocks.helpers.OnResponseListener
import bmstu.max.stocks.model.StocksModel

class StocksViewModel : ViewModel() {

    private val model = StocksModel()

    val isSearching = ObservableField(false)

    var stockItems = MutableLiveData<MutableList<StockInfo>>()
    var allStockItems = mutableListOf<StockInfo>()

    var stock = MutableLiveData<StockChangeData>()
    var stockLogoUrl = MutableLiveData<String>()

    fun getStocks(listener: OnResponseListener<List<StockInfo>>) {
        isSearching.set(true)
        model.requestStocks(object : OnResponseListener<List<StockInfo>> {
            override fun failure(error: String) {
                listener.failure("search error: $error")
                isSearching.set(false)
            }

            override fun success(data: List<StockInfo>) {
                Log.d("StocksApp","search success: ${data.size}")
                stockItems.postValue(data.toMutableList())
                allStockItems = data.toMutableList()
                listener.success(data.toMutableList())
                isSearching.set(false)
            }
        })
    }

    fun getStocksBySearch(text: String) {
        isSearching.set(true)
        val filtered = mutableListOf<StockInfo>()
        for (stock in allStockItems) {
            if (stock.name.contains(text, ignoreCase = true) ||
                stock.symbol.contains(text, ignoreCase = true)) {
                filtered.add(stock)
            }
        }

        stockItems.postValue(filtered)
        isSearching.set(false)
    }

    fun getStockByTag(tag: String, currency: String) {
        isSearching.set(true)
        model.requestStock(tag, object : OnResponseListener<StockChangeData> {

            override fun failure(error: String) {
                Log.d("StocksApp", "search stock change data error: $error")
                isSearching.set(false)
            }

            override fun success(data: StockChangeData) {
                if (currency != "USD") {
                    model.requestCurrency(currency, object : OnResponseListener<Double> {

                        override fun failure(error: String) {
                            Log.d("StocksApp", "currency error: $error")
                        }

                        override fun success(factor: Double) {
                            val price = data.latestPrice.toDouble() * factor
                            data.latestPrice = price.toString()

                            val change = data.change.toDouble() * factor
                            data.change = change.toString()

                            stock.postValue(data)
                            isSearching.set(false)
                        }
                    })
                } else {
                    stock.postValue(data)
                    isSearching.set(false)
                }
            }
        })
    }

    fun getStockLogo(tag: String) {
        model.requestStockLogo(tag, object : OnResponseListener<String> {

            override fun failure(error: String) {
                Log.d("StocksApp", "stock logo error: $error")
            }

            override fun success(data: String) {
                stockLogoUrl.postValue(data)
            }
        })
    }
}