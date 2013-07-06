package se.joelholmberg.android.remindme;

import java.io.IOException;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class ReminderActivity extends Activity {

	protected static Vibrator vibrator;
	protected MediaPlayer mMediaPlayer;
	protected TextView reminderText;
	protected Button confirmButton, snoozeButton;
	protected boolean confirmed = false;
	protected boolean isEntered;
	protected Long mRowId;
	private NotesDbAdapter mDbAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_reminder);
		
		LayoutParams params = getWindow().getAttributes(); 
		params.height = LayoutParams.MATCH_PARENT;
		params.width  = LayoutParams.MATCH_PARENT;
		getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

		isEntered = getIntent().getExtras().getBoolean(LocationManager.KEY_PROXIMITY_ENTERING);
		mDbAdapter = new NotesDbAdapter(this);
		mDbAdapter.open();
		
		//Get current reminder object (by rowID) if already existing
		if (savedInstanceState != null){
			mRowId = (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ID);
		}
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ID) : null;
		}
		
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		reminderText = (TextView) findViewById(R.id.reminderText);
		snoozeButton = (Button) findViewById(R.id.snoozeButton);
		confirmButton = (Button) findViewById(R.id.confirmButton);

		confirmButton.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				confirmed = true;
				mDbAdapter.deleteNote(mRowId);
				NavUtils.navigateUpFromSameTask(ReminderActivity.this);
			}
		});
		
//		while (!confirmed){
			try {
				vibrate(2);
				playSound(this, getAlarmUri(), 5000L);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		}
		

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//getMenuInflater().inflate(R.menu.activity_reminder, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Log.e("RemindMe", "Pressed home button");
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void playSound(final Context context, final int resource_id){
		new Thread() {
			@Override
			public void run(){
				mMediaPlayer = MediaPlayer.create(ReminderActivity.this, resource_id);

				final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
				if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
					mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
					mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
						public void onCompletion(MediaPlayer mp) {
							mMediaPlayer.stop();
							mMediaPlayer.release();
						}
					});
					mMediaPlayer.start();
				}
			}
		}.start();

	}
	
	private void playSound(final Context context, final Uri alert, final Long milliseconds) throws InterruptedException {
		Runnable vibrateRunnable = new Runnable(){
			public void run(){
				mMediaPlayer = new MediaPlayer();
				try {
					mMediaPlayer.setDataSource(context, alert);
					final AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
					if (audioManager.getStreamVolume(AudioManager.STREAM_ALARM) != 0) {
						mMediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
						mMediaPlayer.prepare();
						mMediaPlayer.start();
						Thread.sleep(milliseconds);
						mMediaPlayer.stop();
						mMediaPlayer.release();
					}
				} catch (IOException e) {
					System.out.println("OOPS");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		Thread vibrateThread = new Thread(vibrateRunnable);
		vibrateThread.start();
		
	}

//	private void setAlarm(int seconds){
//		if (seconds == -1) {return;}
//		Intent intent = getIntent();
//		//Intent myIntent = new Intent(getParent(), getClass());
//		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
//		AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);
//		Calendar calendar = Calendar.getInstance();
//		calendar.setTimeInMillis(System.currentTimeMillis());
//		calendar.add(Calendar.SECOND, seconds);
//		alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
//	}
	
	private Uri getAlarmUri() {
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) {
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		return alert;
		
	}
		


	public static void vibrate(final int sec){
		Runnable vibrateRunnable = new Runnable(){
			public void run(){
				ReminderActivity.vibrator.vibrate(sec*1000);
			}
		};
		Thread vibrateThread = new Thread(vibrateRunnable);
		vibrateThread.start();
	}
}
