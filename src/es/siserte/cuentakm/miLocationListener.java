package es.siserte.cuentakm;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class miLocationListener implements LocationListener{
	private Context context;
	private Boolean firstLoc = true;
	private Location locAnt;
	public miLocationListener(Context context) {
		this.context = context;
		Log.d("listener", "constructor");
	}
	
	@Override
	public void onLocationChanged(Location location) {
		double res;
		Log.d("listener","onLocationChanged");
		if (firstLoc == true){
			firstLoc = false; 
		}
		else{
			Log.d("listener","lat:" + location.getLatitude());
			Log.d("listener","latAnt:" + locAnt.getLatitude());
			res =locAnt.distanceTo(location);
			//res = MainActivity.calculator(locAnt, location);
			MainActivity.contador(context, res);
		}
		locAnt=location;
	}

	@Override
	public void onProviderDisabled(String provider) {
		Toast.makeText(context, "GPS desactivado", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onProviderEnabled(String provider) {
		Toast.makeText(context, "GPS activado", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub	
	}
}
