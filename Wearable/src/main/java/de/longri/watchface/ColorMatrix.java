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

/**
 * Holds all used Color Matrix
 *
 * R' = a*R + b*G + c*B + d*A + e;
 * G' = f*R + g*G + h*B + i*A + j;
 * B' = k*R + l*G + m*B + n*A + o;
 * A' = p*R + q*G + r*B + s*A + t;
 *
 *
 * Created by Longri on 10.07.2015.
 */
public class ColorMatrix {

    private ColorMatrix(){}

    public static final float[] AMBIENT = new float[]{1, 0.4f, 0.4f, 0, 0,
            0.4f, 1, 0.4f, 0, 0,
            0.4f, 0.4f, 1, 0, 0,
            0, 0, 0, 1, 0};


    public static final float[] AMBIENT_GRAY_SCALE =new float[] {   0, 0, 1, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 1, 0, 0,
            0, 0, 0, 1, 0};

}
