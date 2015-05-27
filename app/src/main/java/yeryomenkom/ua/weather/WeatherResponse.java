package yeryomenkom.ua.weather;



public class WeatherResponse {
    /*
    не использую все параметры погоды из ответа сервера, только основные(на мой взгляд)
     */
    String name;
    Coordinates coord;
    Weather[] weather;
    MainInfo main;
    Wind wind;
    Clouds clouds;

    static class Coordinates {
        float lon, lat;
    }

    static class Weather {
        String main,description,icon;
    }

    static class MainInfo {
        float temp, pressure, humidity;
    }

    static class Wind {
        float speed, deg;
    }

    static class Clouds {
        float all;
    }

    String toNiceString(){
        return "City : "+name+"\n"+
                "Main: "+weather[0].main+"\n"+
                "Description: "+weather[0].description+"\n"+
                "Temperature: "+Math.round(main.temp-273.15)+"\u00B0\n"+
                "Pressure: "+main.pressure+"hPa\n"+
                "Humidity: "+main.humidity+"%\n"+
                "Cloudiness: "+clouds.all+"%\n"+
                "Wind speed: "+wind.speed+"mps\n"+
                "Wind direction: "+getStringDirectionInLetters(wind.deg);
    }

    String getStringDirectionInLetters(float deg) {
        String directions[] = {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "N"};
        return directions[ Math.round(((deg % 360) / 45)) ];
    }
}
