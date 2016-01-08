package comsri.gps_buzz;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Srinath on 10/6/2015.
 */
public class mode {

    List<List<HashMap<String, String>>> routes = new ArrayList<List<HashMap<String, String>>>();
    ArrayList<String> travelmode=new ArrayList<String>();

    public mode(List<List<HashMap<String, String>>> a,ArrayList<String> b)
    {
        routes=a;
        travelmode=b;
    }

}
