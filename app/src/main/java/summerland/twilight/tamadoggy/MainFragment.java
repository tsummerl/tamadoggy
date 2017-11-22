package summerland.twilight.tamadoggy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Date;
import java.util.concurrent.TimeUnit;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    int hunger, fitness, hygiene, fun, cash;
    ProgressBar progHunger, progFitness, progHygiene, progFun;
    TextView textHunger, textFitness, textHygiene, textFun, textCash;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance(String param1, String param2) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        progFitness = v.findViewById(R.id.barFitness);
        progHunger = v.findViewById(R.id.barHunger);
        progHygiene = v.findViewById(R.id.barHygiene);
        progFun = v.findViewById(R.id.barFun);
        textFitness = v.findViewById(R.id.textFitness);
        textFun = v.findViewById(R.id.textFun);
        textHunger = v.findViewById(R.id.textHunger);
        textHygiene = v.findViewById(R.id.textHygiene);
        textCash = v.findViewById(R.id.textCash);

        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
    public void onResume(){
        super.onResume();
        updateUI();
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void setProgress(int valFitness, int valFun, int valHygiene, int valHunger, int valCash)
    {
        try{
            fitness = valFitness;
            fun = valFun;
            hygiene = valHygiene;
            hunger = valHunger;
            cash = valCash;
            updateUI();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    private void updateUI(){
        try{
            progFitness.setProgress(fitness);
            progFun.setProgress(fun);
            progHygiene.setProgress(hygiene);
            progHunger.setProgress(hunger);

            textHygiene.setText("" + hygiene);
            textFun.setText("" + fun);
            textHunger.setText("" + hunger);
            textFitness.setText("" + fitness);
            textCash.setText("Cash: " + cash);
        }
        catch (Exception e){
            e.printStackTrace();
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
        void onFragmentInteraction(Uri uri);
    }

}
