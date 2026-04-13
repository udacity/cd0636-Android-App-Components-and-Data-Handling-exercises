package com.udacity.project.app

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

/**
 * ListAdapter with DiffUtil for efficient list updates and animations.
 */
class CountryAdapter(
    private val onCountryClick: (Country) -> Unit,
    private val onToggleFavorite: (Country) -> Unit
) : ListAdapter<Country, CountryAdapter.CountryViewHolder>(CountryDiffCallback()) {

    /**
     * DiffUtil.ItemCallback to calculate the difference between two lists.
     */
    class CountryDiffCallback : DiffUtil.ItemCallback<Country>() {
        override fun areItemsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Country, newItem: Country): Boolean {
            return oldItem == newItem
        }

        override fun getChangePayload(oldItem: Country, newItem: Country): Any? {
            return when {
                oldItem.isFavorite != newItem.isFavorite -> PAYLOAD_FAVORITE
                else -> null
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_country, parent, false)
        return CountryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int, payloads: List<Any>) {
        if (payloads.isEmpty()) {
            super.onBindViewHolder(holder, position, payloads)
        } else {
            val country = getItem(position)
            payloads.forEach { payload ->
                when (payload) {
                    PAYLOAD_FAVORITE -> holder.updateFavorite(country.isFavorite)
                }
            }
        }
    }

    override fun onBindViewHolder(holder: CountryViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

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

            itemView.setOnClickListener { onCountryClick(country) }
            favoriteButton.setOnClickListener { onToggleFavorite(country) }
            deleteButton.setOnClickListener { /* Handled by swipe gesture */ }
        }

        fun updateFavorite(isFavorite: Boolean) {
            updateFavoriteIcon(isFavorite)
        }

        private fun updateFavoriteIcon(isFavorite: Boolean) {
            favoriteButton.setImageResource(
                if (isFavorite) android.R.drawable.btn_star_big_on
                else android.R.drawable.btn_star_big_off
            )
        }
    }

    companion object {
        private const val PAYLOAD_FAVORITE = "payload_favorite"
    }
}
