package com.example.moreaboutmoreapp;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.moreaboutmoreapp.Activities.MainActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AboutAppFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AboutAppFragment extends Fragment implements BackKeyPressedListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public static BackKeyPressedListener backKeyPressedListener;

    public AboutAppFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AboutAppFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AboutAppFragment newInstance(String param1, String param2) {
        AboutAppFragment fragment = new AboutAppFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        showBottomNavigationView();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ImageView Btn_BackPage;
        View view=inflater.inflate(R.layout.fragment_about_app, container, false);

        Btn_BackPage = view.findViewById(R.id.Btn_BackAboutApp);
        Btn_BackPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                Fragment newFragment = new SettingFragment();
//                FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                transaction.replace(R.id.managePostFragment, newFragment);
//                transaction.addToBackStack(null);
//                transaction.commit();
                getFragmentManager().popBackStack();
            }
        });


        hideBottomNavigationView();

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

