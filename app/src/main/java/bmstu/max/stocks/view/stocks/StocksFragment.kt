package bmstu.max.stocks.view.stocks

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import bmstu.max.stocks.R
import bmstu.max.stocks.databinding.FragmentStocksBinding
import bmstu.max.stocks.entities.StockInfo
import bmstu.max.stocks.helpers.OnResponseListener
import bmstu.max.stocks.viewmodel.StocksViewModel
import kotlinx.android.synthetic.main.fragment_stocks.*


class StocksFragment : Fragment() {

    private lateinit var viewModel: StocksViewModel
    private lateinit var binding: FragmentStocksBinding

    private lateinit var stocksListAdapter: StocksListAdapter

    private var allStocks = mutableListOf<StockInfo>()
    private var searchedStocks = mutableListOf<StockInfo>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        try {
            binding = FragmentStocksBinding.inflate(inflater, container, false)
        } catch (e: Exception) {
            Log.e("StocksApp", "onCreateView:", e)
            throw e
        }

        setupToolbar(binding.root)

        setupViewModel()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<ImageButton>(R.id.action_settings).setOnClickListener {
            findNavController().navigate(R.id.action_StocksFragment_to_SettingsFragment)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupSearchRecyclerAdapter()

        viewModel.getStocks(object : OnResponseListener<List<StockInfo>> {

            override fun failure(error: String) {
                Log.d("StocksApp", "search error: $error")
            }

            override fun success(data: List<StockInfo>) {
                //val stocks = data.toMutableList()

                /*val appPreferencesFileName = "StocksAppSharedPreferences"
                val appPreferences = context?.getSharedPreferences(
                    appPreferencesFileName,
                    Context.MODE_PRIVATE
                )*/
                /*if (appPreferences?.getBoolean("onlyRising", false)!!) {
                    //
                }
                if (appPreferences.getBoolean("onlyFallen", false)) {
                    //
                }
                if (appPreferences.getString("percentage", "0")?.toDouble() != 0.0) {
                    //
                }*/

                allStocks = data.toMutableList()
                //Log.d("SSS", allStocks.toString())
            }
        })
    }

    private fun setupToolbar(root: View) {
        val toolbar = root.findViewById(R.id.toolbar) as Toolbar
        val appActivity = activity as AppCompatActivity
        appActivity.supportActionBar?.title = null

        toolbar.inflateMenu(R.menu.search_menu)
        val searchItem = toolbar.menu.findItem(R.id.menu_search)
        if (searchItem != null) {

            searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {

                override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                    stocksListAdapter.updateData(mutableListOf())
                    search_recyclerView?.adapter?.notifyDataSetChanged()

                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                    stocksListAdapter.updateData(allStocks)
                    search_recyclerView?.adapter?.notifyDataSetChanged()

                    return true
                }
            })

            val searchView = searchItem.actionView as SearchView

            searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

                override fun onQueryTextSubmit(query: String?): Boolean {
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    val text = newText ?: return false
                    if (text.isEmpty()) {
                        stocksListAdapter.updateData(mutableListOf())
                        val adapter = search_recyclerView?.adapter ?: return false
                        adapter.notifyDataSetChanged()
                    } else {
                        viewModel.getStocksBySearch(text)
                    }

                    return false
                }
            })
        }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProviders.of(this).get(StocksViewModel::class.java)
        binding.viewModel = viewModel
        binding.executePendingBindings()
        viewModel.stockItems.observe(viewLifecycleOwner, Observer {
            searchedStocks = it.toMutableList()
            val adapter = search_recyclerView.adapter ?: return@Observer //true
            stocksListAdapter.updateData(it)
            adapter.notifyDataSetChanged()
        })
    }

    private fun setupSearchRecyclerAdapter() {
        search_recyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            stocksListAdapter = StocksListAdapter(allStocks, context)
            adapter = stocksListAdapter

            val lineDivider = ContextCompat.getDrawable(context, R.drawable.line_divider)
            val itemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
            lineDivider?.let { itemDecoration.setDrawable(it) }
            addItemDecoration(itemDecoration)

            stocksListAdapter.onItemClick = { item ->
                val appPreferencesFileName = "StocksAppSharedPreferences"
                val appPreferences = context?.getSharedPreferences(
                    appPreferencesFileName,
                    Context.MODE_PRIVATE
                )

                val args = Bundle()
                args.putString("tag", item.symbol)
                args.putString("currency", appPreferences?.getString("currency", "USD"))
                args.putString("region", item.region)

                findNavController().navigate(R.id.action_StocksFragment_to_StockInfoFragment, args)

                Log.d("StocksApp", item.name)
            }
        }
    }
}