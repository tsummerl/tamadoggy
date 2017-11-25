package summerland.twilight.tamadoggy;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StoreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StoreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StoreFragment extends Fragment {

    private static final String ITEMS_KEY = "ITEMS_KEY";
    private static final String CASH = "CASH";

    private View v;
    private LinearLayout layout;
    private RecyclerView.LayoutManager m_layoutManager;
    private ArrayList<Const.Items> m_storeItems;
    private TextView m_cashText;
    private RecyclerView m_recycler;
    private StoreAdapter m_adapter;
    private OnFragmentInteractionListener mListener;
    private int m_cash;

    public StoreFragment() {
        // Required empty public constructor
    }

    public static StoreFragment newInstance(ArrayList<Const.Items> items, int cash) {
        StoreFragment fragment = new StoreFragment();
        Bundle args = new Bundle();
        args.putSerializable(ITEMS_KEY, items);
        args.putInt(CASH, cash);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_store, container, false);
        m_storeItems = (ArrayList<Const.Items>) getArguments().getSerializable(ITEMS_KEY);
        m_cashText = v.findViewById(R.id.textStoreCash);
        layout = v.findViewById(R.id.linearHolderStore);
        m_recycler = v.findViewById(R.id.recyclerStoreItems);
        m_adapter = new StoreAdapter(m_storeItems);
        m_recycler.setAdapter(m_adapter);
        updateUI();
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public void updateCash(int cash)
    {
        m_cash = cash;
        //updateUI();
    }

    private void updateUI()
    {
        m_cashText.setText("Cash: $"+m_cash);
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
