/*
 * Copyright (C) 2015-2016 longri.de
 *
 * Licensed under the : GNU General Public License (GPL);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/gpl.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.longri.watchface;

public class Consts {

	public static final boolean RELEASE = true; //TODO set RELEASE flag


	public static final String TAG_PHONE = "PHONE";
	public static final String TAG_WEAR = "WEAR ";


	public static final String PATH_CONFIG = "/WeatherWatchFace/Config/";
	public static final String PATH_WEATHER_INFO = "/WeatherWatchFace/alt_WeatherInfo";
	public static final String PATH_WEATHER_REQUIRE = "/WeatherService/Require";
	public static final String PATH_WEATHER_REQUIRE_FORECAST = "/WeatherService/Forecast";
	public static final String PATH_PHONE_POWER = "/Phone/Power";


	public static final String KEY_CONFIG = "config";
	public static final String KEY_WEATHER_INFO = "weather";
	public static final String KEY_POWER_INFO = "power";
	public static final String KEY_LOGS = "log";

	public static final String KEY_WEATHER_UPDATE_TIME = "Update_Time";
	public static final String KEY_DEBUG = "debug";


	public static final String PREFS_NAME = "LongriPrefsFile";


	public static final String KEY_CONFIG_TIME_UNIT = "TimeUnit";
	public static final String KEY_CONFIG_THEME = "Theme";
	public static final String KEY_CONFIG_TEMPERATURE_SCALE = "UseCelsius";
	public static final String KEY_CONFIG_VIEW_TOP = "ViewTop";
	public static final String KEY_CONFIG_VIEW_RIGHT = "ViewRight";
	public static final String KEY_CONFIG_VIEW_BOTTOM = "ViewBottom";
	public static final String KEY_CONFIG_VIEW_LEFT = "ViewLeft";
	public static final String KEY_CONFIG_LAST_WEATHER_INFO = "WeatherInfo";
	public static final String KEY_CONFIG_DEBUG_INTERVAL_DEVISOR = "debugIntervallDevisor";

	public static final String KEY_LOG_WEATHER = "LogWeather";
	public static final String KEY_LOG_COMMUNICATION = "LogCommunication";
	public static final String KEY_LOG_POWER = "LogPower";
	public static final String KEY_LOG_DRAW = "LogDraw";
	public static final String KEY_LOG_HTTP = "LogHTTP";
	public static final String KEY_LOG_TouchInput = "LogTouch";
	public static final String KEY_CONFIG_DIGI = "digital";
	public static final String KEY_SECOND_TIME_ZONE = "timeZone";
	public static final String KEY_BRIGHTNESS = "brightness";
	public static final String KEY_TOTALISATOR_ZOOM = "totalisatorZoom";
	public static final String KEY_TOTALISATOR_MARGIN = "totalisatorMargin";

	public static final String KEY_CONFIG_SCALE = "scale";
	public static final String KEY_CONFIG_SCALE_VALUE = "scale_value";
	public static final String KEY_CONFIG_SCALE_AMBIENT = "scale_ambient";
	public static final String KEY_CONFIG_SCALE_VALUE_AMBIENT = "scale_value_ambient";

}
