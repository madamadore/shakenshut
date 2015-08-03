package it.matteoavanzini.android.muteonshake;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.os.IBinder;

/**
 * Esempio di un Service locale che permette di inviare delle notifiche
 * ad intervalli casuali di tempo
 *  
 * @author Matteo
 *
 */
public class ShakeService extends Service 
{
	/*
	 * Tag del Log
	 */
	private final static String LOG_TAG = "SHAKE_LISTENER";
	private static BackgroundThread backgroundThread;
	
	private AudioManager audioManager;
	private SensorManager sensorManager;
	
	private Sensor mAccelerometer;
	
	private int ringerMode;
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    if (sensorManager == null) {
      throw new UnsupportedOperationException("Sensors not supported");
    }
    mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    ringerMode = audioManager.getRingerMode();
    
		// Facciamo partire il BackgroundThread
		backgroundThread = new BackgroundThread();
		backgroundThread.start();
	}
	
	@Override
	public void onDestroy() {
		audioManager.setRingerMode(ringerMode);
		backgroundThread.running = false;
		super.onDestroy();
	}

	/* (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
	@Override
	public IBinder onBind(Intent arg0) {
		// Ritorniamo null in quanto non si vuole permettere
		// l'accesso al servizio da una applicazione diversa
		return null;
	}
	
	private final class BackgroundThread extends Thread {

		private static final long DELAY = 500L;
		public boolean running = true;
		
		public void run() 
		{
			ShakeListener shakeListener = new ShakeListener() {
				@Override
				public void onShake()
				{
					audioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
				}
			};
			boolean supported = sensorManager.registerListener(shakeListener, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
	    if (!supported) {
	    	sensorManager.unregisterListener(shakeListener, mAccelerometer);
	    	running = false;
	      throw new UnsupportedOperationException("Accelerometer not supported");
	    }
			
			while(running) {
				try
				{
					Thread.sleep(DELAY);
				}
				catch (InterruptedException e)
				{
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			sensorManager.unregisterListener(shakeListener, mAccelerometer);
			// Al termine del metodo run terminiamo il servizio
			stopSelf();
		}

	}
	

}
