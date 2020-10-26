package bmstu.max.stocks.view.stocks

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import bmstu.max.stocks.R
import bmstu.max.stocks.databinding.FragmentStockInfoBinding
import bmstu.max.stocks.entities.StockChangeData
import bmstu.max.stocks.helpers.GlideApp
import bmstu.max.stocks.viewmodel.StocksViewModel
import kotlin.math.round

class StockInfoFragment : Fragment() {

    private var stock: StockChangeData? = null
    private lateinit var stockCurrency: String
    private lateinit var stockRegion: String
    //private lateinit var stockTagString: String

    private lateinit var viewModel: StocksViewModel
    private lateinit var binding: FragmentStockInfoBinding

    private lateinit var stockLogo: ImageView
    private lateinit var stockTag: TextView
    private lateinit var stockName: TextView
    private lateinit var stockPrice: TextView
    private lateinit var stockPriceChange: TextView
    private lateinit var stockPriceArrow: ImageView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            binding = FragmentStockInfoBinding.inflate(inflater, container, false)
        } catch (e: Exception) {
            Log.e("StocksApp", "onCreateView:", e)
            throw e
        }

        setupViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupFragmentViews(view)

        val tag = this.arguments?.get("tag").toString()
        stockCurrency = this.arguments?.get("currency").toString()
        stockRegion = this.arguments?.get("region").toString()

        viewModel.getStockByTag(tag, stockCurrency)
        viewModel.getStockLogo(tag)
    }

    private fun setupFragmentViews(root: View) {
        stockLogo = root.findViewById(R.id.stock_logo)
        stockTag = root.findViewById(R.id.stock_tag)
        stockName = root.findViewById(R.id.stock_name)
        stockPrice = root.findViewById(R.id.stock_price)
        stockPriceChange = root.findViewById(R.id.stock_price_change)
        stockPriceArrow = root.findViewById(R.id.stock_arrow)

        root.findViewById<ImageButton>(R.id.arrow_back).setOnClickListener {
            findNavController().navigate(R.id.action_StockInfoFragment_to_StocksFragment)
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(StocksViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        viewModel.stock.observe(viewLifecycleOwner, Observer {
            stock = it
            stockTag.text = it.symbol
            stockName.text = "${it.companyName} ($stockRegion)"

            val price =  it.latestPrice.toDouble().round(3)
            stockPrice.text = "$price $stockCurrency"

            val priceChange = it.change.toDouble().round(3)
            val changePercent = (it.changePercent.toDouble() * 100).round(3)
            stockPriceChange.text = "$priceChange  (${changePercent} %)"
            when {
                priceChange > 0 -> {
                    stockPriceChange.setTextColor(Color.parseColor("#32CD32"))
                    stockPriceArrow.setImageDrawable(
                        context?.resources?.getDrawable(
                            R.drawable.ic_arrow_price_up, context?.theme
                        )
                    )
                    stockPriceArrow.visibility = View.VISIBLE
                }
                priceChange < 0 -> {
                    stockPriceChange.setTextColor(Color.parseColor("#FF0000"))
                    stockPriceArrow.setImageDrawable(
                        context?.resources?.getDrawable(
                            R.drawable.ic_arrow_price_down, context?.theme
                        )
                    )
                    stockPriceArrow.visibility = View.VISIBLE
                }
                else -> {
                    stockPriceChange.setTextColor(Color.BLACK)
                    stockPriceArrow.visibility = View.INVISIBLE
                }
            }
        })

        viewModel.stockLogoUrl.observe(viewLifecycleOwner, Observer {
            if (context != null) {
                GlideApp.with(requireContext())
                    .load(it)
                    .placeholder(R.drawable.ic_ellipsis)
                    .circleCrop()
                    .into(stockLogo)
            }
        })
    }

    fun Double.round(decimals: Int): Double {
        var multiplier = 1.0
        repeat(decimals) { multiplier *= 10 }
        return round(this * multiplier) / multiplier
    }
}