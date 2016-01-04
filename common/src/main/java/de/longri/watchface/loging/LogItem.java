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
package de.longri.watchface.loging;

import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.Serializable;
import de.longri.serializable.StoreBase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Longri on 04.12.2015.
 */
public class LogItem implements Serializable, Comparable<LogItem> {
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd. HH:mm:ss", Locale.ENGLISH);
    private static final String SPACE = " ";
    private static final String RETURN = "\n";


    boolean error;
    boolean wear;
    Date date;
    String tag;
    String massage;


    public LogItem() {
    }

    public LogItem(boolean Wear, boolean Error, String Tag, String Massage) {
        date = new Date();
        tag = Tag;
        massage = Massage;
        error = Error;
        wear = Wear;
    }


    @Override
    public void serialize(StoreBase storeBase) throws NotImplementedException {
        storeBase.write(error);
        storeBase.write(wear);
        storeBase.write(date.getTime());
        storeBase.write(tag);
        storeBase.write(massage);
    }

    @Override
    public void deserialize(StoreBase storeBase) throws NotImplementedException {
        error = storeBase.readBool();
        wear = storeBase.readBool();
        date = new Date(storeBase.readLong());
        tag = storeBase.readString();
        massage = storeBase.readString();
    }

    public Spannable getSpannable() {

        int color;

        if (wear) {
            if (error) {
                color = Color.MAGENTA;
            } else {
                color = Color.rgb(50, 0, 200);
            }
        } else {
            if (error) {
                color = Color.RED;
            } else {
                color = Color.BLACK;
            }
        }

        StringBuilder sb = new StringBuilder();

        sb.append(DATE_FORMATTER.format(this.date));
        sb.append(SPACE);
        sb.append(this.tag);
        sb.append(SPACE);
        sb.append(this.massage);
        sb.append(RETURN);

        Spannable colorMassage = new SpannableString(sb.toString());
        colorMassage.setSpan(new ForegroundColorSpan(color), 0, sb.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        return colorMassage;
    }

    @Override
    public int compareTo(LogItem another) {

        if (this.date != null && (another == null | another.date == null)) return 1;
        if (this.date == null && (another == null || another.date == null)) return 0;
        if (this.date == null && (another != null && another.date != null)) return -1;
        return date.compareTo(another.date);
    }
}
