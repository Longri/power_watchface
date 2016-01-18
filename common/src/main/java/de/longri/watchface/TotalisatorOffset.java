/*
 * Copyright (C) 2016 longri.de
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

import android.content.SharedPreferences;
import android.view.View;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.Serializable;
import de.longri.serializable.StoreBase;

/**
 * Created by Longri on 15.01.2016.
 */
public class TotalisatorOffset implements Serializable {

    private final String _name;
    private final String TOP = "_top";
    private final String RIGHT = "_right";
    private final String BOTTOM = "_bottom";
    private final String LEFT = "_left";

    private ShortPoint offsetTop = new ShortPoint(TOP);
    private ShortPoint offsetRight = new ShortPoint(RIGHT);
    private ShortPoint offsetBottom = new ShortPoint(BOTTOM);
    private ShortPoint offsetLeft = new ShortPoint(LEFT);

    private final short INCREASE_VALUE = 1;

    interface IChanged {
        void isChanged(TotalisatorOffsetPos pos);
    }

    private IChanged changedListener;

    public void setChangedListener(IChanged listener) {
        changedListener = listener;
    }


    //######### Top click listener ##########################################################################
    public final View.OnClickListener Top_x_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetTop.increase_x(INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Top);
        }
    };

    public final View.OnClickListener Top_x_minus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetTop.increase_x((short) -INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Top);
        }
    };

    public final View.OnClickListener Top_y_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetTop.increase_y(INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Top);
        }
    };

    public final View.OnClickListener Top_y_minus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetTop.increase_y((short) -INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Top);
        }
    };


    //######### Right click listener ##########################################################################    
    public final View.OnClickListener Right_x_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetRight.increase_x(INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Right);
        }
    };

    public final View.OnClickListener Right_x_minus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetRight.increase_x((short) -INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Right);
        }
    };

    public final View.OnClickListener Right_y_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetRight.increase_y(INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Right);
        }
    };

    public final View.OnClickListener Right_y_minus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetRight.increase_y((short) -INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Right);
        }
    };

    //######### Bottom click listener ##########################################################################    
    public final View.OnClickListener Bottom_x_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetBottom.increase_x(INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Bottom);
        }
    };

    public final View.OnClickListener Bottom_x_minus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetBottom.increase_x((short) -INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Bottom);
        }
    };

    public final View.OnClickListener Bottom_y_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetBottom.increase_y(INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Bottom);
        }
    };

    public final View.OnClickListener Bottom_y_minus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetBottom.increase_y((short) -INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Bottom);
        }
    };

    //######### Left click listener ##########################################################################    
    public final View.OnClickListener Left_x_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetLeft.increase_x(INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Bottom);
        }
    };

    public final View.OnClickListener Left_x_minus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetLeft.increase_x((short) -INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Left);
        }
    };

    public final View.OnClickListener Left_y_plus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetLeft.increase_y(INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Left);
        }
    };

    public final View.OnClickListener Left_y_minus = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            offsetLeft.increase_y((short) -INCREASE_VALUE);
            if (changedListener != null) changedListener.isChanged(TotalisatorOffsetPos.Left);
        }
    };


    public TotalisatorOffset(String name) {
        _name = name;
    }

    public TotalisatorOffset(String name, StoreBase storeBase) {
        this(name);
        try {
            serialize(storeBase);
        } catch (NotImplementedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof TotalisatorOffset) {
            TotalisatorOffset to = (TotalisatorOffset) o;
            if (this.offsetTop.equals(to.offsetTop)
                    && this.offsetRight.equals(to.offsetRight)
                    && this.offsetBottom.equals(to.offsetBottom)
                    && this.offsetLeft.equals(to.offsetLeft)) return true;

        }
        return false;
    }

    @Override
    public void serialize(StoreBase storeBase) throws NotImplementedException {
        offsetTop.serialize(storeBase);
        offsetRight.serialize(storeBase);
        offsetBottom.serialize(storeBase);
        offsetLeft.serialize(storeBase);
    }

    @Override
    public void deserialize(StoreBase storeBase) throws NotImplementedException {
        deserializeIsChanged(storeBase);
    }

    public boolean deserializeIsChanged(StoreBase storeBase) throws NotImplementedException {
        boolean isChanged = false;
        if (offsetTop.deserializeIsChanged(storeBase)) isChanged = true;
        if (offsetRight.deserializeIsChanged(storeBase)) isChanged = true;
        if (offsetBottom.deserializeIsChanged(storeBase)) isChanged = true;
        if (offsetLeft.deserializeIsChanged(storeBase)) isChanged = true;
        return isChanged;
    }

    public void storeToAndroidPreferences(SharedPreferences.Editor editor) {
        offsetTop.storeToAndroidPreferences(editor);
        offsetRight.storeToAndroidPreferences(editor);
        offsetBottom.storeToAndroidPreferences(editor);
        offsetLeft.storeToAndroidPreferences(editor);
        editor.commit();
    }

    public void readFromAndroidPreferences(SharedPreferences preferences) {
        offsetTop.readFromAndroidPreferences(preferences);
        offsetRight.readFromAndroidPreferences(preferences);
        offsetBottom.readFromAndroidPreferences(preferences);
        offsetLeft.readFromAndroidPreferences(preferences);
    }


    public void increase_top_x(short value) {
        offsetTop.increase_x(value);
    }

    public void increase_top_y(short value) {
        offsetTop.increase_y(value);
    }

    public void increase_right_x(short value) {
        offsetRight.increase_x(value);
    }

    public void increase_right_y(short value) {
        offsetRight.increase_y(value);
    }

    public void increase_bottom_x(short value) {
        offsetBottom.increase_x(value);
    }

    public void increase_bottom_y(short value) {
        offsetBottom.increase_y(value);
    }

    public void increase_left_x(short value) {
        offsetLeft.increase_x(value);
    }

    public void increase_left_y(short value) {
        offsetLeft.increase_y(value);
    }

    public ShortPoint getTop() {
        return offsetTop;
    }

    public ShortPoint getRight() {
        return offsetRight;
    }

    public ShortPoint getBottom() {
        return offsetBottom;
    }

    public ShortPoint getLeft() {
        return offsetLeft;
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TotalisatorOffset:");
        sb.append("\n");

        sb.append("  Top: ");
        sb.append(offsetTop.toString());
        sb.append("\n");
        sb.append("  Right: ");
        sb.append(offsetRight.toString());
        sb.append("\n");
        sb.append("  Bottom: ");
        sb.append(offsetBottom.toString());
        sb.append("\n");
        sb.append("  Left: ");
        sb.append(offsetLeft.toString());
        sb.append("\n");

        return sb.toString();
    }


}
