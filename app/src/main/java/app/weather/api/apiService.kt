package app.weather.api

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

data class Hourly(
    val time: String,
    val temp: Double,
    val icon: String
)

data class Current(
    val sunrise: String,
    val sunset: String,
    val wind_speed: Double,
    val temp: Double,
    val place: String,
)

data class Daily(
    val weekday: String,
    val icon: String,
    val temp: String,
    val overview: String,
)

data class Response(
    val hourly: List<Hourly>,
    val current: Current,
    val daily: List<Daily>
)

data class cords(
    val lat : Double,
    val lon : Double
)

interface apiService {

    @GET("/forecast")
    suspend fun getTemp(): Response

    @POST("/forecast")
    suspend fun getTemp(@Body request: cords): Response
}
