package com.example.weissenberger.pubhub;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;


/**
 * A simple {@link Fragment} subclass.
 */
public class SortSelect extends Fragment implements View.OnClickListener {
    RadioButton wait;
    RadioButton cover;
    RadioButton location;
    private OnFragmentInteractionListener mListener;



    public SortSelect() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sort_select, container, false);
        cover = (RadioButton) view.findViewById(R.id.coverRB);
        wait = (RadioButton) view.findViewById(R.id.waitRB);
        location = (RadioButton) view.findViewById(R.id.locationRB);
        cover.setOnClickListener(this);
        wait.setOnClickListener(this);
        location.setOnClickListener(this);


        return view;
    }

    @Override
    public void onClick(View view) {
        onButtonPressed(view.getId());

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MapDisplayFrag.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void onButtonPressed(int id) {
        String s="";

        switch (id){
            case R.id.locationRB:
                s = MainActivity.SORT_LOCATION;
                break;
            case R.id.coverRB:
                s = MainActivity.SORT_COVER;
                break;
            case R.id.waitRB:
                s = MainActivity.SORT_WAIT;
                break;
        }

        if (mListener != null) {
            mListener.onSortChanged(s);
        }
    }

    public interface OnFragmentInteractionListener {
        void onSortChanged(String uri);
    }
}
