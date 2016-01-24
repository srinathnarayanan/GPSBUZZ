package comsri.gps_buzz;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.location.Address;
import android.location.Geocoder;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.PagerTitleStrip;
import android.support.v4.view.ViewPager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ActionViewTarget;
import com.github.amlcurran.showcaseview.targets.PointTarget;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import cardsui.CardUI;

public class All4 extends FragmentActivity {

    SectionsPagerAdapter mSectionsPagerAdapter;
    static ViewPager mViewPager;
    String source, dest;
    public static LatLng origin, destination;
    public static CardUI mCardView1;
    public static CardUI mCardView2;
    public static CardUI mCardView3;
    public static CardUI mCardView4;
    public static DownloadTask downloadTask1;
    public static DownloadTask downloadTask2;
    public static DownloadTask downloadTask3;
    public static DownloadTask downloadTask4;
    public static int[] counter = new int[4];
    public static ShowcaseView show = null;
    public static ViewTarget target2;
    public static PointTarget target1;
    public static Context c;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all4);

        c = this;
        downloadTask1 = new DownloadTask();
        downloadTask2 = new DownloadTask();
        downloadTask3 = new DownloadTask();
        downloadTask4 = new DownloadTask();
        counter[0] = 0;
        counter[1] = 0;
        counter[2] = 0;
        counter[3] = 0;


        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);
        mViewPager.setCurrentItem(1);

        Bundle bundle = getIntent().getParcelableExtra("bundle");
        origin = bundle.getParcelable("from");
        destination = bundle.getParcelable("to");
        source = getIntent().getStringExtra("source");
        dest = getIntent().getStringExtra("dest");

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;

        final DBAdapter db = new DBAdapter(this);
        db.open();

        if (db.tutorialcheck(2)) {

            target2 = new ViewTarget(R.id.pager_title_strip, this);
            target1 = new PointTarget(width / 4, height / 5);


            show = new ShowcaseView.Builder(this)
                    .setTarget(target1)
                    .setContentText("by clicking on the cards")
                    .setContentTitle("CHOOSE ONE OF THE PATHS")
                    .setOnClickListener(new View.OnClickListener() {

                        int counter = 0;

                        @Override
                        public void onClick(View v) {

                            switch (counter) {
                                case 0:
                                    show.setShowcase(target2, true);
                                    show.setContentText("by swiping left or right");
                                    show.setContentTitle("VIEW DIFFERENT MODES OF TRANSPORT");
                                    mCardView1.setAlpha((float) 0.5);
                                    counter++;
                                    break;
                                case 1:
                                    mCardView1.setAlpha((float) 1);
                                    db.settutorial(2, 0);
                                    show.setVisibility(View.GONE);
                                    break;

                            }
                        }
                    })
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .build();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all4, menu);
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
            final Uri currentTone = RingtoneManager.getActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALL);
            Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone");
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, currentTone);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, false);
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
            startActivityForResult(intent, RingtoneManager.TYPE_ALL);

        } else if (id == R.id.viewalarm) {

            Intent intent = new Intent(this, AlarmActivity.class);
            intent.putExtra("view", 1);
            startActivity(intent);
            return true;
        } else if (id == R.id.tutorial) {
            DBAdapter db = new DBAdapter(this);
            db.open();
            db.settutorial(2, 1);

            Intent intent = new Intent(this, All4.class);
            Bundle args = new Bundle();
            args.putParcelable("from", origin);
            args.putParcelable("to", destination);
            intent.putExtra("bundle", args);
            intent.putExtra("source", source);
            intent.putExtra("dest", dest);
            finish();
            startActivity(intent);

        }


        return super.onOptionsItemSelected(item);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        FragmentManager fragm;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            fragm = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            Drawable drawable = null;
            String s="";
            switch (position) {
                case 0:
                    drawable = ContextCompat.getDrawable(c, R.drawable.car);
                    s="     CAR     ";
                    break;
                case 1:
                    drawable = ContextCompat.getDrawable(c, R.drawable.bus);
                    s="     TRANSIT     ";
                    break;
                case 2:
                    drawable = ContextCompat.getDrawable(c, R.drawable.cycle);
                    s="     CYCLE     ";
                    break;
                case 3:
                    drawable = ContextCompat.getDrawable(c, R.drawable.walk);
                    s="     WALK     ";
                    break;

            }


            /*
            SpannableStringBuilder sb = new SpannableStringBuilder(" "+s); // space added before text for convenience
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth()/4, drawable.getIntrinsicHeight()/4);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            */

            return s;
        }


        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).


            this.getPageTitle(0);

            if (position == 0)
                return FragmentDrive.newInstance();
            else if (position == 1)
                return FragmentTransit.newInstance();
            else if (position == 2)
                return FragmentBicycle.newInstance();
            else
                return FragmentWalking.newInstance();


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }


    }

    public static class FragmentDrive extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ProgressBar p;

        public static FragmentDrive newInstance() {
            FragmentDrive fragment = new FragmentDrive();

            return fragment;
        }


        public FragmentDrive()
        {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_all4, container, false);

            TextView top=(TextView)rootView.findViewById(R.id.top);
            Drawable drawable = ContextCompat.getDrawable(c, R.drawable.car);

            SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 4, drawable.getIntrinsicHeight() / 4);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            top.setText(sb);



            ImageView left = (ImageView) rootView.findViewById(R.id.left);
            left.setVisibility(View.INVISIBLE);

            ImageView right = (ImageView) rootView.findViewById(R.id.right);
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(1);
                }
            });


            p = (ProgressBar) rootView.findViewById(R.id.progressBar);
            String url = getDirectionsUrl(origin, destination, "driving");
            downloadTask1.execute(new urlnumb(url, 1));

            mCardView1 = (CardUI) rootView.findViewById(R.id.cardsview);
            mCardView1.setSwipeable(false);

/*
                MyPlayCard androidViewsCard = new MyPlayCard("DRIVING",
                        "", "#669900",
                        "#669900", false, false);

                mCardView1.addCard(androidViewsCard);

                // draw cards
                mCardView1.refresh();
*/

            return rootView;
        }

        public void update() {
            p.setVisibility(View.INVISIBLE);

        }
    }

    public static class FragmentTransit extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ProgressBar p;

        public static FragmentTransit newInstance() {
            FragmentTransit fragment = new FragmentTransit();

            return fragment;
        }

        public FragmentTransit() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_all4, container, false);
            TextView top=(TextView)rootView.findViewById(R.id.top);
            Drawable drawable = ContextCompat.getDrawable(c, R.drawable.bus);

            SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 4, drawable.getIntrinsicHeight() / 4);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            top.setText(sb);


            ImageView left = (ImageView) rootView.findViewById(R.id.left);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(0);
                }
            });

            ImageView right = (ImageView) rootView.findViewById(R.id.right);
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(2);
                }
            });


            p = (ProgressBar) rootView.findViewById(R.id.progressBar);

            String url = getDirectionsUrl(origin, destination, "transit");
            urlnumb urlnumb1 = new urlnumb(url, 2);

            downloadTask2.execute(new urlnumb(url, 2));

            mCardView2 = (CardUI) rootView.findViewById(R.id.cardsview);
            mCardView2.setSwipeable(false);

/*
            MyPlayCard androidViewsCard = new MyPlayCard("TRANSIT",
                    "", "#669900",
                    "#669900", false, false);

            mCardView2.addCard(androidViewsCard);

            // draw cards
            mCardView2.refresh();
*/

            return rootView;
        }

        public void update() {
            p.setVisibility(View.INVISIBLE);
        }
    }

    public static class FragmentBicycle extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ProgressBar p;

        public static FragmentBicycle newInstance() {
            FragmentBicycle fragment = new FragmentBicycle();

            return fragment;
        }

        public FragmentBicycle() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_all4, container, false);

            TextView top=(TextView)rootView.findViewById(R.id.top);
            Drawable drawable = ContextCompat.getDrawable(c, R.drawable.cycle);

            SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 4, drawable.getIntrinsicHeight() / 4);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            top.setText(sb);


            ImageView left = (ImageView) rootView.findViewById(R.id.left);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(1);
                }
            });

            ImageView right = (ImageView) rootView.findViewById(R.id.right);
            right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(3);
                }
            });


            p = (ProgressBar) rootView.findViewById(R.id.progressBar);
            String url = getDirectionsUrl(origin, destination, "bicycling");
            downloadTask3.execute(new urlnumb(url, 3));

            mCardView3 = (CardUI) rootView.findViewById(R.id.cardsview);
            mCardView3.setSwipeable(false);

/*
            MyPlayCard androidViewsCard = new MyPlayCard("BICYCLING",
                    "", "#669900",
                    "#669900", false, false);

            mCardView3.addCard(androidViewsCard);

            // draw cards
            mCardView3.refresh();
*/


            return rootView;
        }

        public void update() {
            p.setVisibility(View.INVISIBLE);
        }
    }

    public static class FragmentWalking extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public ProgressBar p;

        public static FragmentWalking newInstance() {
            FragmentWalking fragment = new FragmentWalking();

            return fragment;
        }

        public FragmentWalking() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_all4, container, false);


            TextView top=(TextView)rootView.findViewById(R.id.top);
            Drawable drawable = ContextCompat.getDrawable(c, R.drawable.walk);

            SpannableStringBuilder sb = new SpannableStringBuilder(" "); // space added before text for convenience
            drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 4, drawable.getIntrinsicHeight() / 4);
            ImageSpan span = new ImageSpan(drawable, ImageSpan.ALIGN_BASELINE);
            sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            top.setText(sb);


            ImageView right = (ImageView) rootView.findViewById(R.id.right);
            right.setVisibility(View.INVISIBLE);


            ImageView left = (ImageView) rootView.findViewById(R.id.left);
            left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewPager.setCurrentItem(2);
                }
            });


            p = (ProgressBar) rootView.findViewById(R.id.progressBar);
            String url = getDirectionsUrl(origin, destination, "walking");
            downloadTask4.execute(new urlnumb(url, 4));

            mCardView4 = (CardUI) rootView.findViewById(R.id.cardsview);
            mCardView4.setSwipeable(false);
/*

            MyPlayCard androidViewsCard = new MyPlayCard("WALKING",
                    "", "#669900",
                    "#669900", false, false);

            mCardView4.addCard(androidViewsCard);

            // draw cards
            mCardView4.refresh();

*/

            return rootView;
        }

        public void update() {
            p.setVisibility(View.INVISIBLE);
        }
    }


    static public String getDirectionsUrl(LatLng origin, LatLng dest, String modetype) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String mode = "mode=" + modetype;

        String alternatives = "alternatives=" + "true";
        // Sensor enabled
        String sensor = "sensor=true";

        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode + "&" + sensor + "&" + alternatives;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

        return url;
    }


    static public String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
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
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            Log.d("downloading failed", e.toString());
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }


    private class DownloadTask extends AsyncTask<urlnumb, Void, urlnumb> {

        // Downloading data in non-ui thread
        @Override
        protected urlnumb doInBackground(urlnumb... url) {

            // For storing data from web service
            String data = "";

            urlnumb urlnumb1 = new urlnumb("", 0);
            try {
                // Fetching the data from web service
                urlnumb1.url = downloadUrl(url[0].url);
                urlnumb1.number = url[0].number;
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return urlnumb1;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(urlnumb urlnumb1) {
            super.onPostExecute(urlnumb1);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(urlnumb1);
        }
    }

    /**
     * A class to parse the Google Places in JSON format
     */
    private class ParserTask extends AsyncTask<urlnumb, Integer, mode> {

        int numb;

        // Parsing the data in non-ui thread
        @Override
        protected mode doInBackground(urlnumb... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;
            ArrayList<String> travelmode;
            mode a = null;

            try {
                jObject = new JSONObject(jsonData[0].url);
                DirectionsJSONParserRoutes parser = new DirectionsJSONParserRoutes();

                numb = jsonData[0].number;
                // Starts parsing data
                a = parser.parse(jObject);
                routes = a.routes;
                travelmode = a.travelmode;

            } catch (Exception e) {
                e.printStackTrace();
            }
            return a;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(mode result) {

            FragmentWalking fw1 = (FragmentWalking) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 3);
            fw1.update();
            FragmentDrive fw2 = (FragmentDrive) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 0);
            fw2.update();
            FragmentBicycle fw3 = (FragmentBicycle) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 2);
            fw3.update();
            FragmentTransit fw4 = (FragmentTransit) getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.pager + ":" + 1);
            fw4.update();


            String top, bottom;

            CardUI m;

            if (numb == 1)
                m = mCardView1;
            else if (numb == 2)
                m = mCardView2;
            else if (numb == 3)
                m = mCardView3;
            else
                m = mCardView4;

            if (result.routes.size() < 1) {
                top = "NO ROUTES AVAILABLE";
                bottom = "choose a different mode of transport";

                MyPlayCard androidViewsCard = new MyPlayCard(top,
                        bottom, "#669900",
                        "#669900", false, false);
                androidViewsCard.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(v.getContext(), MainActivity.class);
                        startActivity(intent);
                    }
                });

                m.addCard(androidViewsCard);

                // draw cards
                m.refresh();


            } else {
                for (int i = 0; i < result.routes.size(); i++) {
                    List<HashMap<String, String>> path = result.routes.get(i);

                    HashMap<String, String> point = path.get(0);
                    String summary = point.get("summary");
                    point = path.get(1);
                    bottom = (String) point.get("distance");
                    bottom += "\n" + summary;

                    point = path.get(2);
                    top = (String) point.get("duration");
                    counter[numb - 1]++;

                    final MyPlayCard androidViewsCard = new MyPlayCard(top,
                            bottom, "#669900",
                            "#669900", false, false);
                    final int position = counter[numb - 1];
                    androidViewsCard.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {

                            Intent intent = new Intent(v.getContext(), Choices.class);

                            Bundle args = new Bundle();
                            args.putParcelable("from", origin);
                            args.putParcelable("to", destination);
                            args.putInt("notif", 0);
                            intent.putExtra("bundle", args);
                            intent.putExtra("numb", numb);
                            intent.putExtra("count", position);
                            intent.putExtra("source", source);
                            intent.putExtra("dest", dest);

                            startActivity(intent);
                        }
                    });

                    m.addCard(androidViewsCard);

                    // draw cards
                    m.refresh();

                }
            }
        }
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case RingtoneManager.TYPE_ALL:
                if (resultCode == RESULT_OK) {
                    Uri notifToneUri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
                    RingtoneManager.setActualDefaultRingtoneUri(this, RingtoneManager.TYPE_ALL, notifToneUri);
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(All4.this, MainActivity.class);
        startActivity(intent);
        finish();
    }


}
