package comsri.gps_buzz;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

public class AlarmDialog extends DialogFragment {
    String title;
    Context c;
    LatLng ll;
    public static final String alarm="comsri.gps_buzz.alarm";
    public static final String lati="comsri.gps_buzz.lati";
    public static final String longi="comsri.gps_buzz.longi";
    public static final String reminder="comsri.gps_buzz.reminder";
    public static final String radius="comsri.gps_buzz.radius";


    public AlarmDialog(String t, LatLng l,Context cc)
    {
        ll=l;
        c=cc;
        title=t;
    }

    public AlarmDialog()
    {
        ll=null;
        c=null;
        title=null;

    }



    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction

        final String s=title;
        final String  latitude=Double.toString(ll.latitude);
        final String  longitude=Double.toString(ll.longitude);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.dialoglayout, null);
        builder.setView(dialoglayout);

        final EditText alarmname=(EditText)dialoglayout.findViewById(R.id.alarmname);
        final EditText reminders=(EditText)dialoglayout.findViewById(R.id.reminder);
        final SeekBar seek=(SeekBar)dialoglayout.findViewById(R.id.seekBar);
        final TextView t4=(TextView)dialoglayout.findViewById(R.id.textView4);
        final Button ok=(Button) dialoglayout.findViewById(R.id.button);
        final Button cancel=(Button) dialoglayout.findViewById(R.id.button2);


        seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {

                t4.setText(Integer.toString(progress+20));

            }
        });


        alarmname.setText(s.split(",")[0]);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(c, AlarmActivity.class);
                intent.putExtra(alarm, alarmname.getText().toString());
                intent.putExtra(reminder, reminders.getText().toString());
                intent.putExtra(lati, latitude);
                intent.putExtra(longi, longitude);
                intent.putExtra(radius, Integer.parseInt(t4.getText().toString()));

                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);


            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Toast.makeText(getActivity(), "select a location", Toast.LENGTH_LONG).show();
            }
        });

        /*
        builder.setPositiveButton("OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(c, AlarmActivity.class);
                        intent.putExtra(alarm, alarmname.getText().toString());
                        intent.putExtra(reminder, reminders.getText().toString());
                        intent.putExtra(lati, latitude);
                        intent.putExtra(longi, longitude);
                        intent.putExtra(radius, Integer.parseInt(t4.getText().toString()));

                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        Toast.makeText(c, alarmname.getText().toString() + " alarm added", Toast.LENGTH_LONG).show();

                    }
                }).setNegativeButton("CANCEL",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity(), "select a location", Toast.LENGTH_LONG).show();
            }
        });
        */
        // Create the AlertDialog object and return it
        return builder.create();
    }
};

