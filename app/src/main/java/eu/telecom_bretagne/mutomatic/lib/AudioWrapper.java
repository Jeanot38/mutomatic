package eu.telecom_bretagne.mutomatic.lib;

import android.content.Context;
import android.media.AudioManager;

/**
 * Created by Vincent on 22/01/2015.
 */
public class AudioWrapper {

    private AudioManager audioWrapper;
    private Integer previousRingerMode = null;

    public AudioWrapper(Context context) {
        this.audioWrapper = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    public int getCurrentSettings() {
        return audioWrapper.getRingerMode();
    }

    public void setPreviousRingerMode(Integer previousRingerMode) {
        this.previousRingerMode = previousRingerMode;
    }

    public void autoSettings() {
        this.setPreviousRingerMode(audioWrapper.getRingerMode());
        audioWrapper.setRingerMode(AudioManager.RINGER_MODE_SILENT);   //Application du profil correspondant (silent par defaut)
    }

    public void restoreSettings(){
        audioWrapper.setRingerMode(this.previousRingerMode);

        this.setPreviousRingerMode(null);
    }
}