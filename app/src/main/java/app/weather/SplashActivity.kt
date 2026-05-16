package app.weather

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import app.weather.api.apiService
import app.weather.api.cords
import app.weather.WeatherUtils
import com.google.android.gms.location.LocationServices
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime

class SplashActivity : AppCompatActivity() {

    private val LOCATION_PERMISSION_REQUEST = 100
    private lateinit var back: ImageView
    private lateinit var icon: ImageView
    private lateinit var prefsEditor: android.content.SharedPreferences.Editor
    private var started = false

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val prefs = getSharedPreferences("app_cache", MODE_PRIVATE)
        prefsEditor = prefs.edit()

        back = findViewById(R.id.backgroundView)
        back.alpha = 0f
        icon = findViewById(R.id.icon)
    }

    override fun onResume() {
        super.onResume()

        if (!started) {
            started = true
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST
            )

        } else {
            getLocationAndLoad()
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == LOCATION_PERMISSION_REQUEST &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            getLocationAndLoad()
        } else {
            getWeatherAndStart(45.44, 10.82)
        }
    }
    private fun getLocationAndLoad() {

        val fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->

                if (location != null) {
                    getWeatherAndStart(location.latitude, location.longitude)
                } else {
                    getWeatherAndStart(45.44, 10.82)
                }
            }
            .addOnFailureListener {
                getWeatherAndStart(45.44, 10.82)
            }
    }

    private fun getWeatherAndStart(lat: Double, lon: Double) {

        val retrofit = Retrofit.Builder()
            .baseUrl("https://weather.3mrys00.workers.dev")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(apiService::class.java)
        val gson = Gson()

        lifecycleScope.launch {

            try {

                val response = apiService.getTemp(cords(lat, lon))

                prefsEditor.putFloat("currentTemp", response.current.temp.toFloat())
                prefsEditor.putFloat("wind", response.current.wind_speed.toFloat())
                prefsEditor.putString("overview", response.daily[0].overview)
                prefsEditor.putString("sunrise", response.current.sunrise)
                prefsEditor.putString("sunset", response.current.sunset)
                prefsEditor.putFloat("lat", lat.toFloat())
                prefsEditor.putFloat("lon", lon.toFloat())
                prefsEditor.putString("hourly_list", gson.toJson(response.hourly))
                prefsEditor.putString("daily_list", gson.toJson(response.daily))
                prefsEditor.putString("place", response.current.place)

                var willRain = false

                if (
                    response.hourly[1].icon == "09d" ||
                    response.hourly[1].icon == "10d" ||
                    response.hourly[1].icon == "11d" ||
                    response.hourly[1].icon == "13d"
                ) {
                    willRain = true
                }
                prefsEditor.putBoolean("willRain", willRain)

                val hour = LocalTime.now().hour

                val backgroundName =
                    if (hour < 7 || hour > 19) {
                        if (willRain) "background_notte_pioggia"
                        else "background_notte"
                    } else {
                        if (willRain) "background_giorno_pioggia"
                        else "background_giorno"
                    }
                prefsEditor.putString("background", backgroundName)
                prefsEditor.apply()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            val resId = WeatherUtils.getBackgroundResource(this@SplashActivity)
            back.setImageResource(resId)

            startAnimation()
        }
    }

    private fun startAnimation() {

        back.animate()
            .alpha(1f)
            .setDuration(600)
            .start()

        icon.animate()
            .rotation(2160f)
            .setDuration(1500)
            .withEndAction {

                icon.animate()
                    .translationX(-700f)
                    .scaleX(0f)
                    .scaleY(0f)
                    .setDuration(200)
                    .withEndAction {

                        val fadeOptions = ActivityOptions.makeCustomAnimation(
                            this@SplashActivity,
                            R.anim.no_fade,
                            R.anim.no_fade
                        )

                        val nextPage =
                            Intent(this@SplashActivity, MainActivity::class.java)

                        startActivity(nextPage, fadeOptions.toBundle())

                        finish()   // important
                    }
                    .start()
            }
            .start()
    }
}