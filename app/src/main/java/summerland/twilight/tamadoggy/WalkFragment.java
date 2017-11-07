package summerland.twilight.tamadoggy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WalkFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WalkFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalkFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_LAT = "LAT";
    private static final String ARG_LONG = "LONG";

    // TODO: Rename and change types of parameters
    private Double mGPSLatitude;
    private Double mGPSLong;

    private TextView textGPS;

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
    public static WalkFragment newInstance(Long mGPSLatitude, Long mGPSLong) {
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
        textGPS = v.findViewById(R.id.textGPSlocation);
        textGPS.setText("GPS LOCATION: " + mGPSLatitude + ", " + mGPSLong);
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
