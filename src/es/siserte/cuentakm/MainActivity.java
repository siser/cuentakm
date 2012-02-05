package es.siserte.cuentakm;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	private Button boton;
	private static Boolean estadoBoton = false; // inicialmente el estado es
	// "detenido"
	public static TextView distancia;
	public TextView ultimokm, actualkm;
	public LocationManager locman;
	public miLocationListener loclis;
	private static double metros;
	public AudioManager audioMan;
	private static Vibrator vibrator;

	private int volumenMax;
	public boolean airplaneModesEnabled;

	Chronometer crono;
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		boton = (Button) findViewById(R.id.bStart);
		distancia = (TextView) findViewById(R.id.rDist);
		// ultimokm = (TextView) findViewById(R.id.rVelUlt);
		// actualkm = (TextView) findViewById(R.id.rVel);

		/* Use the LocationManager class to obtain GPS locations */
		locman = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		loclis = new miLocationListener(getApplicationContext());

		/* establecemos el manejador del gps */
		locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 200, 1,
				loclis);

		/* Volumen multimedia al máximo */
		audioMan = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		volumenMax = audioMan.getStreamMaxVolume(3);
		audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, volumenMax,
				AudioManager.FLAG_SHOW_UI);
		// silenciamos el volumen del audio timbre
		audioMan.setStreamVolume(AudioManager.STREAM_RING, 0,
				AudioManager.FLAG_SHOW_UI);

		/* habilitamos el vibrador */
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		/* activamos el modo vuelo */
		/*
		 * airplaneModesEnabled = Settings.System.getInt(
		 * this.getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
		 * Settings.System.putInt( this.getContentResolver(),
		 * Settings.System.AIRPLANE_MODE_ON, airplaneModesEnabled ? 0 : 1);
		 * 
		 * Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		 * intent.putExtra("state", 1); this.sendBroadcast(intent);
		 */
		
		
		/*cronometro*/
		crono=(Chronometer) findViewById(R.id.chrono1);
		
		
		/* boton */
		boton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				vibrator.vibrate(250);
				// si el contador está detenido, se inicia y se resetean los
				// datos.
				if (estadoBoton == false) {
					estadoBoton = true;
					boton.setText(R.string.bFin);
					distancia.setText("0");
					metros = 0;
					crono.setBase(SystemClock.elapsedRealtime());
					crono.start();
				} else {
					estadoBoton = false;
					boton.setText(R.string.bIni);
					crono.stop();
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menuprincipal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.mAcerca:
			String aboutTxt = "(c) 2011 Sergio Iserte";
			Toast.makeText(MainActivity.this, aboutTxt, Toast.LENGTH_LONG)
					.show();
			return true;
		case R.id.mQuit:
			finish();
			return true;
		default:
			return false;
		}
	}

	public static void contador(Context context, double res) {
		MediaPlayer mp = MediaPlayer.create(context, R.raw.bell);
		try {
			mp.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (estadoBoton) {
			metros = metros + res;
			distancia.setText(String.valueOf((int) metros));
			Toast toast;
			// 10 metros antes de completar un km, emitirá una señal.
			int aviso = 10;
			int resto;

			for (int i = 0; i <= aviso; i++) {
				resto = ((int) metros + i) % 1000;
				if (resto == 0) {
					vibrator.vibrate(1000);
					mp.start();
					toast = Toast.makeText(context, (int) metros + i
							+ " metros", Toast.LENGTH_SHORT);
					toast.show();
					break;
				}
			}
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		//Log.d("estado", "onStop");
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// desactivamos el modo vuelo para dejar en normal funcionamiento el
		// dispositivo
		Settings.System.putInt(this.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, airplaneModesEnabled ? 1 : 0);
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", 1);
		this.sendBroadcast(intent);

		// bajamos el volumen del audio multimedia
		audioMan.setStreamVolume(AudioManager.STREAM_MUSIC, 4,
				AudioManager.FLAG_SHOW_UI);
		// eliminamos el manejador GPS para ahorrar batería
		locman.removeUpdates(loclis);
		// restauramos el volumen del audio timbre
		audioMan.setStreamVolume(AudioManager.STREAM_RING, 4,
				AudioManager.FLAG_SHOW_UI);
	}
}