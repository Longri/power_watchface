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
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by Longri on 14.01.2016.
 */
public class TotalisatorView extends LinearLayout {


    ImageView imageView;
    Button btnTop;
    Button btnRight;
    Button btnBottom;
    Button btnLeft;

    public TotalisatorView(Context context, AttributeSet attrs) {
        super(context, attrs);

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.totalisator_view, this, true);

        imageView = (ImageView) findViewById(R.id.imageViewTotalisator);

        btnTop = (Button) findViewById(R.id.buttonTop);
        btnRight = (Button) findViewById(R.id.buttonRight);
        btnBottom = (Button) findViewById(R.id.buttonBottom);
        btnLeft = (Button) findViewById(R.id.buttonLeft);
    }

    public void setImageDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }


    public void setButtonVisibility(int visibility) {
        btnTop.setVisibility(visibility);
        btnRight.setVisibility(visibility);
        btnBottom.setVisibility(visibility);
        btnLeft.setVisibility(visibility);
    }

    public void setOnButtonClickListener(OnClickListener x_plus, OnClickListener x_minus, OnClickListener y_plus, OnClickListener y_minus) {
        btnRight.setOnClickListener(x_plus);
        btnLeft.setOnClickListener(x_minus);
        btnTop.setOnClickListener(y_minus);
        btnBottom.setOnClickListener(y_plus);
    }
}
