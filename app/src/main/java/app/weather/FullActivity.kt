package app.weather

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.dynamicanimation.animation.DynamicAnimation
import androidx.dynamicanimation.animation.SpringAnimation
import androidx.lifecycle.lifecycleScope
import app.weather.api.Hourly
import app.weather.api.apiService
import app.weather.api.cords
import app.weather.WeatherUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eightbitlab.com.blurview.BlurView
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalTime

class FullActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_full)

        val prefs = getSharedPreferences("app_cache", MODE_PRIVATE)
        val wind = prefs.getFloat("wind", 0f)
        val sunrise = prefs.getString("sunrise", "")
        val sunset = prefs.getString("sunset", "")
        val place = prefs.getString("place", "")
        val overview = prefs.getString("overview", "")
        val lat = prefs.getFloat("lat", 0f).toDouble()
        val lon = prefs.getFloat("lon", 0f).toDouble()
        val json = prefs.getString("hourly_list", null)

        val type = object : TypeToken<List<Hourly>>() {}.type
        val hourlyList: List<Hourly> = Gson().fromJson(json, type) ?: emptyList()

        val mainTxt = findViewById<TextView>(R.id.mainTxt)
        val tempTxt = findViewById<TextView>(R.id.tempTxt)
        val placeTxt = findViewById<TextView>(R.id.placeTxt)
        val overviewTxt = findViewById<TextView>(R.id.overviewText)

        val backView = findViewById<ImageView>(R.id.backView)
        backView.setImageResource(WeatherUtils.getBackgroundResource(this))

        mainTxt.translationY = -500f
        val mainTxtSpring = SpringAnimation(mainTxt, DynamicAnimation.TRANSLATION_Y, 0f).apply {

            // Low stiffness makes the animation slower/weighter.
            // Higher damping ratio prevents it from bouncing too much.
            spring.stiffness = 100f
            spring.dampingRatio = 0.40f
            start()
        }
        tempTxt.scaleX = 0f
        tempTxt.scaleY = 0f
        val tempTxtSpringX = SpringAnimation(tempTxt, DynamicAnimation.SCALE_X, 1f).apply {

            // Low stiffness makes the animation slower/weighter.
            // Higher damping ratio prevents it from bouncing too much.
            spring.stiffness = 150f
            spring.dampingRatio = 0.70f
            start()
        }
        val tempTxtSpringY = SpringAnimation(tempTxt, DynamicAnimation.SCALE_Y, 1f).apply {

            // Low stiffness makes the animation slower/weighter.
            // Higher damping ratio prevents it from bouncing too much.
            spring.stiffness = 100f
            spring.dampingRatio = 0.40f
            start()
        }
        val mainV = findViewById<ConstraintLayout>(R.id.main)
        mainV.background = getDrawable(R.drawable.backpalmtree)

        val mainContent = findViewById<ScrollView>(R.id.content)
        mainContent.alpha = 0f
        mainContent.animate().alpha(1f).setDuration(900).start()

        val temp = findViewById<CardView>(R.id.temp)
        val celo = findViewById<LinearLayout>(R.id.wind)
        val qual = findViewById<CardView>(R.id.vento)
        val extra = findViewById<CardView>(R.id.extra)
        val sunriseTxt = findViewById<TextView>(R.id.sunriseTxt)
        val sunsetTxt = findViewById<TextView>(R.id.sunsetTxt)
        val windTxt = findViewById<TextView>(R.id.windTxt)
        val hourlyWeather = findViewById<LinearLayout>(R.id.houlryWeather)

        val buttonCard1 = findViewById<CardView>(R.id.buttonCard1)
        val buttonCard2 = findViewById<CardView>(R.id.buttonCard2)
        val buttonCard3 = findViewById<CardView>(R.id.buttonCard3)


        //blur
        val decorView = window.decorView
        val rootView = findViewById<ViewGroup>(R.id.main)

        val radius = 10f

        val blurViewTemp = findViewById<BlurView>(R.id.blurViewTemp)
        val blurViewVento1 = findViewById<BlurView>(R.id.blurViewVento1)
        val blurViewVento2 = findViewById<BlurView>(R.id.blurViewVento2)
        val blurViewQual = findViewById<BlurView>(R.id.blurViewQual)
        val blurViewExtra = findViewById<BlurView>(R.id.blurViewExtra)
        val blurViewOver = findViewById<BlurView>(R.id.blurViewOver)
        val blurViewBtn1 = findViewById<BlurView>(R.id.blurViewBtn1)
        val blurViewBtn2 = findViewById<BlurView>(R.id.blurViewBtn2)
        val blurViewBtn3 = findViewById<BlurView>(R.id.blurViewBtn3)


        val allBlurViews = listOf(
            blurViewTemp,
            blurViewVento1,
            blurViewVento2,
            blurViewQual,
            blurViewExtra,
            blurViewOver,
            blurViewBtn1,
            blurViewBtn2,
            blurViewBtn3
        )

        for (blurView in allBlurViews) {
            blurView.setupWith(rootView) // Or use RenderEffectBlur for API 31+
                .setBlurAutoUpdate(true)
                .setBlurRadius(radius)

            // This makes the blur respect the CardView's rounded corners
            blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blurView.clipToOutline = true
        }

        mainTxt.animate().alpha(.8f).setDuration(500).start()
        temp.animate().alpha(.99f).setDuration(500).start()
        celo.animate().alpha(.99f).setDuration(500).start()
        qual.animate().alpha(.99f).setDuration(500).start()
        extra.animate().alpha(.99f).setDuration(500).start()


        val hourlyContainer = findViewById<LinearLayout>(R.id.houlryWeather)

        tempTxt.text = "${prefs.getFloat("currentTemp", 0f).toInt()}°"
        sunriseTxt.text = sunrise
        sunsetTxt.text = sunset
        windTxt.text = "${wind}\nm/s"
        placeTxt.text = place
        overviewTxt.text = overview

        hourlyContainer.removeAllViews()

        for (hourlyData in hourlyList) {
            val inflater = layoutInflater
            val hourlyView = inflater.inflate(R.layout.item_hourly_forecast, hourlyContainer, false)

            val timeTextView = hourlyView.findViewById<TextView>(R.id.timeTxt)
            val tempTextView = hourlyView.findViewById<TextView>(R.id.windTitleTxt)
            val iconView = hourlyView.findViewById<ImageView>(R.id.weatherIcon)

            val hour24 = hourlyData.time.toInt()

            if (hour24 == 0) {
                timeTextView.text = "0 AM"
            } else if (hour24 < 12) {
                timeTextView.text = "$hour24 AM"
            } else if (hour24 == 12) {
                timeTextView.text = "12 AM"
            } else {
                val hour12 = hour24 - 12
                timeTextView.text = "$hour12 PM"
            }
            tempTextView.text = "${hourlyData.temp.toInt()}°"

            when (hourlyData.icon) {
                "01d" -> iconView.setImageResource(R.drawable.i01d)
                "02d" -> iconView.setImageResource(R.drawable.i02d)
                "03d" -> iconView.setImageResource(R.drawable.i03d)
                "04d" -> iconView.setImageResource(R.drawable.i04d)
                "09d" -> iconView.setImageResource(R.drawable.i09d)
                "10d" -> iconView.setImageResource(R.drawable.i10d)
                "11d" -> iconView.setImageResource(R.drawable.i11d)
                "13d" -> iconView.setImageResource(R.drawable.i13d)
                "50d" -> iconView.setImageResource(R.drawable.i50d)
                else -> iconView.setImageResource(R.drawable.i03d)
            }

            hourlyContainer.addView(hourlyView)
        }


        val buttonCards = listOf(buttonCard1, buttonCard2, buttonCard3)
        for (card in buttonCards) {
            card.setOnTouchListener { v, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        v.animate().scaleX(0.9f).scaleY(0.8f).setDuration(50).start()
                    }
                    MotionEvent.ACTION_UP -> {
                        v.animate().scaleX(1f).scaleY(1f).setDuration(250).start()
                    }
                }
                false
            }
        }
        buttonCard1.setOnClickListener {
            val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val nextPage = Intent(this, MainActivity::class.java)
            startActivity(nextPage, fadeOptions.toBundle())
        }
        buttonCard2.setOnClickListener {
            // Initialize the editor correctly
            val prefs = getSharedPreferences("app_cache", MODE_PRIVATE)
            val prefsEditor = prefs.edit()

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
                    if (response.hourly[1].icon in listOf("09d", "10d", "11d", "13d")) {
                        willRain = true
                    }
                    prefsEditor.putBoolean("willRain", willRain)

                    val hour = LocalTime.now().hour
                    val backgroundName = if (hour < 7 || hour > 19) {
                        if (willRain) "background_notte_pioggia" else "background_notte"
                    } else {
                        if (willRain) "background_giorno_pioggia" else "background_giorno"
                    }

                    // Put the background name BEFORE applying
                    prefsEditor.putString("background", backgroundName)
                    prefsEditor.apply() // Save changes to disk

                    Toast.makeText(this@FullActivity, "Pagina ricaricata", Toast.LENGTH_SHORT).show()

                    val fadeOptions = ActivityOptions.makeCustomAnimation(this@FullActivity, R.anim.fade_in, R.anim.fade_out)
                    val nextPage = Intent(this@FullActivity, MainActivity::class.java)
                    startActivity(nextPage, fadeOptions.toBundle())

                } catch (e: Exception) {
                    e.printStackTrace()
                    // It's good practice to show a toast if the network request fails
                    Toast.makeText(this@FullActivity, "Errore durante l'aggiornamento", Toast.LENGTH_SHORT).show()
                }
            }
        }
        buttonCard3.setOnClickListener {
            val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val nextPage = Intent(this, SettingsActivity::class.java)
            startActivity(nextPage, fadeOptions.toBundle())
        }
        hourlyWeather.setOnClickListener {
            temp.animate().scaleX(0.9f).scaleY(0.8f).setDuration(250).withEndAction {
                temp.animate().scaleX(1f).scaleY(1f).setDuration(250)
            }.start()
            val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val nextPage = Intent(this, DaysActivity::class.java)
            startActivity(nextPage, fadeOptions.toBundle())
        }





    }
}
