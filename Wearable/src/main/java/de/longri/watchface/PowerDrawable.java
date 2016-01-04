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

import android.graphics.*;
import de.longri.watchface.loging.Log;
import de.longri.watchface.loging.LogType;


public class PowerDrawable {


    private Paint color;
    private int power = 100;
    final private float mStartAngle;
    final private float directionMilti;

    public enum Direction {
        LEFT, RIGHT
    }


    public PowerDrawable(int r, int g, int b, float startAngle, Direction direction) {
        this.color = new Paint();
        this.color.setColor(Color.rgb(r, g, b));
        color.setAntiAlias(true);
        color.setStrokeWidth(4);
        color.setStyle(Paint.Style.STROKE);
        this.mStartAngle = startAngle;
        directionMilti = direction == Direction.RIGHT ? -1.8f : 1.8f;
    }

    public void SetColor(int r, int g, int b) {
        this.color.setColor(Color.rgb(r, g, b));
    }

    public void setColorFilter(ColorMatrixColorFilter filter) {
        this.color.setColorFilter(filter);
    }

    public void draw(Canvas canvas, int width, int height) {
        RectF rectF = new RectF(5, 5, width - 5, height - 5);
        canvas.drawArc(rectF, mStartAngle, directionMilti * this.power, false, this.color);
    }

    public void setPowerPercent(int batteryPercent) {
        if (Log.isLoggable(LogType.POWER)) {
            Log.d(Consts.TAG_WEAR, "set batteryPercent:" + batteryPercent);
        }
        this.power = batteryPercent;
        this.color.setColor(getBatteryColor(batteryPercent));
    }


    private int getBatteryColor(int batteryPercent) {

        if (batteryPercent > 100) batteryPercent = 100;
        if (batteryPercent < 0) batteryPercent = 0;

        float s = 2.55f;
        float s2 = 1.275f;

        int r = 255 - ((int) (s2 * batteryPercent));
        int g = (int) (s * batteryPercent);
        int b = 0;

        if (Log.isLoggable(LogType.POWER)) {
            Log.d(Consts.TAG_WEAR, "return color rgb:" + r + "|" + g + "|" + b + "  / from value:" + batteryPercent);
        }

        return Color.rgb(r, g, b);
    }
}
