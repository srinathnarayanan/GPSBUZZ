package comsri.gps_buzz;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class CurrentLoc extends Activity {


    private GoogleMap googleMap;
    public LatLng newpoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_loc);


        Intent intent= getIntent();

            String name = intent.getStringExtra("name");
            String reminer = intent.getStringExtra("reminder");
            int radius= intent.getIntExtra("radius", 20);
        System.out.print(radius);
            TextView reminders=(TextView)findViewById(R.id.tv1);
        if(reminer.length()>0)
            reminders.setText("REMINDER\n"+reminer);
        else
            reminders.setText("NO REMINDERS");


            newpoint = new LatLng(intent.getDoubleExtra("lati",0), intent.getDoubleExtra("longi", 0));
            initilizeMap();

            CameraPosition cameraPosition = new CameraPosition.Builder().target(newpoint).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        Circle circle = googleMap.addCircle(new CircleOptions()
                .center(newpoint)
                .radius(radius)
                .strokeColor(Color.BLUE)
                .strokeWidth(2)
        .fillColor(0x100000FF));

            MarkerOptions options = new MarkerOptions();

            // Setting the position of the marker
            options.position(newpoint);
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));
            options.title(name);
            CurrentLoc.this.googleMap.addMarker(options);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_current_loc, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.changering) {

            Intent intent1 = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_RINGTONE);
            intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            intent1.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            this.startActivityForResult(intent1,5);

        }

        return super.onOptionsItemSelected(item);
    }

    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById( R.id.map)).getMap();
            // check if map is created successfully or not

            googleMap.setOnInfoWindowClickListener(
                    new GoogleMap.OnInfoWindowClickListener() {
                        public void onInfoWindowClick(Marker marker) {

                       //     AlarmDialog e = new AlarmDialog(marker.getTitle(), marker.getPosition(), getBaseContext());
                       //     e.show(getFragmentManager(), ALARM_SERVICE);
                        }
                    });
            googleMap.setMyLocationEnabled(true);


            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }

}
