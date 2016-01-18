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
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.Serializable;
import de.longri.serializable.StoreBase;

/**
 * Created by Longri on 15.01.2016.
 */
public class ShortPoint implements Serializable {

    private final String _name;
    private final String X = "_x";
    private final String Y = "_y";

    private short _x;
    private short _y;

    public ShortPoint(String name) {
        _name = name;
    }


    public ShortPoint(String name, StoreBase storebase) {
        this(name);
        try {
            deserialize(storebase);
        } catch (NotImplementedException e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean equals(Object o) {

        if (o instanceof ShortPoint) {
            ShortPoint sp = (ShortPoint) o;
            if (this._x == sp._x && this._y == sp._y) return true;
        }

        return false;
    }


    @Override
    public void serialize(StoreBase storeBase) throws NotImplementedException {
        storeBase.write(_x);
        storeBase.write(_y);
    }

    @Override
    public void deserialize(StoreBase storeBase) throws NotImplementedException {
        _x = storeBase.readShort();
        _y = storeBase.readShort();
    }

    public boolean deserializeIsChanged(StoreBase storeBase) throws NotImplementedException {
        boolean isChanged = false;

        short value = storeBase.readShort();
        if (value != _x) isChanged = true;
        _x = value;

        value = storeBase.readShort();
        if (value != _y) isChanged = true;
        _y = value;

        return isChanged;
    }

    public short get_x() {
        return _x;
    }

    public short get_y() {
        return _y;
    }

    public void set_x(short x) {
        _x = x;
    }

    public void set_y(short y) {
        _y = y;
    }

    public void increase_x(short x) {
        _x += x;
    }

    public void increase_y(short y) {
        _y += y;
    }

    public void storeToAndroidPreferences(SharedPreferences.Editor editor) {
        editor.putInt(_name + X, _x);
        editor.putInt(_name + Y, _y);
    }

    public void readFromAndroidPreferences(SharedPreferences preferences) {
        _x = (short) preferences.getInt(_name + X, 0);
        _y = (short) preferences.getInt(_name + Y, 0);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("x= ");
        sb.append(_x);
        sb.append("/");
        sb.append("y= ");
        sb.append(_y);

        return sb.toString();
    }

    public void Reset() {
        _x = 0;
        _y = 0;
    }
}
