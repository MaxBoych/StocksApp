package bmstu.max.stocks.helpers

interface OnResponseListener<T> {

    fun failure(error: String)

    fun success(data: T)
}