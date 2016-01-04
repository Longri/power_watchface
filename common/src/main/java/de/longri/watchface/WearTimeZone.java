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

import de.longri.serializable.NotImplementedException;
import de.longri.serializable.Serializable;
import de.longri.serializable.StoreBase;

import java.util.SimpleTimeZone;

/**
 * Created by Longri on 14.12.2015.
 */
public class WearTimeZone extends SimpleTimeZone implements Serializable {

    private final static WearTimeZone[] TimeZoneList = new WearTimeZone[]{
            new WearTimeZone((byte) 0, -43200000, "(GMT-12:00) International Date Line West"),
            new WearTimeZone((byte) 1, -39600000, "(GMT-11:00) Midway Island, Samoa"),
            new WearTimeZone((byte) 2, -36000000, "(GMT-10:00) Hawaii"),
            new WearTimeZone((byte) 3, -32400000, "(GMT-09:00) Alaska"),
            new WearTimeZone((byte) 4, -28800000, "(GMT-08:00) Pacific Time (US and Canada); Tijuana"),
            new WearTimeZone((byte) 5, -25200000, "(GMT-07:00) Mountain Time (US and Canada)"),
            new WearTimeZone((byte) 6, -25200000, "(GMT-07:00) Chihuahua, La Paz, Mazatlan"),
            new WearTimeZone((byte) 7, -25200000, "(GMT-07:00) Arizona"),
            new WearTimeZone((byte) 8, -21600000, "(GMT-06:00) Central Time (US and Canada"),
            new WearTimeZone((byte) 9, -21600000, "(GMT-06:00) Saskatchewan"),
            new WearTimeZone((byte) 10, -21600000, "(GMT-06:00) Guadalajara, Mexico City, Monterrey"),
            new WearTimeZone((byte) 11, -21600000, "(GMT-06:00) Central America"),
            new WearTimeZone((byte) 12, -18000000, "(GMT-05:00) Eastern Time (US and Canada)"),
            new WearTimeZone((byte) 13, -18000000, "(GMT-05:00) Indiana (East)"),
            new WearTimeZone((byte) 14, -18000000, "(GMT-05:00) Bogota, Lima, Quito"),
            new WearTimeZone((byte) 15, -14400000, "(GMT-04:00) Atlantic Time (Canada)"),
            new WearTimeZone((byte) 16, -14400000, "(GMT-04:00) Caracas, La Paz"),
            new WearTimeZone((byte) 17, -14400000, "(GMT-04:00) Santiago"),
            new WearTimeZone((byte) 18, -12600000, "(GMT-03:30) Newfoundland and Labrador"),
            new WearTimeZone((byte) 19, -10800000, "(GMT-03:00) Brasilia"),
            new WearTimeZone((byte) 20, -10800000, "(GMT-03:00) Buenos Aires, Georgetown"),
            new WearTimeZone((byte) 21, -10800000, "(GMT-03:00) Greenland"),
            new WearTimeZone((byte) 22, -7200000, "(GMT-02:00) Mid-Atlantic"),
            new WearTimeZone((byte) 23, -3600000, "(GMT-01:00) Azores"),
            new WearTimeZone((byte) 24, -3600000, "(GMT-01:00) Cape Verde Islands"),
            new WearTimeZone((byte) 25, 0, "(GMT) Greenwich Mean Time: Dublin, Edinburgh, Lisbon, London"),
            new WearTimeZone((byte) 26, 0, "(GMT) Casablanca, Monrovia"),
            new WearTimeZone((byte) 27, 3600000, "(GMT+01:00) Belgrade, Bratislava, Budapest, Ljubljana, Prague"),
            new WearTimeZone((byte) 28, 3600000, "(GMT+01:00) Sarajevo, Skopje, Warsaw, Zagreb"),
            new WearTimeZone((byte) 29, 3600000, "(GMT+01:00) Brussels, Copenhagen, Madrid, Paris"),
            new WearTimeZone((byte) 30, 3600000, "(GMT+01:00) Amsterdam, Berlin, Bern, Rome, Stockholm, Vienna"),
            new WearTimeZone((byte) 31, 3600000, "(GMT+01:00) West Central Africa"),
            new WearTimeZone((byte) 32, 7200000, "(GMT+02:00) Bucharest"),
            new WearTimeZone((byte) 33, 7200000, "(GMT+02:00) Cairo"),
            new WearTimeZone((byte) 34, 7200000, "(GMT+02:00) Helsinki, Kiev, Riga, Sofia, Tallinn, Vilnius"),
            new WearTimeZone((byte) 35, 7200000, "(GMT+02:00) Athens, Istanbul, Minsk"),
            new WearTimeZone((byte) 36, 7200000, "(GMT+02:00) Jerusalem"),
            new WearTimeZone((byte) 37, 7200000, "(GMT+02:00) Harare, Pretoria"),
            new WearTimeZone((byte) 38, 10800000, "(GMT+03:00) Moscow, St. Petersburg, Volgograd"),
            new WearTimeZone((byte) 39, 10800000, "(GMT+03:00) Kuwait, Riyadh"),
            new WearTimeZone((byte) 40, 10800000, "(GMT+03:00) Nairobi"),
            new WearTimeZone((byte) 41, 10800000, "(GMT+03:00) Baghdad"),
            new WearTimeZone((byte) 42, 12600000, "(GMT+03:30) Tehran"),
            new WearTimeZone((byte) 43, 14400000, "(GMT+04:00) Abu Dhabi, Muscat"),
            new WearTimeZone((byte) 44, 14400000, "(GMT+04:00) Baku, Tbilisi, Yerevan"),
            new WearTimeZone((byte) 45, 16200000, "(GMT+04:30) Kabul"),
            new WearTimeZone((byte) 46, 18000000, "(GMT+05:00) Ekaterinburg"),
            new WearTimeZone((byte) 47, 18000000, "(GMT+05:00) Islamabad, Karachi, Tashkent"),
            new WearTimeZone((byte) 48, 19800000, "(GMT+05:30) Chennai, Kolkata, Mumbai, New Delhi"),
            new WearTimeZone((byte) 49, 20700000, "(GMT+05:45) Kathmandu"),
            new WearTimeZone((byte) 50, 21600000, "(GMT+06:00) Astana, Dhaka"),
            new WearTimeZone((byte) 51, 21600000, "(GMT+06:00) Sri Jayawardenepura"),
            new WearTimeZone((byte) 52, 21600000, "(GMT+06:00) Almaty, Novosibirsk"),
            new WearTimeZone((byte) 53, 23400000, "(GMT+06:30) Yangon Rangoon"),
            new WearTimeZone((byte) 54, 25200000, "(GMT+07:00) Bangkok, Hanoi, Jakarta"),
            new WearTimeZone((byte) 55, 25200000, "(GMT+07:00) Krasnoyarsk"),
            new WearTimeZone((byte) 56, 28800000, "(GMT+08:00) Beijing, Chongqing, Hong Kong SAR, Urumqi"),
            new WearTimeZone((byte) 57, 28800000, "(GMT+08:00) Kuala Lumpur, Singapore"),
            new WearTimeZone((byte) 58, 28800000, "(GMT+08:00) Taipei"),
            new WearTimeZone((byte) 59, 28800000, "(GMT+08:00) Perth"),
            new WearTimeZone((byte) 60, 28800000, "(GMT+08:00) Irkutsk, Ulaanbaatar"),
            new WearTimeZone((byte) 61, 32400000, "(GMT+09:00) Seoul"),
            new WearTimeZone((byte) 62, 32400000, "(GMT+09:00) Osaka, Sapporo, Tokyo"),
            new WearTimeZone((byte) 63, 32400000, "(GMT+09:00) Yakutsk"),
            new WearTimeZone((byte) 64, 34200000, "(GMT+09:30) Darwin\n"),
            new WearTimeZone((byte) 65, 34200000, "(GMT+09:30) Adelaide"),
            new WearTimeZone((byte) 66, 36000000, "(GMT+10:00) Canberra, Melbourne, Sydney"),
            new WearTimeZone((byte) 67, 36000000, "(GMT+10:00) Brisbane"),
            new WearTimeZone((byte) 68, 36000000, "(GMT+10:00) Hobart"),
            new WearTimeZone((byte) 69, 36000000, "(GMT+10:00) Vladivostok"),
            new WearTimeZone((byte) 70, 36000000, "(GMT+10:00) Guam, Port Moresby"),
            new WearTimeZone((byte) 71, 39600000, "(GMT+11:00) Magadan, Solomon Islands, New Caledonia"),
            new WearTimeZone((byte) 72, 43200000, "(GMT+12:00) Fiji Islands, Kamchatka, Marshall Islands"),
            new WearTimeZone((byte) 73, 43200000, "(GMT+12:00) Auckland, Wellington"),
            new WearTimeZone((byte) 74, 46800000, "(GMT+13:00) Nuku'alofa")
    };


    private byte index;
    private String offsetString;

    public WearTimeZone(byte idx, int offset, String name) {
        super(offset, name);
        setOffsetString(name);
        index = idx;
    }

    private void setOffsetString(String name) {
        offsetString = name.substring(4, 10);
        if (!offsetString.startsWith("+") && !offsetString.startsWith("-")) {
            offsetString = "";
        }
    }


    public WearTimeZone(StoreBase storeBase) {
        super(0, "");
        try {
            deserialize(storeBase);
        } catch (NotImplementedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialize(StoreBase storeBase) throws NotImplementedException {
        storeBase.write(index);
    }

    @Override
    public void deserialize(StoreBase storeBase) throws NotImplementedException {

        index = storeBase.readByte();
        WearTimeZone tz = TimeZoneList[index];

        super.setRawOffset(tz.getRawOffset());
        super.setID(tz.getID());
        setOffsetString(super.getID());
    }

    public boolean equals(Object other) {
        if (other instanceof WearTimeZone) {
            if (this.index == ((WearTimeZone) other).index) return true;
        }
        return false;
    }

    public static WearTimeZone[] getAvailableTimeZones() {
        return TimeZoneList;
    }

    public int getIndex() {
        return index;
    }

}
