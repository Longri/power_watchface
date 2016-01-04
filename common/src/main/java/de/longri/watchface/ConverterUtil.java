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

public class ConverterUtil {

    public static final int FAHRENHEIT = 1;
    public static final int TIME_UNIT_12 = 0;
    public static final int TIME_UNIT_24 = 1;

    // converts to celsius
    public static float convertFahrenheitToCelsius(float fahrenheit) {
        return ((fahrenheit - 32f) * 5f / 9f);
    }

    // converts to fahrenheit
    public static int convertCelsiusToFahrenheit(int celsius) {
        return ((celsius * 9) / 5) + 32;
    }

    public static String convertToMonth(int month) {
        switch (month) {
            case 0:
                return "January ";
            case 1:
                return "February ";
            case 2:
                return "March ";
            case 3:
                return "April ";
            case 4:
                return "May ";
            case 5:
                return "June ";
            case 6:
                return "July ";
            case 7:
                return "August ";
            case 8:
                return "September ";
            case 9:
                return "October ";
            case 10:
                return "November ";
            default:
                return "December";
        }
    }

    public static String getDaySuffix(int monthDay) {
        switch (monthDay) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static int convertHour(int hour, int timeUnit) {
        if (timeUnit == TIME_UNIT_12) {
            int result = hour % 12;
            return (result == 0) ? 12 : result;
        } else {
            return hour;
        }
    }
}
