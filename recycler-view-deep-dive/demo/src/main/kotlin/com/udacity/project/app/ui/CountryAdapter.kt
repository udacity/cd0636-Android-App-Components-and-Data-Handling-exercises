package com.udacity.project.app.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.udacity.project.app.R
import com.udacity.project.app.data.repo.Country


class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
    override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
        return oldItem.countryName == newItem.countryName
    }

    override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
        return oldItem == newItem
    }
}

class CountryAdapter(
    private val onFavoriteToggled: (String) -> Unit
) : ListAdapter<Country, CountryAdapter.CountryViewHolder>(CountryDiffCallback()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CountryViewHolder,
        position: Int
    ) {
        holder.bind(getItem(position))
    }

    inner class CountryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val countryName: TextView = itemView.findViewById(R.id.countryName)
        private val countryOfficialName: TextView = itemView.findViewById(R.id.countryOfficialName)
        private val countryCapital: TextView = itemView.findViewById(R.id.countryCapital)
        private val countryCurrency: TextView = itemView.findViewById(R.id.countryCurrency)
        private val favoriteButton: ImageButton = itemView.findViewById(R.id.favoriteButton)

        fun bind(country: Country) {
            countryName.text = country.countryName
            countryOfficialName.text = country.officialName
            countryCapital.text = "Capital: ${country.capital}"
            countryCurrency.text = "Currency: ${country.currencyName} (${country.currencyCode})"

            favoriteButton.setImageResource(
                if (country.isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )

            favoriteButton.setOnClickListener {
                onFavoriteToggled(country.countryName)
            }
        }

    }
}