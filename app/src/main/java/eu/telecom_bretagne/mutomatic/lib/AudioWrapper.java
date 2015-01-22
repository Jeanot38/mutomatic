package eu.telecom_bretagne.mutomatic.lib;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Vincent on 22/01/2015.
 */
public class AudioWrapper {
    private Context context;
    AudioManager audioSetting;

    public AudioWrapper(Context c){
        this.context=c;
        this.audioSetting=(AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public int getCurrentSettings () {
        int currentSettings =audioSetting.getRingerMode();
        return currentSettings;
    }

    public void setSettings (int i){
        String settings;
        switch (i){
            case 0: audioSetting.setRingerMode(AudioManager.RINGER_MODE_SILENT);
                settings="Silencieux";
                break;
            case 1: audioSetting.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                settings="Vibreur";
                break;
            case 2: audioSetting.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                settings="Normal";
                break;
            default:settings="Mode invalide";
                break;
        }
    }
}