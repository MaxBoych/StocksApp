package bmstu.max.stocks.model

import android.util.Log
import bmstu.max.stocks.api.CurrencyAPI
import bmstu.max.stocks.api.StocksAPI
import bmstu.max.stocks.entities.StockChangeData
import bmstu.max.stocks.entities.StockInfo
import bmstu.max.stocks.helpers.OnResponseListener
import com.google.gson.Gson
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class StocksModel {

    private val stocksAPI = StocksAPI.shared
    private val currencyAPI = CurrencyAPI.shared

    /*private var stocksAmount = 0
    private var currentStocksAmount = 0*/

    fun requestStocks(listener: OnResponseListener<List<StockInfo>>) {
        stocksAPI.requestStocks(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                listener.failure(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData: String = response.body?.string() ?: return
                val jsonArray = JSONArray(jsonData)
                //stocksAmount = jsonArray.length()

                Log.d("StocksApp", jsonArray.toString())

                val stocks = mutableListOf<StockInfo>()
                //val hashSetCurrency = hashSetOf<String>()
                //val hashSetRegion = hashSetOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = jsonArray.getJSONObject(i)
                    val stock = Gson().fromJson(jsonObject.toString(), StockInfo::class.java)
                    stocks.add(stock)
                    /*requestStock(stock.symbol, object : OnResponseListener<StockChangeData> {

                        override fun failure(error: String) {
                            Log.e("StocksApp", error)
                            readinessCheck(listener)
                        }

                        override fun success(data: StockChangeData) {
                            stock.data = data
                            stocks.add(stock)
                            readinessCheck(listener)
                        }
                    })*/


                    //hashSetCurrency.add(stock.currency)
                    //hashSetRegion.add(stock.region)
                }

                //Log.d("SSS currency", hashSetCurrency.toString())
                //Log.d("SSS region", hashSetRegion.toString())

                listener.success(stocks)
            }
        })
    }

    /*private fun readinessCheck(listener: OnResponseListener<List<StockInfo>>) {
        currentStocksAmount++
        Log.d("SSS", "$currentStocksAmount   $stocksAmount")
        if (currentStocksAmount == stocksAmount) {
            listener.success(stocks)
        }
    }*/

    fun requestStock(tag: String, listener: OnResponseListener<StockChangeData>) {
        stocksAPI.requestStock(tag, object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                listener.failure(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData: String = response.body?.string() ?: return
                //Log.d("SSS", jsonData)
                val jsonObject = JSONObject(jsonData)
                val stock = Gson().fromJson(jsonObject.toString(), StockChangeData::class.java)
                listener.success(stock)
            }
        })
    }

    fun requestCurrency(tag: String, listener: OnResponseListener<Double>) {
        currencyAPI.requestCurrency(object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                listener.failure(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData: String = response.body?.string() ?: return
                val jsonObject = JSONObject(jsonData)
                //Log.d("SSS", jsonObject.toString(4))
                listener.success(jsonObject.getJSONObject("rates").getString(tag).toDouble())
            }
        })
    }

    fun requestStockLogo(tag: String, listener: OnResponseListener<String>) {
        stocksAPI.requestStockLogo(tag, object : Callback {

            override fun onFailure(call: Call, e: IOException) {
                listener.failure(e.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val jsonData: String = response.body?.string() ?: return
                val jsonObject = JSONObject(jsonData)
                listener.success(jsonObject.getString("url"))
            }
        })
    }
}