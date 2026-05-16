package app.weather

import android.content.Context
// Import your R file (make sure this matches your package name)
import app.weather.R

object WeatherUtils {
    fun getBackgroundResource(context: Context): Int {
        val prefs = context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)
        val backgroundName = prefs.getString("background", "background_giorno")

        return when (backgroundName) {
            "background_giorno_pioggia" -> R.drawable.backrainingtrees
            "background_notte_pioggia"  -> R.drawable.backnightrain
            "background_giorno"         -> R.drawable.backpalmtree
            "background_notte"          -> R.drawable.backnightsky2
            else                        -> R.drawable.backpalmtree
        }
    }
}