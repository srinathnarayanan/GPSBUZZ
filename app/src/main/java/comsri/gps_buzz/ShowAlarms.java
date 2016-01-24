package comsri.gps_buzz;

import cardsui.Card;
import cardsui.CardUI;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

class t implements Card.OnCardSwiped
{
Context x;
String tt;
	public t(Context c,String t)
	{
		x=c;
		tt=t;
	}
	
	@Override
	public void onCardSwiped(Card card, View layout) {
		DBAdapter db=new DBAdapter(x);
		db.open();
		
			int i=db.deleteAlarm(tt);
			System.out.println(i);

			Toast.makeText(x, tt+" alarm deleted", Toast.LENGTH_LONG).show();
			String context = Context.LOCATION_SERVICE;
			LocationManager locationManager = (LocationManager) x.getSystemService(context);
			Intent intent1 = new Intent("comsri.gps_buzz.AlarmReceiverActivity");
	        PendingIntent operation = 
			PendingIntent.getBroadcast(x, i ,intent1 , 0);
	        locationManager.removeProximityAlert(operation);
			operation.cancel();
		db.close();
	}
}

public class ShowAlarms extends Activity {

	ArrayList<String> alarmnames=new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarm);
	
	
	DBAdapter db=new DBAdapter(this);
	
	db.open();
	
	alarmnames=db.getAllAlarms();

		int i=0;
	 CardUI mCardView = (CardUI) findViewById(R.id.cardsview);
        mCardView.setSwipeable(true);
    if(alarmnames==null)
    {
    	MyPlayCard androidViewsCard = new MyPlayCard("No alarms set",
				null, "#89A131",
				"#89A131", false, false);
		mCardView.addCard(androidViewsCard);
		 
		// draw cards
		mCardView.refresh();
		
    	
    }
    else
    {
	for(i=0;i<alarmnames.size();i++)
	{
		MyPlayCard androidViewsCard = new MyPlayCard(alarmnames.get(i),
				null, "#89A131",
				"#89A131", false, false);
		androidViewsCard.setOnCardSwipedListener(new t(getBaseContext(),alarmnames.get(i)));
		mCardView.addCard(androidViewsCard);
		 
		// draw cards
		mCardView.refresh();
		
	}
    }
	db.close();

}
	
	public void gohome(View view)
	{
		Intent intent=new Intent(this,MainActivity.class);
		startActivity(intent);
		
	}

	
}
