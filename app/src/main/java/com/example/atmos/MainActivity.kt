package com.example.atmos

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.icu.text.SimpleDateFormat
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.widget.SearchView;
import android.widget.Toast
import com.example.atmos.databinding.ActivityMainBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.sql.Timestamp
import java.util.Date
import java.util.Locale

// e25f3bf2e857a4c1069f2cfb4e6e84cb - api key
public class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fetchWeatherapp("Jaipur")
        searchCity()

    }

    private fun searchCity() {
       val searchView = binding.searchview
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    fetchWeatherapp(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
               return true
            }


        })
    }

    private fun fetchWeatherapp(cityName : String) {

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl("https://api.openweathermap.org/data/2.5/")
            .build().create(ApiInterface::class.java)


        val response = retrofit.getWeatherData(cityName , "e25f3bf2e857a4c1069f2cfb4e6e84cb", "metric")
        response.enqueue(object : Callback<WeatherApp> {
            @SuppressLint("SuspiciousIndentation")


            override fun onResponse(call: Call<WeatherApp>, response: Response<WeatherApp>) {

                val responseBody = response.body()

                if (response.isSuccessful && responseBody != null) {

                    val Temperature = responseBody.main.temp.toString()
                    val Humidity = responseBody.main.humidity
                    val windSpeed = responseBody.wind.speed
                    val sunRise = responseBody.sys.sunrise.toLong()
                    val sunSet = responseBody.sys.sunset.toLong()
                    val seaLevel = responseBody.main.sea_level
                    val Condition = responseBody.weather.firstOrNull()?.main ?: "unknown"
                    val maxTemp = responseBody.main.temp_max
                    val minTemp = responseBody.main.temp_min


                    binding.temperature.text = "$Temperature ℃  "
                    binding.humidity.text = "$Humidity %"
                    binding.weather.text = Condition
                    binding.maxTemp.text = "Max Temp: $maxTemp ℃ "
                    binding.minTemp.text= "Min Temp: $minTemp ℃ "
                    binding.windspeed.text = "$windSpeed m/s"
                    binding.sunrise.text = "${time(sunRise)}"
                    binding.sunset.text = "${time(sunSet)}"
                    binding.seaflow.text = "$seaLevel hpa"
                    binding.condition.text = Condition
                    binding.cityName.text ="$cityName"
                    binding.day.text = daytime(System.currentTimeMillis())
                    binding.date.text = date()


                    changesimageaccrdingtoweathercondition(Condition)

                }
            }
            override fun onFailure(call: Call<WeatherApp>, t: Throwable) {

            }
        })
    }

    private fun changesimageaccrdingtoweathercondition(condtions : String) {
       when(condtions){
           "Clear Sky" , "Sunny" , "Clear" -> {
               binding.root.setBackgroundResource(R.drawable.sunnyscreen)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
           "Partly Clouds", "Clouds" , "Overcast" , "Mist" , "Foggy" ->{
                binding.root.setBackgroundResource(R.drawable.colin_lloyd_gjnlws2wy3w_unsplash_1)
                binding.lottieAnimationView.setAnimation(R.raw.cloud)
           }
           "Light Rain", "Drizzle" , "Moderate Rain" , "Showers" ,"Heavy Rain" -> {
               binding.root.setBackgroundResource(R.drawable.rain_screen)
               binding.lottieAnimationView.setAnimation(R.raw.rain)
           }
           "Light Snow" , "Moderate Snow" , "Heavy Snow" , "Blizzard" ,"Smoke" ->{
               binding.root.setBackgroundResource(R.drawable.snow_screen)
               binding.lottieAnimationView.setAnimation(R.raw.snow)
           }
           else -> {
               binding.root.setBackgroundResource(R.drawable.sunnyscreen)
               binding.lottieAnimationView.setAnimation(R.raw.sun)
           }
       }
        binding.lottieAnimationView.playAnimation()
    }

    private fun date(): String {
        val sdf = SimpleDateFormat("dd MMMM  yyyy" , Locale.getDefault())
        return sdf.format((Date()))
    }
    private fun time(timestamp: Long): String {
        val sdf = SimpleDateFormat("HH:mm" , Locale.getDefault())
        return sdf.format((Date(timestamp*1000)))
    }

    fun daytime(timestamp: Long):String{
        val sdf = SimpleDateFormat("EEEE" , Locale.getDefault())
        return sdf.format((Date()))
    }
}




