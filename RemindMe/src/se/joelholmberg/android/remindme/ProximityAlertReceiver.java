package se.joelholmberg.android.remindme;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.location.LocationManager;
import android.widget.Toast;

public class ProximityAlertReceiver extends BroadcastReceiver {

	private NotesDbAdapter mNotesDbAdapter;
	//private final long mRowId;
	public ProximityAlertReceiver(){
		super();
		//mRowId = -1;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		final Context finalContext = context;
		mNotesDbAdapter = new NotesDbAdapter(context);
		mNotesDbAdapter.open();
		
		//TODO: Delete
		CharSequence text = "onRecieve i ProxAlertReciever!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(context, text, duration);
		toast.show();

		final long mRowId = intent.getLongExtra(SetPositionView.KEY_ROW_ID, -1l);
		if (mRowId == -1l || mRowId < 0) return;

		String enteringKey = LocationManager.KEY_PROXIMITY_ENTERING;
		boolean entering = intent.getBooleanExtra(enteringKey, false);

		//Get data from database
		Cursor note = mNotesDbAdapter.fetchNote(mRowId);
		String title = note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE));
		String body= note.getString(note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY));


		if (entering){
			//Handle events when entering the zone

			//For using an Alarm Dialogue
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);

			// set title
			alertDialogBuilder.setTitle(title);

			// set dialog message
			alertDialogBuilder
			.setMessage(body)
			.setCancelable(false)
			.setPositiveButton("Snooze",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, do nothing (snooze)
					dialog.cancel();
				}
			})
			.setNegativeButton("Remove reminder",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					// if this button is clicked, remove reminder from database
					mNotesDbAdapter.open();
					mNotesDbAdapter.deleteNote(mRowId);
					if (((Activity) finalContext).isChild()){
						((Activity) finalContext).getParent().finishFromChild(((Activity) finalContext));

					}
					dialog.cancel();
				}
			});

			// create alert dialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// show it
			alertDialog.show();
		} else {
			//Handle events when leaving the zone
			Toast.makeText(context, (title + ": \n" + body), Toast.LENGTH_LONG).show();
		}
		//Close db connection
		mNotesDbAdapter.close();

	}





}
