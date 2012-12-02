/** 
 * This library/program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either 
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library/program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public 
 * License along with this library/program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */

package org.androidcare.android.view;

import org.androidcare.android.reminders.Reminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

/**
 * Handles the events related to Reminders
 * 
 * @author Alejandro Escario MŽndez
 */
public class ReminderReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Reminder reminder = (Reminder) bundle.getSerializable("reminder");
        displayDialog(context, reminder);
        playSound(context);
        // @comentario @todo que el teléfono vibre
        Log.i("ReminderReceiver", reminder.toString());
    }

    private void displayDialog(Context ctx, Reminder reminder) {
        // 1 - setting up the intent
        Intent intent = new Intent("android.intent.action.MAIN");
        intent.setClass(ctx, ReminderDialogReceiver.class);
        intent.putExtra("reminder", reminder);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // 2 - displaying the activity
        ctx.startActivity(intent);
    }

    // @comentario mejor gestionar aquí la excepción, sobre todo cuando es una excepción respecto a la
    // que no vamos a hacer nada, en vez de gestionarla en onReceive, como tu la tenías
    public void playSound(Context context) {
        
      //@Comentario ¿todo esto de hacer solitos no debería ir en un thread diferente del main?
        try {
            // 1 - getting the sound
            Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            MediaPlayer mMediaPlayer = new MediaPlayer();
            mMediaPlayer.setDataSource(context, soundUri);
            // 2 - setting up the audio manager
            final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
            // 3 - playing the sound
            if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
                mMediaPlayer.setLooping(false);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        }
        catch (Exception e) { // we must catch the exception
            Log.i("ReminderReceiver", "Could not play the sound when reminder was received");
            e.printStackTrace();
        }
    }
}
