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

import java.io.IOException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import org.androidcare.android.reminders.*;

/**
 * Alarm receiver handles the events related to alerts/alarms 
 * @author Alejandro Escario MŽndez
 */
public class ReminderReceiver extends BroadcastReceiver {
	 
	/**
	 * On alarm receive handler
	 */
	 @Override
	 public void onReceive(Context context, Intent intent) {
	   try {
		   //1 - extracting the data from the 'messenger'
		   Bundle bundle = intent.getExtras();
		   Reminder r = (Reminder)bundle.getSerializable("reminder");
		   //2 - display the detail dialog 
		   displayDialog(context, r);
		   //3 - getting user's attention
		   playSound(context);
		   
		   Log.i("AlarmReceiver", r.toString());
	    } catch (Exception e) { // we must catch the exception
	    	Log.i("AlarmReceiver", "There was an error somewhere, but we still received an alarm");
	    	e.printStackTrace();
    	}
	 }

	 private void displayDialog(Context ctx, Reminder a) {
		//1 - setting up the intent
		Intent intent = new Intent("android.intent.action.MAIN");
		intent.setClass(ctx, ReminderDialogReceiver.class);
		intent.putExtra("reminder", a);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//2 - displaying the activity
		ctx.startActivity(intent);    
	 }	

	 public void playSound(Context context) throws IllegalArgumentException, SecurityException, IllegalStateException,
	    											IOException {
		//1 - getting the sound 
		Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		MediaPlayer mMediaPlayer = new MediaPlayer();
		mMediaPlayer.setDataSource(context, soundUri);
		//2 - setting up the audio manager
		final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		//3 - playing the sound
		if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
		    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
		    mMediaPlayer.setLooping(false);
		    mMediaPlayer.prepare();
		    mMediaPlayer.start();
		}
	}
}
