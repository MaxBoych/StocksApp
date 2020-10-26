package bmstu.max.stocks.api

import android.util.Log
import okhttp3.Callback
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request

class StocksAPI {

    companion object {
        @JvmStatic
        val shared = StocksAPI()
    }

    enum class Status {
        OK,
        BadRequest,
        Unauthorized,
        NotFound,
        Conflict,
        InternalError,
        UnknownError,
        Unreachable
    }

    private val httpClient = OkHttpClient()

    private val token = "pk_4215033813b74f77b93ee7f97acc2c30"

    private val baseUrl = "https://cloud.iexapis.com"

    private fun getRequest(
        endpoint: String,
        callback: Callback
    ) {
        val headers = hashMapOf(
            "Content-Type" to "application/json"
        )
        val headerBuilder: Headers.Builder = Headers.Builder()
        for (key in headers.keys) {
            headerBuilder.add(key, headers[key]!!)
        }

        val request = Request.Builder()
            .url(baseUrl + endpoint)
            .headers(headerBuilder.build())
            .get()
            .build()

        val call = httpClient.newCall(request)
        call.enqueue(callback)
    }

    fun requestStocks(callback: Callback) {
        getRequest("/beta/ref-data/symbols?token=$token", callback)
    }

    fun requestStock(tag: String, callback: Callback) {
        getRequest("/stable/stock/$tag/quote?&token=$token", callback)
    }

    fun requestStockLogo(tag: String, callback: Callback) {
        Log.d("StocksApp", "HERE")
        getRequest("/stable/stock/$tag/logo?&token=$token", callback)
    }
}