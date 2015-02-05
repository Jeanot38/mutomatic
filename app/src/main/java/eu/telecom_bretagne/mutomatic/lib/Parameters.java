package eu.telecom_bretagne.mutomatic.lib;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Switch;

import java.util.HashSet;
import java.util.Set;

/**
 * Provide methods which allow to get and set parameters of Mut'oMatic application.
 * @author Mathis GAVILLON
 */
public abstract class Parameters {

    public static final String APPLICATION_ENABLED = "eu.telecom_bretagne.mutomatic.application_enabled";
    public static final String SCHEDULING_INTERVAL = "eu.telecom_bretagne.mutomatic.scheduling_interval";
    public static final String PROFILE_SELECTED = "eu.telecom_bretagne.mutomatic.profile_selected";


    private static SharedPreferences sharedPreferences;

    public static void configurePreferences(Context context) {
        sharedPreferences = context.getSharedPreferences("mutomatic", Context.MODE_PRIVATE);
    }

    public static Boolean getBooleanPreference(String key) {
        if(sharedPreferences != null) {
            Integer preference = getIntPreference(key);
            if(preference != null) {
                if (preference == 1) {
                    return true;
                } else if (preference == 0) {
                    return false;
                }
            }
        }

        return null;
    }

    public static Integer getIntPreference(String key) {
        if(sharedPreferences != null) {
            int pref = sharedPreferences.getInt(key, -1);
            if(pref == -1) {
                return null;
            }

            return pref;
        }

        return null;
    }

    public static String getStringPreference(String key) {
        if(sharedPreferences != null) {
            return sharedPreferences.getString(key, "notDefined");
        }

        return null;
    }

    public static Set<String> getStringPreferenceSet(String key) {
        if(sharedPreferences != null) {
            return sharedPreferences.getStringSet(key, new HashSet<String>());
        }

        return null;
    }

    public static Set<Integer> getIntPreferenceSet(String key) {

        if(sharedPreferences != null) {
            Set <String> stringPreferenceSet = sharedPreferences.getStringSet(key, new HashSet<String>());
            Set <Integer> intPreferenceSet = new HashSet<>();
            for(String stringPreference : stringPreferenceSet) {
                intPreferenceSet.add(Integer.parseInt(stringPreference));
            }

            return intPreferenceSet;
        }

        return null;
    }

    public static Boolean setPreference(String key, int value) {

        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(key, value);
            return editor.commit();
        }

        return null;
    }

    public static Boolean setPreference(String key, String value) {

        if(sharedPreferences != null) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(key, value);
            return editor.commit();
        }

        return null;
    }

    public static Boolean setPreference(String key, String [] values) {

        if(sharedPreferences != null) {
            HashSet<String> listOfValues = new HashSet<>();
            for(String value : values) {
                listOfValues.add(value);
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(key, listOfValues);
            return editor.commit();
        }

        return null;
    }

    public static Boolean setPreference(String key, Integer [] values) {

        if(sharedPreferences != null) {
            HashSet<String> listOfValues = new HashSet<>();
            for(Integer value : values) {
                listOfValues.add(Integer.toString(value));
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putStringSet(key, listOfValues);
            return editor.commit();
        }

        return null;
    }

    public static Boolean setPreference(String key, Boolean value) {

        if(sharedPreferences != null) {

            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(value == true) {
                editor.putInt(key, 1);
            } else {
                editor.putInt(key, 0);
            }

            return editor.commit();
        }

        return null;
    }
}
