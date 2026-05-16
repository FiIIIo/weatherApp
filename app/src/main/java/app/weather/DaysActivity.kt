package app.weather

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import app.weather.WeatherUtils
import app.weather.api.Daily
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import eightbitlab.com.blurview.BlurView

class DaysActivity: AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_days)

        val prefs = getSharedPreferences("app_cache", MODE_PRIVATE)
        val dailyContainer = findViewById<LinearLayout>(R.id.dailyContainer)
        val json = prefs.getString("daily_list", null)
        val rootView = findViewById<ViewGroup>(R.id.main)

        val backView = findViewById<ImageView>(R.id.backView)
        backView.setImageResource(WeatherUtils.getBackgroundResource(this))

        val type = object : TypeToken<List<Daily>>() {}.type
        val dailyList: List<Daily> =
            if (json != null) Gson().fromJson(json, type) else emptyList()

        val decorView = window.decorView
        val blurViewDays = findViewById<BlurView>(R.id.blurViewDays)
        val blurViewBack = findViewById<BlurView>(R.id.blurViewBack)
        val radius = 10f

        val allBlurViews = listOf(
            blurViewDays,
            blurViewBack
        )

        val extra = findViewById<CardView>(R.id.extra)

        extra.setOnTouchListener { v, event ->
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
        extra.setOnClickListener {
            val fadeOptions = ActivityOptions.makeCustomAnimation(this, R.anim.fade_in, R.anim.fade_out)
            val nextPage = Intent(this, FullActivity::class.java)
            startActivity(nextPage, fadeOptions.toBundle())
        }


        dailyList.forEachIndexed { index, dailyData ->
            Log.d("DaysActivity", "Adding index = $index")
        }

        for (dailyData in dailyList) {
            val inflater = layoutInflater
            val dailyView = inflater.inflate(R.layout.item_daily_forecast, dailyContainer, false)

            val weekday = dailyData.weekday
            val icon = dailyData.icon
            val temp = dailyData.temp

            val iconView = dailyView.findViewById<ImageView>(R.id.icon)
            val tempTextView = dailyView.findViewById<TextView>(R.id.windTitleTxt)
            val weekdayTextView = dailyView.findViewById<TextView>(R.id.weekdayTxt)

            tempTextView.text = "${temp.toFloat().toInt()}°"
            val days = listOf(
                "domenica","lunedì","martedì","mercoledì",
                "giovedì","venerdì","sabato"
            )

            weekdayTextView.text = days[dailyData.weekday.toInt()]

            when (dailyData.icon) {
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

            dailyContainer.addView(dailyView)
        }

        for (blurView in allBlurViews) {
            blurView.setupWith(rootView) // Or use RenderEffectBlur for API 31+
                .setBlurAutoUpdate(true)
                .setBlurRadius(radius)

            // This makes the blur respect the CardView's rounded corners
            blurView.outlineProvider = ViewOutlineProvider.BACKGROUND
            blurView.clipToOutline = true
        }

    }
}