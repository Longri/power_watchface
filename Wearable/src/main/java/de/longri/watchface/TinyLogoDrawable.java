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


/**
 * Created by Longri on 18.11.2015.
 */
public class TinyLogoDrawable extends TinyDrawable {


    public TinyLogoDrawable(Context context) {
        super(context);
    }

    @Override
    public boolean hasFullDraw() {
        return false;
    }

    @Override
    public boolean draw(Canvas canvas, Matrix matrix) {
        if (isInAmbientMode()) {
            canvas.drawBitmap(RES.getTinyAmbientLogo(), matrix, RES.mAntiAliasPaint_noGreyScale);
        } else {
            canvas.drawBitmap(RES.getTinyLogo(), matrix, RES.mAntiAliasPaint_noGreyScale);
        }
        return true;
    }


    @Override
    public boolean drawFull(Canvas canvas, Matrix matrix) {
        return false;
    }

    @Override
    public void onTapCommand(int tapType, int x, int y, long eventTime) {

    }

    @Override
    public View getViewType() {
        return View.Logo;
    }
}
