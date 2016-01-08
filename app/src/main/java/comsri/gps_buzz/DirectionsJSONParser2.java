package comsri.gps_buzz;

/**
 * Created by Srinath on 10/5/2015.
 */
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.model.LatLng;

public class DirectionsJSONParser2
{

    /** Receives a JSONObject and returns a list of lists containing latitude and longitude */
    //public List<List<HashMap<String, String>>> parse(JSONObject jObject)
    public mode parse(JSONObject jObject)
    {
        List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
        ArrayList<String> travelmode=new ArrayList<String>();
        JSONArray jRoutes = null;
        JSONArray jLegs = null;
        JSONArray jSteps = null;
        JSONObject jDistance = null;
        JSONObject jDuration = null;

        try
        {
            jRoutes = jObject.getJSONArray("routes");
/** Traversing all routes */
            for (int i = 0; i < jRoutes.length(); i++)
            {
                jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List<HashMap<String, String>> path = new ArrayList<HashMap<String, String>>();
/** Traversing all legs */
                for (int j = 0; j < jLegs.length(); j++)
                {
/** Getting distance from the json data */
                    jDistance = ((JSONObject) jLegs.get(j)).getJSONObject("distance");
                    HashMap<String, String> hmDistance = new HashMap<String, String>();
                    hmDistance.put("distance", jDistance.getString("text"));

/** Getting duration from the json data */
                    jDuration = ((JSONObject) jLegs.get(j)).getJSONObject("duration");
                    HashMap<String, String> hmDuration = new HashMap<String, String>();
                    hmDuration.put("duration", jDuration.getString("text"));

/** Adding distance object to the path */
                    path.add(hmDistance);

/** Adding duration object to the path */
                    path.add(hmDuration);

                    jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");

/** Traversing all steps */
                    for (int k = 0; k < jSteps.length(); k++)
                    {
                        String polyline = "";
                        String modetype="";
                        String type="0";
                        polyline = (String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points");
                        modetype=(String) ( ((JSONObject) jSteps.get(k)).get("travel_mode"));
                        travelmode.add(modetype);
                        if(modetype.equals("WALKING"))
                            type="1";
                        else
                            type="0";

/** Traversing all points */

                    }
                }
                routes.add(path);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (Exception e)
        {
        }

        mode m=new mode(routes,travelmode);
        return m;
    }


}