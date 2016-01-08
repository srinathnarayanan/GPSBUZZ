package comsri.gps_buzz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import cardsui.Card;
import cardsui.Card.OnCardSwiped;
import cardsui.CardUI;
import cardsui.SwipeDismissTouchListener;

import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.PointTarget;


public class AlarmActivity extends Activity {
    Double latitude;
    Double longitude;
    int radius;
    String alarm,reminder;
    LocationManager lm;
    LocationListener locationListener;
    String senderTel;
    ArrayList<String> alarmnames = new ArrayList<String>();
    PointTarget target1;
    ShowcaseView show;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);


        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;


        DBAdapter db = new DBAdapter(this);

    /*    try {
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
*/
        db.open();

        if (db.tutorialcheck(4))
        {
            target1 = new PointTarget(width / 4, height / 5);


            show = new ShowcaseView.Builder(this)
                    .setTarget(target1)
                    .setContentText("by swiping")
                    .setContentTitle("DELETE ALARMS")
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .build();
            db.settutorial(4,0);

        }


        final Intent intent=getIntent();
        if(intent.getIntExtra("view",0)!=1)
        {

            alarm = intent.getStringExtra(AlarmDialog.alarm);
            latitude = Double.parseDouble(intent.getStringExtra(AlarmDialog.lati));
            longitude = Double.parseDouble(intent.getStringExtra(AlarmDialog.longi));
            reminder=intent.getStringExtra(AlarmDialog.reminder);
            radius=intent.getIntExtra(AlarmDialog.radius, 20);

            if (db.notPresent(alarm)) {
                long a = db.addAlarm(alarm,reminder);
                LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

                Intent intent1 = new Intent("comsri.gps_buzz.AlarmReceiverActivity");

                intent1.putExtra("name", alarm);
                intent1.putExtra("lati",latitude);
                intent1.putExtra("longi",longitude);
                intent1.putExtra("reminder",reminder);
                intent1.putExtra("radius",radius);

                PendingIntent proximityIntent = PendingIntent.getBroadcast(getApplicationContext(), (int) a, intent1, 0);


                System.out.println("radius:"+radius);
                locationManager.addProximityAlert(
                        latitude, // the latitude of the central point of the alert region
                        longitude, // the longitude of the central point of the alert region
                        radius, // the radius of the central point of the alert region, in meters
                        -1, // time for this proximity alert, in milliseconds, or -1 to indicate no expiration
                        proximityIntent // will be used to generate an Intent to fire when entry to or exit from the alert region is detected
                );


            }

        }

        alarmnames=db.getAllAlarms();
        int i=0;
        CardUI mCardView = (CardUI) findViewById(R.id.cardsview);
        if(alarmnames==null)
        {
            mCardView.setSwipeable(false);
            MyPlayCard androidViewsCard = new MyPlayCard("NO ALARMS SET",
                    null, "#FF0000",
                    "#FF0000", false, false);
            mCardView.addCard(androidViewsCard);
            mCardView.refresh();

        }
        else
        {
            mCardView.setSwipeable(true);

            for (i = 0; i < alarmnames.size(); i++) {
                MyPlayCard androidViewsCard = new MyPlayCard(alarmnames.get(i),
                        null, "#89A131",
                        "#89A131", false, false);
                androidViewsCard.setOnCardSwipedListener(new t(getBaseContext(), alarmnames.get(i)));
                mCardView.addCard(androidViewsCard);

                // draw cards
                mCardView.refresh();
            }
        }
        db.close();

    }

 /*
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
*/


    public void gohome(View view)
    {
        Intent intent=new Intent(this,MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();

    }

@Override
public void onBackPressed()
{
    finish();
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
        else if (id==R.id.tutorial)
        {
            DBAdapter db = new DBAdapter(this);
            db.open();
            db.settutorial(4,1);
            Intent intent=new Intent(this,AlarmActivity.class);
            intent.putExtra("view",1);
            finish();
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_alarm, menu);
        return true;
    }

}



