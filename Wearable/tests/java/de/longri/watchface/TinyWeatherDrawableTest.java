package de.longri.watchface;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.text.format.Time;
import de.longri.weather.Info;

import java.util.Date;


/**
 * Created by Longri on 25.11.2015.
 */
@MediumTest
public class TinyWeatherDrawableTest extends InstrumentationTestCase {

    public void testGetWeatherRequestType() throws Exception {

        RES.mTime = new Time();
        RES.mTime.setToNow();
        RES.mTime.normalize(true);

        TinyWeatherDrawable weatherDrawable = new TinyWeatherDrawable(getInstrumentation().getContext());
        assertTrue(weatherDrawable.getWeatherRequestType() == WeatherInfoType.FORECAST);


        Info infoNow = new Info();
        infoNow.setDate(new Date());
        weatherDrawable.setActWeather(infoNow);
        assertTrue(weatherDrawable.getWeatherRequestType() == WeatherInfoType.FORECAST);


        weatherDrawable.setWeatherForecast1(infoNow);
        weatherDrawable.setWeatherForecast2(infoNow);
        assertTrue(weatherDrawable.getWeatherRequestType() == WeatherInfoType.FORECAST);


        RES.mTime.set(0, 0, 12, 11, 11, 2015);
        RES.mTime.normalize(true);
        Info info1 = new Info();
        Info info2 = new Info();
        Info info3 = new Info();

        info1.setDate("2015-11-11 12:00:00");
        info2.setDate("2015-11-11 13:31:00");
        info3.setDate("2015-11-11 16:31:00");

        weatherDrawable.setActWeather(info1);
        weatherDrawable.setWeatherForecast1(info2);
        weatherDrawable.setWeatherForecast2(info3);

        assertTrue(weatherDrawable.getWeatherRequestType() == WeatherInfoType.NOW);
        RES.mTime.set(0, 29, 11, 11, 11, 2015);
        RES.mTime.normalize(true);
        assertTrue(weatherDrawable.getWeatherRequestType() == WeatherInfoType.NOW);

        RES.mTime.set(0, 0, 12, 11, 11, 2015);
        RES.mTime.normalize(true);
        info1.setDate("2015-11-11 11:29:00");
        info2.setDate("2015-11-11 13:00:00");
        info3.setDate("2015-11-11 16:00:00");

        weatherDrawable.setActWeather(info1);
        weatherDrawable.setWeatherForecast1(info2);
        weatherDrawable.setWeatherForecast2(info3);

        assertTrue(weatherDrawable.getWeatherRequestType() == WeatherInfoType.FORECAST);
        RES.mTime.set(0, 29, 11, 11, 11, 2015);
        RES.mTime.normalize(true);
        assertTrue(weatherDrawable.getWeatherRequestType() == WeatherInfoType.NOW);

    }
}