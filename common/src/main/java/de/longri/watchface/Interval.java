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
 * Created by Longri on 28.10.15.
 */
public class Interval {


    private static int debugDivisor = 1;

    public static void setDebugDivisor(int value) {
        if (value <= 0) value = 1;
        debugDivisor = value;
    }


    private long interval = 1000;
    private final long minimumQueryTime;
    private long lastElapsed = Long.MIN_VALUE;
    private long lastQuery = Long.MIN_VALUE;

    public Interval(long minimumQueryTime) {
        this.minimumQueryTime = minimumQueryTime;
    }


    public void setInterval(long value) {
        this.interval = value;
    }

    public long getInterval() {
        return this.interval;
    }

    /**
     * Returns @true if the interval elapsed!
     *
     * @return
     */
    public boolean isElapsed() {

        long currentTimeMillis = System.currentTimeMillis();

        if (lastQuery == Long.MIN_VALUE) {
            lastQuery = currentTimeMillis;
            restartInterval();
            return true;
        }

        if (debugDivisor <= 0) debugDivisor = 1;

        if (lastQuery + (minimumQueryTime / debugDivisor) > currentTimeMillis) return false;

        if (lastElapsed == Long.MIN_VALUE) {
            lastQuery = currentTimeMillis;
            return true;
        }
        if (lastElapsed + (interval / debugDivisor) >= currentTimeMillis) return false;

        return true;
    }

    public boolean restartInterval() {
        lastElapsed = System.currentTimeMillis();
        return true;
    }

    public String toString() {

        StringBuilder sb = new StringBuilder();

        if (lastQuery == Long.MIN_VALUE || lastElapsed == Long.MIN_VALUE) {
            sb.append("INVALID");
        } else {
            if (lastElapsed + interval >= System.currentTimeMillis()) {
                sb.append(System.currentTimeMillis() - lastElapsed);
            } else {
                sb.append("0");
            }
        }

        sb.append("/");
        sb.append(Long.toString(interval));

        return sb.toString();
    }

}
