package comsri.gps_buzz;

import android.content.Context;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONObject;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;


public class Choices extends Activity {

	private GoogleMap googleMap;
	ArrayList<LatLng> markerPoints=new ArrayList<LatLng>();
	public String source;
	public String dest;
	public int count;
	public int numb;
	public LatLng origin,destination,newpoint;
	public Context context;
	public String[] modetypes={"driving","transit","bicycling","walking"};
	public static Marker newmarker;
	public static MarkerOptions newone=new MarkerOptions();
	ShowcaseView show=null;
	PointTarget target1,target2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_choices);
		context=this;

		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		final DBAdapter db = new DBAdapter(this);
		db.open();

		if(db.tutorialcheck(3))
		{
			target1 = new PointTarget(width / 2, (height / 2) - 5);
			target2 = new PointTarget(width / 2, height / 2);


			show = new ShowcaseView.Builder(this)
					.setTarget(target2)
					.setContentText("to display the location name")
					.setContentTitle("CLICK ON THE MARKER")
					.setOnClickListener(new View.OnClickListener() {

						int counter = 0;

						@Override
						public void onClick(View v) {

							switch (counter) {
								case 0:
									show.setShowcase(target1, true);
									show.setContentText("to set the alarm. Give it a name and add notes");
									show.setContentTitle("CLICK ON THE TITLE");
									counter++;
									break;
								case 1:
									show.setVisibility(View.GONE);
									db.settutorial(3,0);
									break;

							}
						}
					})
					.setStyle(R.style.CustomShowcaseTheme3)
					.build();
		}

		Intent intent=getIntent();
		Bundle bundle=intent.getParcelableExtra("bundle");

			Toast.makeText(this, "Click on a marker's name to set an alarm", Toast.LENGTH_LONG).show();

			origin = bundle.getParcelable("from");
			destination = bundle.getParcelable("to");

			numb = intent.getIntExtra("numb", 0);
			count = intent.getIntExtra("count", 0);
			source = intent.getStringExtra("source");
			dest = intent.getStringExtra("dest");

			try {
				// Loading map
				initilizeMap();

				markerPoints.add(origin);
				markerPoints.add(destination);

				CameraPosition cameraPosition = new CameraPosition.Builder().target(origin).zoom(14).build();
				googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

				MarkerOptions options = new MarkerOptions();

				// Setting the position of the marker
				options.position(origin);
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
				options.title(source.split(",")[0]);
				Choices.this.googleMap.addMarker(options);

				options.position(destination);
				options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				options.title(dest.split(",")[0]);
				Choices.this.googleMap.addMarker(options);


				String url = Choices.this.getDirectionsUrl(origin, destination);

				DownloadTask downloadTask = new DownloadTask();

				// Start downloading json data from Google Directions API
				downloadTask.execute(url);


			} catch (Exception e) {
				e.printStackTrace();
			}


	}

	private String getDirectionsUrl(LatLng origin, LatLng dest)
	{
		// Origin of route
		String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

		// Destination of route
		String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

		String mode="mode="+modetypes[numb-1];

		String alternatives="alternatives="+"true";
		// Sensor enabled
		String sensor = "sensor=true";

		// Building the parameters to the web service
		String parameters = str_origin + "&" + str_dest + "&" + mode+"&"+sensor+"&"+alternatives;

		// Output format
		String output = "json";

		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

		return url;
	}



	private String downloadUrl(String strUrl) throws IOException
	{
		String data = "";
		InputStream iStream = null;
		HttpURLConnection urlConnection = null;
		try
		{
			URL url = new URL(strUrl);

			// Creating an http connection to communicate with url
			urlConnection = (HttpURLConnection) url.openConnection();

			// Connecting to url
			urlConnection.connect();

			// Reading data from url
			iStream = urlConnection.getInputStream();

			BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

			StringBuffer sb = new StringBuffer();

			String line = "";
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
			}

			data = sb.toString();

			br.close();

		} catch (Exception e)
		{
			Log.d("downloading failed", e.toString());
		} finally
		{
			iStream.close();
			urlConnection.disconnect();
		}
		return data;
	}


	private class DownloadTask extends AsyncTask<String, Void, String>{

		// Downloading data in non-ui thread
		@Override
		protected String doInBackground(String... url) {

			// For storing data from web service
			String data = "";

			try{
				// Fetching the data from web service
				data = downloadUrl(url[0]);
			}catch(Exception e){
				Log.d("Background Task",e.toString());
			}
			return data;
		}

		// Executes in UI thread, after the execution of
		// doInBackground()
		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);

			ParserTask parserTask = new ParserTask();

			// Invokes the thread for parsing the JSON data
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, mode >{

		// Parsing the data in non-ui thread
		@Override
		protected mode doInBackground(String... jsonData) {

			JSONObject jObject;
			List<List<HashMap<String, String>>> routes = null;
			ArrayList<String> travelmode;
			mode a=null;

			try{
				jObject = new JSONObject(jsonData[0]);
				DirectionsJSONParser parser = new DirectionsJSONParser();

				// Starts parsing data
				a=parser.parse(jObject);
				routes = a.routes;
				travelmode = a.travelmode;

			}catch(Exception e){
				e.printStackTrace();
			}
			return a;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(mode result) {
			ArrayList<LatLng> points = null;
			ArrayList<ArrayList<LatLng>>pointsList=new ArrayList<ArrayList<LatLng>>();
			PolylineOptions lineOptions=null;
			int pointlistsize=0;

			MarkerOptions markerOptions = new MarkerOptions();
			String distance = "";
			String duration = "";

			int type=100,initialtype=100,beginningtype=0;
			if(result.routes.size()<1){
				Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
				return;
			}

			// Traversing through all the routes
		//	for(int i=0;i<result.routes.size();i++){

				points = new ArrayList<LatLng>();
				lineOptions = new PolylineOptions();
				LatLng position=null;

				// Fetching i-th route
				List<HashMap<String, String>> path = result.routes.get(count-1);

				// Fetching all the points in i-th route
				for(int j=0;j<path.size();j++){

					if(j>2) {
						if (type == initialtype)
							points.add(position);
						else {
							initialtype = type;
							pointsList.add(points);
							points = new ArrayList<LatLng>();
						}
					}

					HashMap<String,String> point = path.get(j);

					if(j==0){    // Get distance from the list
						distance = (String)point.get("distance");
						continue;
					}else if(j==1){ // Get duration from the list
						duration = (String)point.get("duration");
						continue;
					}

					double lat = Double.parseDouble(point.get("lat"));
					double lng = Double.parseDouble(point.get("lng"));
					type=Integer.parseInt(point.get("type"));
					if(j==2) {
						initialtype = type;
						beginningtype=type;
					}
					position = new LatLng(lat, lng);

				}

				pointsList.add(points);
				// Adding all the points in the route to LineOptions

				int[] colorlist={Color.GRAY,Color.BLUE,Color.GRAY};
				for(int x=0;x<pointsList.size();x++) {
					lineOptions=new PolylineOptions();
					lineOptions.width(6);
					lineOptions.addAll(pointsList.get(x));
					if(beginningtype==1)
					lineOptions.color(colorlist[x%(2)]);
					else
						lineOptions.color(colorlist[x%(2)+1]);

					googleMap.addPolyline(lineOptions);

				}

		//	}


			// Drawing polyline in the Google Map for the i-th route

		}
	}



	private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById( R.id.map)).getMap();
            // check if map is created successfully or not
           
             googleMap.setOnInfoWindowClickListener(
            		  new GoogleMap.OnInfoWindowClickListener(){
            		    public void onInfoWindowClick(Marker marker){
            		     
            		  	AlarmDialog e=new AlarmDialog(marker.getTitle(),marker.getPosition(),getBaseContext());
                    		e.show(getFragmentManager(), ALARM_SERVICE);
            		    }
            		    });
			googleMap.setMyLocationEnabled(true);
			googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
				@Override
				public void onMapLongClick(LatLng latLng) {

					if(newmarker!=null)
						newmarker.remove();
					newone.position(latLng);
					newone.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

					try {
						Geocoder geoCoder = new Geocoder(Choices.this);

						try {

							//Place your latitude and longitude
							List<Address> addresses = geoCoder.getFromLocation(latLng.latitude,latLng.longitude, 1);

							if(addresses != null) {

								Address fetchedAddress = addresses.get(0);
								StringBuilder strAddress = new StringBuilder();

								for(int i=0; i<fetchedAddress.getMaxAddressLineIndex(); i++) {
									strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
								}

								newone.title(strAddress.toString());

							} else
								newone.title("New Location");

						}
						catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Toast.makeText(getApplicationContext(),"Could not get address..!", Toast.LENGTH_LONG).show();
						}

					} catch (Exception e) {
					}


					newmarker=Choices.this.googleMap.addMarker(newone);


				}
			});
            
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.changering) {

			Intent intent1 = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
			intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
			intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
			this.startActivityForResult(intent1,5);

		}
		else if (id==R.id.viewalarm)
		{
			Intent intent=new Intent(this,AlarmActivity.class);
			intent.putExtra("view",1);
			startActivity(intent);
			return true;
		}
		else if (id==R.id.tutorial)
		{
			DBAdapter db = new DBAdapter(this);
			db.open();
			db.settutorial(3,1);

			Intent intent=new Intent(this,Choices.class);

			Bundle args = new Bundle();
			args.putParcelable("from", origin);
			args.putParcelable("to", destination);
			args.putInt("notif",0);
			intent.putExtra("bundle",args);
			intent.putExtra("numb",numb);
			intent.putExtra("count", count);
			intent.putExtra("source", source);
			intent.putExtra("dest", dest);
			finish();
			startActivity(intent);

		}

		return super.onOptionsItemSelected(item);
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.choices, menu);
		return true;
	}

}
