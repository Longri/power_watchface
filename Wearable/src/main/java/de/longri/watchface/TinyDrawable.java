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

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;

import java.util.ArrayList;

/**
 * Created by Longri on 17.11.15.
 */
public abstract class TinyDrawable {


    private static ArrayList<TinyDrawable> instanceList = new ArrayList<>();
    private boolean mustDraw = false;

    protected final Context mContext;
    private boolean isInAmbientMode = false;
    protected static RES res;
    protected final Matrix EMPTY_MATRIX = new Matrix();

    public static void setAmbientMode(boolean value) {
        for (TinyDrawable tiny : instanceList) {

            if (tiny.isInAmbientMode != value) {
                tiny.setMustRedraw();
            }
            tiny.isInAmbientMode = value;
        }
    }


    public interface InvalidateListener {
        public void invalidate();

        public void resetFullDraw();

        public void setAmbientMode(boolean value);
    }

    public abstract boolean hasFullDraw();

    protected InvalidateListener listener;

    public void setInvalidateListener(InvalidateListener listener) {
        this.listener = listener;
    }

    protected void Invalidate() {
        if (listener != null) listener.invalidate();
    }

    public static void setRes(RES value) {
        res = value;
    }

    public boolean isInAmbientMode() {
        return isInAmbientMode;
    }

    public void setIsInAmbientMode(boolean value) {

        //if changed
        if (isInAmbientMode != value) {
            isInAmbientMode = value;
            mustDraw = true;
        }


    }

    public abstract boolean draw(Canvas canvas, Matrix matrix);

    public abstract boolean drawFull(Canvas canvas, Matrix matrix);

    public abstract void onTapCommand(int tapType, int x, int y, long eventTime);

    public abstract WatchFaceView getViewType();

    public TinyDrawable(Context context) {
        this.mContext = context;
        instanceList.add(this);
    }

    protected void drawTinyBackground(Canvas canvas, Matrix matrix) {
        if (isInAmbientMode) {
            canvas.drawBitmap(res.getTinyAmbientBackground(), matrix, RES.mAntiAliasPaint_noGreyScale);
        } else {
            canvas.drawBitmap(res.getTinyBackground(), matrix, RES.mAntiAliasPaint_noGreyScale);
        }
    }

    public void setInAmbientMode(boolean value) {
        isInAmbientMode = value;
    }


    public boolean mustRedraw() {
        return mustDraw;
    }

    public void setMustRedraw() {
        this.mustDraw = true;
    }

    public void resetMustRedraw() {
        this.mustDraw = false;
    }

}
