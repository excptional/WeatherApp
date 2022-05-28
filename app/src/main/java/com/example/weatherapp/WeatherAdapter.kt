package com.example.weatherapp

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import org.w3c.dom.Text


class WeatherAdapter(private val listener: NewsItemClicked) :
    RecyclerView.Adapter<WeatherAdapter.WeatherViewHolder>() {

    private var items: ArrayList<Item> = ArrayList()

    inner class WeatherViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val timeItem: TextView = itemView.findViewById(R.id.timeItem)
        val imageItem: ImageView = itemView.findViewById(R.id.imageItem)
        val tempItem: TextView = itemView.findViewById(R.id.tempItem)
        val textItem: TextView = itemView.findViewById(R.id.textItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item, parent, false)
        val viewHolder = WeatherViewHolder(view)
        view.setOnClickListener {
            listener.onItemClicked(items[viewHolder.adapterPosition])
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        val currentItem = items[position]

        val stringOfTime = currentItem.time
        val subStringOfTime = stringOfTime.substring(10, stringOfTime.length)

        holder.timeItem.text = subStringOfTime
        holder.tempItem.text = currentItem.temp + "°c"
        holder.textItem.text = currentItem.text
        val imageURL = currentItem.iconUrl
        Glide.with(holder.itemView.context).load("https:$imageURL").into(holder.imageItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateWeather(updatedWeather: ArrayList<Item>) {
        items.clear()
        items.addAll(updatedWeather)

        notifyDataSetChanged()
    }
}

interface NewsItemClicked {
    fun onItemClicked(item: Item)
}
