package com.example.zeppelin;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class RestartDialog extends DialogFragment {
	private View root;
	private TextView txt_counter;
	private AlertDialog dialog;
	private Context context;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		LayoutInflater inflater = getActivity().getLayoutInflater();
		root = inflater.inflate(R.layout.layout_restart_dialog, null);
		
		final AlertDialog dialog = new AlertDialog.Builder(getActivity())
		.setTitle("Kein Controller gefunden!")
		.setView(root)
		.setPositiveButton("Jetzt neustarten",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						restart();
					}
				}).create();

		this.dialog = dialog;
		txt_counter = (TextView)root.findViewById(R.id.restart_counter);
		dialog.setCanceledOnTouchOutside(false);
		new CounterThread().start();
		return dialog;
	}

	private void restart(){
		Intent mStartActivity = new Intent(root.getContext(), MainActivity.class);
		int mPendingIntentId = 123456;
		PendingIntent mPendingIntent = PendingIntent.getActivity(root.getContext(), mPendingIntentId,    mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager mgr = (AlarmManager)root.getContext().getSystemService(Context.ALARM_SERVICE);
		mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, mPendingIntent);
		System.exit(0);
	}
	
	private class CounterThread extends Thread{
		int cnt = 10;
		public void run(){
			while(cnt > 0){

				getActivity().runOnUiThread(new Runnable(){

					@Override
					public void run() {
						txt_counter.setText(cnt+"");
						Log.e("Countdown", cnt + "");
						cnt--;
					}});
				try{
					Thread.sleep(1000);
				} catch(Exception e){
				}
			}
			
			restart();
		}
	}
	
	public void setContext(Context context){
		this.context = context;
	}
}
