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

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;
import android.widget.TextView;
import de.longri.watchface.Consts;
import de.longri.serializable.BitStore;
import de.longri.serializable.NotImplementedException;
import de.longri.serializable.SerializableArrayList;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Hoepfner on 04.12.2015.
 */
public class Log {


    private static SharedPreferences mAndroidSetting;
    private static SharedPreferences.Editor mAndroidSettingEditor;

    public static boolean isInitial() {
        return THAT != null;
    }

    public static byte[] getLogsForSend() throws NotImplementedException {

        synchronized (logs) {
            BitStore writer = new BitStore();
            logs.serialize(writer);
            return writer.getArray();
        }
    }

    public static void setLoggable(LogTo loggable) {
        logTo = loggable;
    }

    public static void clear() {

        logs = new SerializableArrayList<>(LogItem.class);
        //save
        BitStore writer = new BitStore();
        logs.serialize(writer);

        String saveString = null;
        try {
            saveString = Base64.encodeToString(writer.getArray(), Base64.DEFAULT);
        } catch (NotImplementedException e) {
            e.printStackTrace();
        }
        mAndroidSettingEditor.putString(Consts.KEY_LOGS, saveString);
        mAndroidSettingEditor.commit();

    }

    public enum LogTo {
        NONE, Locat, TextView, Both
    }


    private static int maxMassagLines = 500;
    private static LogTo logTo = LogTo.Both;
    public static SerializableArrayList<LogItem> logs = new SerializableArrayList<>(LogItem.class);
    private static Log THAT;
    private static final ArrayList<LogType> logTypeList = new ArrayList<>();


    private final boolean watch;

    private static Context context;


    public Log(Context cont, boolean Watch) {
        this.watch = Watch;
        THAT = this;
        context = cont;
    }


    public static void addLogType(LogType type) {
        if (!logTypeList.contains(type)) logTypeList.add(type);
    }

    public static void removeLogType(LogType type) {
        logTypeList.remove(type);
    }


    public static boolean isLoggable() {
        return logTo != LogTo.NONE;
    }

    public static boolean isLoggable(LogType type) {
        if (!isLoggable()) return false;
        return logTypeList.contains(type);
    }

    public static boolean isLoggable(LogType... type) {
        if (logTo == LogTo.NONE) return false;

        boolean log = false;
        for (LogType t : type) {
            if (logTypeList.contains(t)) {
                log = true;
                break;
            }
        }
        return log;
    }


    public static void Item(LogItem item) {
        if (logTo == LogTo.Both) {
            if (logTo == LogTo.Both || logTo == LogTo.Locat) {
                if (item.error) {
                    android.util.Log.e(item.tag, item.massage);
                } else {
                    android.util.Log.d(item.tag, item.massage);
                }
            }
            if ((logTo == LogTo.Both || logTo == LogTo.TextView) && THAT != null) {
                synchronized (logs) {
                    try {
                        addLogItem(item);
                    } catch (NotImplementedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }


    public static void d(String Tag, String Massage) {
        if (logTo == LogTo.Both) {
            if (logTo == LogTo.Both || logTo == LogTo.Locat) {
                android.util.Log.d(Tag, Massage);
            }
            if ((logTo == LogTo.Both || logTo == LogTo.TextView) && THAT != null) {
                synchronized (logs) {
                    try {
                        addLogItem(new LogItem(THAT.watch, false, Tag, Massage));
                    } catch (NotImplementedException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    public static void e(String Tag, String Massage) {
        if (logTo == LogTo.Both || logTo == LogTo.Locat) {
            android.util.Log.e(Tag, Massage);
        }


        if ((logTo == LogTo.Both || logTo == LogTo.TextView) && THAT != null) {
            synchronized (logs) {
                try {
                    addLogItem(new LogItem(THAT.watch, true, Tag, Massage));
                } catch (NotImplementedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static void addLogItem(LogItem item) throws NotImplementedException {

        logs.add(item);

        if (logs.size() >= maxMassagLines) {
            // save to Prefs

            chkPreferences();

            //first load from prefs and static logs
            SerializableArrayList<LogItem> savedLogs = readLogs();

            //save
            BitStore writer = new BitStore();
            savedLogs.serialize(writer);

            String saveString = Base64.encodeToString(writer.getArray(), Base64.DEFAULT);
            mAndroidSettingEditor.putString(Consts.KEY_LOGS, saveString);
            mAndroidSettingEditor.commit();

            // initial new empty buffer
            logs = new SerializableArrayList<>(LogItem.class);


        }
    }


    private static SerializableArrayList<LogItem> readLogs() {
        chkPreferences();
        SerializableArrayList<LogItem> savedLogs = new SerializableArrayList<>(LogItem.class);
        byte[] save = Base64.decode(mAndroidSetting.getString(Consts.KEY_LOGS, ""), Base64.DEFAULT);

        if (save.length > 0) {
            BitStore reader = new BitStore(save);
            try {
                savedLogs.deserialize(reader);
                for (int i = 0; i < logs.size(); i++) {
                    savedLogs.add(logs.get(i));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return savedLogs;
    }


    public static void chkPreferences() {
        if (mAndroidSetting == null && context != null) {
            mAndroidSetting = context.getSharedPreferences(Consts.PREFS_NAME, 0);
            mAndroidSettingEditor = mAndroidSetting.edit();
        }
    }


    public static void writeLogs(TextView tv) {
        if (tv != null) {

            tv.setText("");
            SerializableArrayList<LogItem> savedLogs = readLogs();
            ArrayList<LogItem> sortList = new ArrayList<>();

            for (int i = 0; i < savedLogs.size(); i++) {
                sortList.add(savedLogs.get(i));
            }

            Collections.sort(sortList);

            for (LogItem item : sortList) {
                tv.append(item.getSpannable());
            }
        }
    }
}
