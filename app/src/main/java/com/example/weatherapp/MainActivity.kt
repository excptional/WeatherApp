package com.example.weatherapp

import android.annotation.SuppressLint
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.bumptech.glide.Glide
import org.json.JSONObject

class MainActivity : AppCompatActivity(), NewsItemClicked {

    private lateinit var mAdapter: WeatherAdapter
    private var city: CharSequence = "Kolkata"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this, RecyclerView.HORIZONTAL, false)

        mAdapter = WeatherAdapter(this)
        recyclerView.adapter = mAdapter

        fetchData(city)

        val searchView: SearchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(p0: String?): Boolean {
                city = searchView.query
                fetchData(city)
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                return false
            }

        })

    }

    @SuppressLint("SetTextI18n")
    private fun fetchData(city: CharSequence) {

        val progressbar: ProgressBar = findViewById(R.id.progressBar)
        progressbar.visibility = View.VISIBLE

        val temperature: TextView = findViewById(R.id.temperature)
        val weatherText: TextView = findViewById(R.id.weatherText)
        val weatherIcon: ImageView = findViewById(R.id.weatherIcon)
        val cityName: TextView = findViewById(R.id.cityName)
        val countryName: TextView = findViewById(R.id.countryName)
        val date: TextView = findViewById(R.id.date)
        val humidityWind: TextView = findViewById(R.id.humidityWind)
        val rainChances: TextView = findViewById(R.id.rainChances)
        val maxMinTemp: TextView = findViewById(R.id.maxMinTemp)

        val url =
            "https://api.weatherapi.com/v1/forecast.json?key=bd2fd5462f7443e49ae51928211709&q=$city&aqi=no"

        val jsonObjectRequest = JsonObjectRequest(
            Request.Method.GET, url, null,
            {
                progressbar.visibility = View.GONE
                val locationJsonObject = it.getJSONObject("location")
                cityName.text = locationJsonObject.getString("name")

                val region = locationJsonObject.getString("region")
                val country = locationJsonObject.getString("country")
                countryName.text = "$region, $country"

                val localtime = locationJsonObject.getString("localtime")
                date.text = dateMaker(localtime)

                val currentJsonObject = it.getJSONObject("current")
                temperature.text = currentJsonObject.getString("temp_c") + "°c"


                val humidity = currentJsonObject.getString("humidity")
                val wind = currentJsonObject.getString("wind_kph")
                humidityWind.text = "Humidity: $humidity%    |    Wind: $wind km/h"

                val conditionJsonObject = currentJsonObject.getJSONObject("condition")
                weatherText.text = conditionJsonObject.getString("text")
                val imageUrl = conditionJsonObject.getString("icon")
                Glide.with(this).load("https:$imageUrl").into(weatherIcon)

                val forecastObject = it.getJSONObject("forecast")
                val forecastDayObject = forecastObject.getJSONArray("forecastday").getJSONObject(0)
                val dayObject = forecastDayObject.getJSONObject("day")

                val maxTemp = dayObject.getString("maxtemp_c")
                val minTemp = dayObject.getString("mintemp_c")
                maxMinTemp.text = "Min: $minTemp°c  |  Max: $maxTemp°c"

                val rainChancesObject = dayObject.getString("daily_chance_of_rain")
                rainChances.text = "Chances of rain: $rainChancesObject%"

                val hourArray = forecastDayObject.getJSONArray("hour")

                val itemArray = ArrayList<Item>()
                for (i in 0 until hourArray.length()) {
                    val itemJsonObject = hourArray.getJSONObject(i)
                    val item = Item(
                        itemJsonObject.getString("time"),
                        itemJsonObject.getString("temp_c"),
                        itemJsonObject.getJSONObject("condition").getString("icon"),
                        itemJsonObject.getJSONObject("condition").getString("text")
                    )
                    itemArray.add(item)
                }
                mAdapter.updateWeather(itemArray)

            },
            {
                progressbar.visibility = View.GONE
                Toast.makeText(
                    this,
                    "Something wrong, please check your internet connection",
                    Toast.LENGTH_LONG
                ).show()
            })

        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
    }

    override fun onItemClicked(item: Item) {

    }

    private fun dateMaker(date: String): String {
        var month = date.substring(5, 7)

        when (month) {
            "01" -> month = "January"
            "02" -> month = "February"
            "05" -> month = "March"
            "04" -> month = "April"
            "05" -> month = "May"
            "06" -> month = "June"
            "07" -> month = "July"
            "08" -> month = "August"
            "09" -> month = "September"
            "10" -> month = "October"
            "11" -> month = "November"
            "12" -> month = "December"
        }
        val day = date.substring(8, 10)
        val year = date.substring(0, 4)
        return "$day $month, $year"
    }

}
