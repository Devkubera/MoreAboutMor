package com.example.moreaboutmoreapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TwentyoneFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TwentyoneFragment extends Fragment implements BackKeyPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static BackKeyPressedListener backKeyPressedListener;

    public TwentyoneFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TwentyoneFragment.
     */
    // TODO: Rename and change types and number of parameters

    ImageView btn_Back;

    public static TwentyoneFragment newInstance(String param1, String param2) {
        TwentyoneFragment fragment = new TwentyoneFragment();
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
    public void onPause() {
        super.onPause();
        backKeyPressedListener = null;

    }

    @Override
    public void onResume() {
        super.onResume();
        backKeyPressedListener = this;

    }

    @Override
    public void onBackPressed() {
        getFragmentManager().popBackStack();
    }

    @Override
    public void onDestroyView() {
        showBottomNavigationView();
        super.onDestroyView();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_twentyone, container, false);

        hideBottomNavigationView();

        btn_Back = view.findViewById(R.id.Btn_Back21);
        btn_Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new MenuFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.menuFragment, newFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private void hideBottomNavigationView() {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav);
        bottomNavigationView.setVisibility(View.GONE);

        View shadowNav = getActivity().findViewById(R.id.shadowNav);
        shadowNav.setVisibility(View.GONE);
    }

    private void showBottomNavigationView() {
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.nav);
        bottomNavigationView.setVisibility(View.VISIBLE);

        View shadowNav = getActivity().findViewById(R.id.shadowNav);
        shadowNav.setVisibility(View.VISIBLE);
    }
}