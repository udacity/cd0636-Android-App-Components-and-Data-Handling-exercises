package com.udacity.project.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * Basic RecyclerView.Adapter implementation.
 *
 * Complete Part 1 TODOs to convert to ListAdapter with DiffUtil.
 */
class CountryAdapter(
    private val onFavoriteToggled: (Int) -> Unit,
    private val onCountryDeleted: (Int) -> Unit
) : RecyclerView.Adapter<CountryAdapter.CountryViewHolder>() {

    private var countries: List<Country> = emptyList()

    fun updateCountries(newCountries: List<Country>) {
        countries = newCountries
        notifyDataSetChanged()
    }

    /**
     * TODO 1.1: Create CountryDiffCallback
     *
     * Create a nested class that extends DiffUtil.ItemCallback<Country>
     * Implement:
     * - areItemsTheSame(): Compare countries by id
     * - areContentsTheSame(): Compare entire Country objects
     */

    /**
     * TODO 1.2: Convert to ListAdapter
     *
     * Change class signature to extend ListAdapter instead of RecyclerView.Adapter:
     * - Extend: ListAdapter<Country, CountryAdapter.CountryViewHolder>(CountryDiffCallback())
     * - Remove the countries property and updateCountries() method
     * - In onBindViewHolder, use getItem(position) instead of countries[position]
     * - Remove getItemCount() override (ListAdapter handles this)
     */

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
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(country: Country) {
            countryName.text = country.name
            countryOfficialName.text = country.officialName
            countryCapital.text = "Capital: ${country.capital}"
            countryCurrency.text = "Currency: ${country.currencyName}"
            updateFavoriteIcon(country.isFavorite)

            favoriteButton.setOnClickListener {
                onFavoriteToggled(country.id)
            }

            deleteButton.setOnClickListener {
                onCountryDeleted(country.id)
            }
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            favoriteButton.setImageResource(
                if (isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }
    }
}