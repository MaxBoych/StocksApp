package bmstu.max.stocks.entities

import com.google.gson.annotations.SerializedName

data class StockInfo(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("currency") val currency: String,
    var data: StockChangeData
)/* {
    inner class Data(data: StockChangeData) {
        val latestPrice = data.latestPrice
        val change = data.change
        val changePercent = data.changePercent
    }
}*/

data class StockChangeData(
    @SerializedName("symbol") val symbol: String,
    @SerializedName("companyName") val companyName: String,
    @SerializedName("latestPrice") var latestPrice: String,
    @SerializedName("change") var change: String,
    @SerializedName("changePercent") val changePercent: String,
)