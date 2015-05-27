package yeryomenkom.ua.weather;

import retrofit.http.GET;
import retrofit.http.Query;

public interface WeatherSimpleAPI {
    @GET("/data/2.5/weather")
    WeatherResponse getWeather(@Query("q") String cityName);
}
