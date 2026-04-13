package com.udacity.project.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project.app.R
import com.udacity.project.app.data.repo.Country

class CountryAdapter : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var countries: List<Country> = emptyList()

    fun updateCountries(newCountries: List<Country>) {
        countries = newCountries
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(countries[position])
    }

    override fun getItemCount(): Int = countries.size

    inner class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val countryName: TextView = itemView.findViewById(R.id.countryName)
        private val countryOfficialName: TextView = itemView.findViewById(R.id.countryOfficialName)
        private val countryCapital: TextView = itemView.findViewById(R.id.countryCapital)
        private val countryCurrency: TextView = itemView.findViewById(R.id.countryCurrency)

        fun bind(country: Country) {
            countryName.text = country.countryName
            countryOfficialName.text = country.officialName
            countryCapital.text = "Capital: ${country.capital}"
            countryCurrency.text = "Currency: ${country.currencyName} (${country.currencyCode})"
        }
    }
}
