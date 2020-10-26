package bmstu.max.stocks.api

import okhttp3.Callback
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request

class CurrencyAPI {

    companion object {
        @JvmStatic
        val shared = CurrencyAPI()
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

    private val token = "20bf198d51cd4d9b8fc160942392ebcd"

    private val baseUrl = "https://api.currencyfreaks.com"

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

    fun requestCurrency(callback: Callback) {
        getRequest("/latest?apikey=$token", callback)
    }
}