package summerland.twilight.tamadoggy;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;

// API KEY: AIzaSyAaaJjwajzpGLJh7Ngm6RUm5gRz7y9bUVQ

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WalkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkFragment extends Fragment implements OnMapReadyCallback{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LAT = "LAT";
    private static final String ARG_LONG = "LONG";

    // TODO: Rename and change types of parameters
    private Double mGPSLatitude;
    private Double mGPSLong;
    private GoogleMap mMap;
    private TextView textDuration;
    private JSONObject mDuration;
    private Button buttonWalk;

    private LatLng mCurrentLocation, mDestinationLocation;

    private OnFragmentInteractionListener mListener;

    public WalkFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param mGPSLatitude Parameter 1.
     * @param mGPSLong Parameter 2.
     * @return A new instance of fragment WalkFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WalkFragment newInstance(Long mGPSLatitude, Long mGPSLong)
    {
        WalkFragment fragment = new WalkFragment();
        Bundle args = new Bundle();
        args.putDouble(ARG_LAT, mGPSLatitude);
        args.putDouble(ARG_LONG, mGPSLong);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGPSLatitude = getArguments().getDouble(ARG_LAT);
            mGPSLong = getArguments().getDouble(ARG_LONG);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v =inflater.inflate(R.layout.fragment_walk, container, false);
        textDuration = v.findViewById(R.id.textEstTime);
        buttonWalk = v.findViewById(R.id.buttonWalk);
        buttonWalk.setEnabled(false);
        buttonWalk.setAlpha(0.5f);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;

        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if(mGPSLatitude== null && mGPSLong == null)
        {
            mGPSLatitude = 0.0; mGPSLong = 0.0;
        }
        mCurrentLocation = new LatLng(mGPSLatitude, mGPSLong);
        mMap.addMarker(new MarkerOptions().position(mCurrentLocation).title("Your location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mCurrentLocation));
        mMap.animateCamera( CameraUpdateFactory.zoomTo( 14.0f ) );
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng point) {
                // TODO Auto-generated method stub
                mDestinationLocation = point;
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(mCurrentLocation).title("Your location"));
                mMap.addMarker(new MarkerOptions().position(point).title("Walking Here"));
                ApiDirectionsAsyncTask task = new ApiDirectionsAsyncTask();
                task.execute();
            }
        });
    }

    public class ApiDirectionsAsyncTask extends AsyncTask<URL, Integer, StringBuilder> {
        private static final String DIRECTIONS_API_BASE = "https://maps.googleapis.com/maps/api/directions";
        private static final String OUT_JSON = "/json";

        // API KEY of the project Google Map Api For work
        private static final String API_KEY = "AIzaSyAaaJjwajzpGLJh7Ngm6RUm5gRz7y9bUVQ";

        @Override
        protected StringBuilder doInBackground(URL... params) {
            HttpURLConnection mUrlConnection = null;
            StringBuilder mJsonResults = new StringBuilder();
            try {
                StringBuilder sb = new StringBuilder(DIRECTIONS_API_BASE + OUT_JSON);
                sb.append("?mode=walking&origin=" + URLEncoder.encode(mCurrentLocation.latitude + "," +mCurrentLocation.longitude, "utf8"));
                sb.append("&destination=" + URLEncoder.encode(mDestinationLocation.latitude + "," +mDestinationLocation.longitude, "utf8"));
                sb.append("&key=" + API_KEY);

                URL url = new URL(sb.toString());
                mUrlConnection = (HttpURLConnection) url.openConnection();
                InputStreamReader in = new InputStreamReader(mUrlConnection.getInputStream());

                // Load the results into a StringBuilder
                int read;
                char[] buff = new char[1024];
                while ((read = in.read(buff)) != -1){
                    mJsonResults.append(buff, 0, read);
                }

            } catch (MalformedURLException e) {
                return null;

            } catch (IOException e) {
                System.out.println("Error connecting to Distance Matrix");
                return null;
            } finally {
                if (mUrlConnection != null) {
                    mUrlConnection.disconnect();
                }
            }
            //Toast.makeText(getActivity(),mJsonResults.toString(), Toast.LENGTH_LONG).show();
            return mJsonResults;
        }
        protected void onPostExecute(StringBuilder sb)
        {
            try{
                JSONObject object = new JSONObject(sb.toString());
                JSONArray arr = object.getJSONArray("routes");
                if(arr.length() > 0)
                {
                    JSONObject route = (JSONObject) arr.get(0);
                    arr = route.getJSONArray("legs");
                    if(arr.length()>0)
                    {
                        JSONObject leg = (JSONObject) arr.get(0);
                        JSONObject duration = leg.getJSONObject("duration");
                        mDuration = duration;
                        updateWalk();
                    }
                }
            }
            catch (Exception e)
            {

            }
//            if(sb!= null)
//            {
//                Toast.makeText(getActivity(),sb.toString(), Toast.LENGTH_LONG).show();
//            }
        }
    }

    private void updateWalk(){
        try{
            textDuration.setText("Estimate Time: " + mDuration.getString("text"));
            buttonWalk.setEnabled(true);
            buttonWalk.setAlpha(1);
            buttonWalk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getActivity(), "Dog has been walked!", Toast.LENGTH_LONG).show();
                    try{
                        ((MainGameActivity) getActivity()).walkDog(mDuration.getInt("value"));
                    }
                    catch (Exception e)
                    {

                    }
                }
            });
        }
        catch (Exception e)
        {

        }
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void updateGPS(double lat, double lon);
    }
}
