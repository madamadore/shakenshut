package it.matteoavanzini.android.muteonshake;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class MuteOnShake extends BroadcastReceiver
{
	private final static String LOG_TAG = "MUTE_ON_SHAKE";
	
	private static Intent serviceIntent;
	private Context context;
	@Override
	public void onReceive(Context context, Intent intent)
	{
			this.context = context;
			serviceIntent = new Intent(context, ShakeService.class);
			TelephonyManager telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
			PhoneStateListener phoneListener = new PhoneStateListener() {
					public void onCallStateChanged(int state, String incomingNumber)
				  {
					  switch(state)
					  {
					    case TelephonyManager.CALL_STATE_IDLE:
					    	// riposo
					      stopLocalService();
					      break;
					    case TelephonyManager.CALL_STATE_RINGING:
					    	// squillando
					    	startLocalService();
					    	break;
					    case TelephonyManager.CALL_STATE_OFFHOOK:
					    	// in corso
					    	stopLocalService();
					    	break;
				    }
				  }
			};
			telephony.listen(phoneListener,PhoneStateListener.LISTEN_CALL_STATE);
	}
	
	public void startLocalService() {
		context.startService(serviceIntent);
	}
	
	public void stopLocalService() {
		context.stopService(serviceIntent);
	}
}