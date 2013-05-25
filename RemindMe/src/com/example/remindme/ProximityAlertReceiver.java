package com.example.remindme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProximityAlertReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Long rowId = intent.getLongExtra(PositionService.KEY_ROW_ID, -1l);
		if (rowId == -1l || rowId < 0) return;
		context.startActivity(intent);
	}

}
