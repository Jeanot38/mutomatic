package eu.telecom_bretagne.mutomatic.lib;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Vincent on 22/01/2015.
 */
public class AudioWrapper {
    AudioManager audioWrapper;
    int previousRingerMode;

    public AudioWrapper(Context c) {
        Context context = c;
        this.audioWrapper = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public int getCurrentSettings() {
        int currentSettings = audioWrapper.getRingerMode();
        return currentSettings;
    }

    public void setSettings(int i) {
        String settings;
        switch (i) {
            case 0:
                audioWrapper.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                settings = "Silencieux";
                break;
            case 1:
                audioWrapper.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                settings = "Vibreur";
                break;
            case 2:
                audioWrapper.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                settings = "Normal";
                break;
            default:
                settings = "Mode invalide";
                break;
        }
    }

    public void autoSettings() {
        this.previousRingerMode=audioWrapper.getRingerMode();
        audioWrapper.setRingerMode(AudioManager.RINGER_MODE_SILENT);   //Application du profil correspondant (silent par defaut)
    }

    public void restoreSettings(){
        audioWrapper.setRingerMode(this.previousRingerMode);
    }
}