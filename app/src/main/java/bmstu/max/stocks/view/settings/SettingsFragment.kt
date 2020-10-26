package bmstu.max.stocks.view.settings

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import bmstu.max.stocks.R
import kotlinx.android.synthetic.main.fragment_settings.*


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val entries = listOf(
            "USD",
            "EUR",
            "RUB",
            "GBP",
            "CHF",
            "JPY",
            "CAD",
            "AUD"
        )

        val appPreferencesFileName = "StocksAppSharedPreferences"
        val appPreferences = context?.getSharedPreferences(
            appPreferencesFileName,
            Context.MODE_PRIVATE
        )

        currency_spr.setSelection(
            entries.indexOf(
                appPreferences?.getString(
                    "currency", "USD"
                )
            )
        )

        only_rising.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                only_fallen.isChecked = false
            }
        }

        only_fallen.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                only_rising.isChecked = false
            }
        }

        arrow_back.setOnClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_StocksFragment)
        }

        cancel_btn.setOnClickListener {
            findNavController().navigate(R.id.action_SettingsFragment_to_StocksFragment)
        }

        apply_btn.setOnClickListener {

            appPreferences?.edit()?.putBoolean("onlyRising", only_rising.isChecked)?.apply()

            appPreferences?.edit()?.putBoolean("onlyFallen", only_fallen.isChecked)?.apply()

            val percentage = percentage.text.toString()
            if (percentage == "" || percentage.toDouble() == 0.0) {
                appPreferences?.edit()?.putString("percentage", "0")?.apply()
            } else {
                appPreferences?.edit()?.putString("percentage", percentage)?.apply()
            }

            appPreferences?.edit()?.putString("currency", currency_spr.selectedItem.toString())
                ?.apply()

            findNavController().navigate(R.id.action_SettingsFragment_to_StocksFragment)
        }
    }
}