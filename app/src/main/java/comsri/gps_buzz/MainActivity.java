package comsri.gps_buzz;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.annotation.Target;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import cardsui.*;
public class MainActivity extends Activity {

	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String OUT_JSON = "/json";
	public static int PLACE_PICKER_REQUEST = 1;

	//------------ make your specific key ------------
	private static final String API_KEY = "AIzaSyDQMICQufKQ3qBghQ9mD6_lIByhUu8rxhU";

	public  String source;
	public  String dest;
	public static String card1info="";
	public static String card2info="";

	int timerflag1=0,timerflag2=0;

	public static Context context;
	public static CardUI mCardView;

	public int which=1;
	public PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();

	public AutoCompleteTextView t1;
	public AutoCompleteTextView t2;

	public TextView desc1;
	public TextView desc2;

	ImageView stripe;
	ImageView stripe2;
	AutoCompleteTextView title ;
	AutoCompleteTextView title2 ;
	ImageView gps1;
	ImageView gps2;
	Button button1;
	RelativeLayout relmain ;

	public ShowcaseView show;
	private static ViewTarget target1,target2,target3,target4,target5;
	private ActionViewTarget target6;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		AdView mAdView = (AdView) findViewById(R.id.adView);
		AdRequest adRequest = new AdRequest.Builder().build();
		mAdView.loadAd(adRequest);

		context=this;

		final ImageView stripe = (ImageView) findViewById(R.id.stripe);
		final ImageView stripe2 = (ImageView) findViewById(R.id.stripe2);
		final AutoCompleteTextView title = (AutoCompleteTextView) findViewById(R.id.title);
		final AutoCompleteTextView title2 = (AutoCompleteTextView) findViewById(R.id.title2);
		final ImageView gps1 = (ImageView) findViewById(R.id.overflow);
		final ImageView gps2 = (ImageView) findViewById(R.id.overflow2);
		final Button button1 = (Button) findViewById(R.id.button1);

		final RelativeLayout relmain = (RelativeLayout) findViewById(R.id.relmain);

		final DBAdapter db = new DBAdapter(this);
		try {
			String destPath = "/data/data/" + getPackageName() +
					"/databases";
			File f = new File(destPath);
			if (!f.exists()) {

				f.mkdirs();
				f.createNewFile();
//---copy the db from the assets folder into
// the databases folder---
				CopyDB(getBaseContext().getAssets().open("App"),
						new FileOutputStream(destPath + "/App"));

			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		db.open();


		if(db.tutorialcheck(1))
		{

			relmain.setGravity(Gravity.NO_GRAVITY);

			target1 = new ViewTarget(R.id.title, this);
			target3 = new ViewTarget(R.id.title2, this);

			target2 = new ViewTarget(R.id.overflow, this);
			target4 = new ViewTarget(R.id.overflow2, this);

			target5 = new ViewTarget(R.id.button1, this);
			target6 = new ActionViewTarget(this, ActionViewTarget.Type.OVERFLOW);


			show = new ShowcaseView.Builder(this)
					.setTarget(target1)
					.setContentText("and choose from the dropdown box")
					.setContentTitle("ENTER YOUR STARTING POINT")
					.setOnClickListener(new View.OnClickListener() {

						int counter = 0;

						@Override
						public void onClick(View v) {

							switch (counter) {
								case 0:
									show.setShowcase(target2, true);
									show.setContentText("from the map");
									show.setContentTitle("OR PICK A NEARBY LOCATION");
									stripe.setAlpha((float) 0.75);
									title.setAlpha((float) 0.75);
									stripe2.setAlpha((float) 0.75);
									title2.setAlpha((float) 0.75);
									button1.setAlpha((float) 0.75);
									gps2.setAlpha((float) 0.75);
									gps1.setAlpha((float) 1);
									counter++;
									break;
								case 1:
									show.setShowcase(target3, true);
									show.setContentText("choose from the dropdown box");
									show.setContentTitle("ENTER YOUR DESTINATION");
									stripe.setAlpha((float) 0.75);
									title.setAlpha((float) 0.75);
									stripe2.setAlpha((float) 1);
									title2.setAlpha((float) 1);
									button1.setAlpha((float) 0.75);
									gps2.setAlpha((float) 0.75);
									gps1.setAlpha((float) 0.75);
									counter++;
									break;
								case 2:
									show.setShowcase(target4, true);
									show.setContentText("from the map");
									show.setContentTitle("OR PICK A NEARBY LOCATION");
									stripe.setAlpha((float) 0.75);
									title.setAlpha((float) 0.75);
									stripe2.setAlpha((float) 0.75);
									title2.setAlpha((float) 0.75);
									button1.setAlpha((float) 0.75);
									gps2.setAlpha((float) 1);
									gps1.setAlpha((float) 0.75);
									counter++;
									break;
								case 3:
									show.setShowcase(target6, true);
									show.setContentText("to see alarms and change your ringtone");
									show.setContentTitle("CLICK THE MENU");
									stripe.setAlpha((float) 0.75);
									title.setAlpha((float) 0.75);
									stripe2.setAlpha((float) 0.75);
									title2.setAlpha((float) 0.75);
									button1.setAlpha((float) 0.75);
									gps2.setAlpha((float) 0.75);
									gps1.setAlpha((float) .75);
									counter++;
									break;
								case 4:
									show.setShowcase(target5, true);
									show.setContentText("get going!");
									show.setContentTitle("ONCE YOU ARE DONE");
									stripe.setAlpha((float) 0.75);
									title.setAlpha((float) 0.75);
									stripe2.setAlpha((float) 0.75);
									title2.setAlpha((float) 0.75);
									button1.setAlpha((float) 1);
									gps2.setAlpha((float) 0.75);
									gps1.setAlpha((float) 0.75);
									counter++;
									break;
								case 5:
									show.setVisibility(View.GONE);
									show.setShowcase(target1, true);
									show.setContentText("and choose from the dropdown box");
									show.setContentTitle("ENTER YOUR STARTING POINT");
									stripe.setAlpha((float) 1);
									title.setAlpha((float) 1);
									stripe2.setAlpha((float) 1);
									title2.setAlpha((float) 1);
									button1.setAlpha((float) 1);
									gps2.setAlpha((float) 1);
									gps1.setAlpha((float) 1);
									relmain.setGravity(Gravity.CENTER);
									counter=0;
									db.settutorial(1,0);
									break;

							}
						}
					})
					.setStyle(R.style.CustomShowcaseTheme3)
					.build();
		}


		t1= (AutoCompleteTextView) findViewById(R.id.title);
		t2= (AutoCompleteTextView) findViewById(R.id.title2);

		desc1 = (TextView) findViewById(R.id.description);
		desc2 = (TextView) findViewById(R.id.description2);

		t1.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.my_list_view2));
		t1.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				String str = (String) adapterView.getItemAtPosition(position);
					source=str;
				String[] s = str.split(",");
				t1.setText(s[0]);
				desc1.setText("");
				for (int i = 1; i < s.length - 1; i++)
				{
					desc1.append(s[i] + ",");
				}

				desc1.append(s[s.length - 1]);


			}
		});


		t2.setAdapter(new GooglePlacesAutocompleteAdapter(this, R.layout.my_list_view));
		t2.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
				String str = (String) adapterView.getItemAtPosition(position);
				dest=str;
				String[] s = str.split(",");
				t2.setText(s[0]);
				desc2.setText("");
				for (int i = 1; i < s.length - 1; i++)
				{
					desc2.append(s[i] + ",");
				}

				desc2.append(s[s.length - 1]);


			}
		});

	}

	public void CopyDB(InputStream inputStream,
					   OutputStream outputStream) throws IOException {
//---copy 1K bytes at a time---
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) > 0) {
			outputStream.write(buffer, 0, length);
		}
		inputStream.close();
		outputStream.close();
	}


	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == PLACE_PICKER_REQUEST) {
			if (resultCode == RESULT_OK) {
				Place place = PlacePicker.getPlace(data, this);

				if(which==1)
				{
					source=place.getAddress().toString();
					t1.setText(place.getName());
					desc1.setText("");
					String[] s=place.getAddress().toString().split(",");
					for(int i=1;i<s.length-1;i++)
						desc1.append(s[i]+",");

					desc1.append(s[s.length-1]);
				}

				if(which==2)
				{
					dest=place.getAddress().toString();
					t2.setText(place.getName());
					desc2.setText("");
					String[] s=place.getAddress().toString().split(",");
					for(int i=1;i<s.length-1;i++)
						desc2.append(s[i]+",");

					desc2.append(s[s.length-1]);
				}
			}
		}
		switch (requestCode) {
			case RingtoneManager.TYPE_ALL:
				if (resultCode == RESULT_OK) {
					Uri notifToneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
					RingtoneManager.setActualDefaultRingtoneUri(this,RingtoneManager.TYPE_ALL,notifToneUri);
				}
				break;
		}

	}

	public static ArrayList<String> autocomplete(String input) {
		ArrayList<String> resultList = null;

		HttpURLConnection conn = null;
		StringBuilder jsonResults = new StringBuilder();
		try {
			StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
			sb.append("?key=" + API_KEY);
			sb.append("&input=" + URLEncoder.encode(input, "utf8"));

			URL url = new URL(sb.toString());
			
			System.out.println("URL: "+url);
			conn = (HttpURLConnection) url.openConnection();
			InputStreamReader in = new InputStreamReader(conn.getInputStream());

			// Load the results into a StringBuilder
			int read;
			char[] buff = new char[1024];
			while ((read = in.read(buff)) != -1) {
				jsonResults.append(buff, 0, read);
			}
		} catch (MalformedURLException e) {
			Log.e(LOG_TAG, "Error processing Places API URL", e);
			return resultList;
		} catch (IOException e) {
			Log.e(LOG_TAG, "Error connecting to Places API", e);
			return resultList;
		} finally {
			if (conn != null) {
				conn.disconnect();
			}
		}

		try {
		
			// Create a JSON object hierarchy from the results
			JSONObject jsonObj = new JSONObject(jsonResults.toString());
			JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

			// Extract the Place descriptions from the results
			resultList = new ArrayList<String>(predsJsonArray.length());
			for (int i = 0; i < predsJsonArray.length(); i++) {
				System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
				System.out.println("============================================================");
				resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
			}
		} catch (JSONException e) {
			Log.e(LOG_TAG, "Cannot process JSON results", e);
		}

		return resultList;
	}

	class GooglePlacesAutocompleteAdapter extends ArrayAdapter<String> implements Filterable {
		private ArrayList<String> resultList;

		public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
			super(context, textViewResourceId);
		}

		@Override
		public int getCount() {
			return resultList.size();
		}

		@Override
		public String getItem(int index) {
			return resultList.get(index);
		}

		@Override
		public Filter getFilter() {
			Filter filter = new Filter() {
				@Override
				protected FilterResults performFiltering(CharSequence constraint) {
					FilterResults filterResults = new FilterResults();
					if (constraint != null) {
						// Retrieve the autocomplete results.
						resultList = autocomplete(constraint.toString());

						// Assign the data to the FilterResults
						filterResults.values = resultList;
						filterResults.count = resultList.size();
					}
					return filterResults;
				}

				@Override
				protected void publishResults(CharSequence constraint, FilterResults results) {
					if (results != null && results.count > 0) {
						notifyDataSetChanged();
					} else {
						notifyDataSetInvalidated();
					}
				}
			};
			return filter;
		}
	}
	
	public void go(View view)
	{


		LatLng origin=null, destination=null;
		Geocoder geoCoder = new Geocoder(this);

			try {

				String currents = t1.getText().toString();
				String currentd = t2.getText().toString();

				List<Address> addressList = geoCoder.getFromLocationName(source, 1);
				timerflag1 = 0;
				while (addressList.size() == 0 && timerflag1 <= 10) {
					timerflag1++;
					addressList = geoCoder.getFromLocationName(source, 1);
					System.out.println("timerflag1:" + timerflag1);

				}
				if (addressList.size() > 0) {
					origin = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
				}
				if (timerflag1 == 11)
					Toast.makeText(this, "Enter a different source!", Toast.LENGTH_SHORT).show();

				addressList = geoCoder.getFromLocationName(dest, 1);


				timerflag2 = 0;
				while (addressList.size() == 0 && timerflag2 <= 10) {
					timerflag2++;
					addressList = geoCoder.getFromLocationName(dest, 1);
					System.out.println("timerflag2:" + timerflag2);

				}
				if (addressList.size() > 0) {
					destination = new LatLng(addressList.get(0).getLatitude(), addressList.get(0).getLongitude());
				}
				if (timerflag2 == 11)
					Toast.makeText(this, "Enter a different destination!", Toast.LENGTH_SHORT).show();
			} catch (Exception e) {
			}

			if (origin != null && destination != null) {
				Intent intent = new Intent(MainActivity.this, All4.class);
				Bundle args = new Bundle();
				args.putParcelable("from", origin);
				args.putParcelable("to", destination);
				intent.putExtra("bundle", args);
				intent.putExtra("source", source);
				intent.putExtra("dest", dest);

				startActivity(intent);
				finish();
			} else if (timerflag1 != 11 && timerflag2 != 11) {
				Toast.makeText(this, "choose proper locations!", Toast.LENGTH_SHORT).show();
			}

	}

	public void startplace(View view)
	{
		Toast.makeText(context,"pick your starting location",Toast.LENGTH_SHORT).show();

		which =1;
		try {
			startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
		}
		catch(Exception e)
		{}

	}
	public void destplace(View view)
	{

		Toast.makeText(context,"pick your destination",Toast.LENGTH_SHORT).show();

		which=2;

		try {
			startActivityForResult(builder.build(this), PLACE_PICKER_REQUEST);
		}
		catch(Exception e)
		{}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
		if (id == R.id.changering) {

			final Uri currentTone= RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALL);
			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
			startActivityForResult(intent, RingtoneManager.TYPE_ALL) ;


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
			final DBAdapter db = new DBAdapter(this);
			db.open();
			db.settutorial(1, 1);
			Intent intent=new Intent(MainActivity.this,MainActivity.class);
			finish();
			startActivity(intent);


		}


		return super.onOptionsItemSelected(item);
	}


}
