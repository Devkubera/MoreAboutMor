package com.example.moreaboutmoreapp;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.moreaboutmoreapp.Adapters.TagAdapter;
import com.example.moreaboutmoreapp.Models.RankingModelTest;
import com.example.moreaboutmoreapp.Models.User;
import com.example.moreaboutmoreapp.Models.subjectNameModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RankingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RankingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RankingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RankingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RankingFragment newInstance(String param1, String param2) {
        RankingFragment fragment = new RankingFragment();
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
    public void onDestroyView() {
        showBottomNavigationView();
        super.onDestroyView();
    }

    ImageView btn_back;
    Spinner spinnerFaculty;
    List<String> options = new ArrayList<>();
    List<String> subjects = new ArrayList<>();
    RankingModelTest rankingModel;

    // declaration for textView Faculty Skills
    TextView text_major1;
    TextView text_major2;
    TextView text_major3;
    TextView text_major4;
    TextView text_major5;

    // declaration for textView GE Skills
    TextView text_ge1;
    TextView text_ge2;
    TextView text_ge3;
    TextView text_ge4;
    TextView text_ge5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        hideBottomNavigationView();

        // initialize all textview
        text_major1 = view.findViewById(R.id.faculty_text1);
        text_major2 = view.findViewById(R.id.faculty_text2);
        text_major3 = view.findViewById(R.id.faculty_text3);
        text_major4 = view.findViewById(R.id.faculty_text4);
        text_major5 = view.findViewById(R.id.faculty_text5);

        // initialize all textview
        text_ge1 = view.findViewById(R.id.ge_text1);
        text_ge2 = view.findViewById(R.id.ge_text2);
        text_ge3 = view.findViewById(R.id.ge_text3);
        text_ge4 = view.findViewById(R.id.ge_text4);
        text_ge5 = view.findViewById(R.id.ge_text5);

        btn_back = view.findViewById(R.id.Btn_BackRanking);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack();
            }
        });

        // spinner faculty
        spinnerFaculty = view.findViewById(R.id.spinner_faculty);
        // add value to spinner
        options.add("ระดับสำนักวิชา");
        options.add("ระดับสาขาวิชา");

        Typeface font = ResourcesCompat.getFont(getActivity(), R.font.mitr_regular);
        TagAdapter adapter = new TagAdapter(getActivity(), R.layout.spinner_item, options, font);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFaculty.setAdapter(adapter);

        // Set the default value
        spinnerFaculty.setSelection(options.indexOf("ระดับสาขาวิชา"));

        // create event get value selected
        spinnerFaculty.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedValue = options.get(position);
                // Toast.makeText(getContext(), selectedValue, Toast.LENGTH_SHORT).show();
                // String tag = "TAG";
                // filter data
                filter(selectedValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });

        pullUserData();



        return view;
    }

    private void pullUserData() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("userData").child(FirebaseAuth.getInstance().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                outside(user);
            }

            private void outside(User user) {
                Log.d("CHECK USER", "outside: " + user.getName());

                DatabaseReference facultySkillRef = FirebaseDatabase.getInstance().getReference("allSubject")
                        .child("MajorSkillBranch")
                        .child(user.getMajor());

                Log.d("facultySkillRef", "outside: " + facultySkillRef);

                DatabaseReference branchSkillRef = FirebaseDatabase.getInstance()
                        .getReference("allSubject")
                        .child("MajorSkillBranch")
                        .child(user.getMajor())
                        .child(user.getSubMajor());

                Log.d("branchSkillRef", "outside: " + branchSkillRef);

                DatabaseReference facultyGeRef = FirebaseDatabase.getInstance().getReference("allSubject").child("GeRelax").child(user.getMajor());
                DatabaseReference branchGeRef = facultyGeRef.child(user.getSubMajor());


                // section : Faculty Skill
//                facultySkillRef.addValueEventListener(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (snapshot.exists()) {
//                            //waitForFetch(model);
//                            Log.d("facultySkillRef", "onDataChange: " + snapshot.getValue());
//
////                        String[] item;
//                            ArrayList<String> arr = new ArrayList<>();
//                            for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
//                                String name1 = subjectSnapshot.child("name1").getValue(String.class);
//                                String name2 = subjectSnapshot.child("name2").getValue(String.class);
//                                String name3 = subjectSnapshot.child("name3").getValue(String.class);
//                                String name4 = subjectSnapshot.child("name4").getValue(String.class);
//                                String name5 = subjectSnapshot.child("name5").getValue(String.class);
//
//                            System.out.println(name1);
//                            System.out.println(name2);
//                            System.out.println(name3);
//                            System.out.println(name4);
//                            System.out.println(name5);
//
//                                arr.add(name1);
//                                arr.add(name2);
//                                arr.add(name3);
//                                arr.add(name4);
//                                arr.add(name5);
//                            } // end of for
//
//                            rankingModel = new RankingModelTest();
//                            rankingModel.setRankingModelTests(arr);
//
//                            ArrayList<String> rankingData = new ArrayList<>();
//                            rankingData = rankingModel.getRankingModelTests();
//
//                            ArrayList<ArrayList<String>> sumArr = new ArrayList<>();
//                            sumArr.add(rankingData);
//                            rankingModel.setMajorModel(sumArr);
//
//                            ArrayList<ArrayList<String>> newSumArr = new ArrayList<>();
//                            newSumArr = rankingModel.getMajorModel();
//
//                            // unit test fetch data is great result
//                            System.out.println("HERE IS A RANKING MODEL");
//
//                        for (int i=0; i< newSumArr.size(); i++) {
//                            System.out.println(newSumArr.get(i).toString());
//                        }
//
////                            // find a top 5 subject high score
////                            Map<String, Integer> subjectCounts = new HashMap<>();
////                            for (String subject : rankingData) {
////                                int count = subjectCounts.getOrDefault(subject, 0);
////                                subjectCounts.put(subject, count + 1);
////                            }
////
////                            // Get the top 5 subjects with the highest counts
////                            List<Map.Entry<String, Integer>> sortedSubjects = new ArrayList<>(subjectCounts.entrySet());
////                            Collections.sort(sortedSubjects, new Comparator<Map.Entry<String, Integer>>() {
////                                @Override
////                                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
////                                    return o2.getValue().compareTo(o1.getValue());
////                                }
////                            });
////                            for (int i = 0; i < Math.min(5, sortedSubjects.size()); i++) {
////                                Map.Entry<String, Integer> subject = sortedSubjects.get(i);
////                                System.out.println(subject.getKey() + ": " + subject.getValue());
////
////                                /** Importanting !!! This is a place for set faculty skill text */
////                                if (i==0)
////                                    text_major1.setText(subject.getKey());
////                                if (i==1)
////                                    text_major2.setText(subject.getKey());
////                                if (i==2)
////                                    text_major3.setText(subject.getKey());
////                                if (i==3)
////                                    text_major4.setText(subject.getKey());
////                                if (i==4)
////                                    text_major5.setText(subject.getKey());
////                            }
//                        } // end if
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("facultySkillRef", "onCancelled: " + error);
//                    }
//                });

                // section : Branch Skill
                branchSkillRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //waitForFetch(model);
                            Log.d("branchSkillRef", "onDataChange: " + snapshot.getValue());

//                        String[] item;
                            ArrayList<String> arr = new ArrayList<>();
                            for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                                String name1 = subjectSnapshot.child("name1").getValue(String.class);
                                String name2 = subjectSnapshot.child("name2").getValue(String.class);
                                String name3 = subjectSnapshot.child("name3").getValue(String.class);
                                String name4 = subjectSnapshot.child("name4").getValue(String.class);
                                String name5 = subjectSnapshot.child("name5").getValue(String.class);

//                            System.out.println(name1);
//                            System.out.println(name2);
//                            System.out.println(name3);
//                            System.out.println(name4);
//                            System.out.println(name5);

                                arr.add(name1);
                                arr.add(name2);
                                arr.add(name3);
                                arr.add(name4);
                                arr.add(name5);
                            } // end of for
//                        rankingModel = new RankingModelTest(name1,name2,name3,name4,name5);
                            rankingModel = new RankingModelTest ();
                            rankingModel.setRankingModelTests(arr);

                            // unit test fetch data is great result
                            System.out.println("HERE IS A RANKING MODEL");
                            List<String> rankingData = new ArrayList<>();
                            rankingData = rankingModel.getRankingModelTests();
//                        for (int i=0; i< rankingData.size(); i++) {
//                            System.out.println(rankingData.get(i).toString());
//                        }

                            // find a top 5 subject high score
                            Map<String, Integer> subjectCounts = new HashMap<>();
                            for (String subject : rankingData) {
                                int count = subjectCounts.getOrDefault(subject, 0);
                                subjectCounts.put(subject, count + 1);
                            }

                            // Get the top 5 subjects with the highest counts
                            List<Map.Entry<String, Integer>> sortedSubjects = new ArrayList<>(subjectCounts.entrySet());
                            Collections.sort(sortedSubjects, new Comparator<Map.Entry<String, Integer>>() {
                                @Override
                                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                                    return o2.getValue().compareTo(o1.getValue());
                                }
                            });
                            for (int i = 0; i < Math.min(5, sortedSubjects.size()); i++) {
                                Map.Entry<String, Integer> subject = sortedSubjects.get(i);
                                System.out.println(subject.getKey() + ": " + subject.getValue());

                                /** Importanting !!! This is a place for set faculty skill text */
                                if (i==0)
                                    text_major1.setText(subject.getKey());
                                if (i==1)
                                    text_major2.setText(subject.getKey());
                                if (i==2)
                                    text_major3.setText(subject.getKey());
                                if (i==3)
                                    text_major4.setText(subject.getKey());
                                if (i==4)
                                    text_major5.setText(subject.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("branchSkillRef", "onCancelled: " + error);
                    }
                });

                // section : Branch GE
                branchGeRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            //waitForFetch(model);
                            Log.d("branchSkillRef", "onDataChange: " + snapshot.getValue());

//                        String[] item;
                            ArrayList<String> arr = new ArrayList<>();
                            for (DataSnapshot subjectSnapshot : snapshot.getChildren()) {
                                String name1 = subjectSnapshot.child("name1").getValue(String.class);
                                String name2 = subjectSnapshot.child("name2").getValue(String.class);
                                String name3 = subjectSnapshot.child("name3").getValue(String.class);
                                String name4 = subjectSnapshot.child("name4").getValue(String.class);
                                String name5 = subjectSnapshot.child("name5").getValue(String.class);

//                            System.out.println(name1);
//                            System.out.println(name2);
//                            System.out.println(name3);
//                            System.out.println(name4);
//                            System.out.println(name5);

                                arr.add(name1);
                                arr.add(name2);
                                arr.add(name3);
                                arr.add(name4);
                                arr.add(name5);
                            } // end of for
//                        rankingModel = new RankingModelTest(name1,name2,name3,name4,name5);
                            rankingModel = new RankingModelTest ();
                            rankingModel.setRankingModelTests(arr);

                            // unit test fetch data is great result
                            System.out.println("HERE IS A RANKING MODEL");
                            List<String> rankingData = new ArrayList<>();
                            rankingData = rankingModel.getRankingModelTests();
//                        for (int i=0; i< rankingData.size(); i++) {
//                            System.out.println(rankingData.get(i).toString());
//                        }

                            // find a top 5 subject high score
                            Map<String, Integer> subjectCounts = new HashMap<>();
                            for (String subject : rankingData) {
                                int count = subjectCounts.getOrDefault(subject, 0);
                                subjectCounts.put(subject, count + 1);
                            }

                            // Get the top 5 subjects with the highest counts
                            List<Map.Entry<String, Integer>> sortedSubjects = new ArrayList<>(subjectCounts.entrySet());
                            Collections.sort(sortedSubjects, new Comparator<Map.Entry<String, Integer>>() {
                                @Override
                                public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
                                    return o2.getValue().compareTo(o1.getValue());
                                }
                            });
                            for (int i = 0; i < Math.min(5, sortedSubjects.size()); i++) {
                                Map.Entry<String, Integer> subject = sortedSubjects.get(i);
                                System.out.println(subject.getKey() + ": " + subject.getValue());

                                /** Importanting !!! This is a place for set faculty skill text */
                                if (i==0)
                                    text_ge1.setText(subject.getKey());
                                if (i==1)
                                    text_ge2.setText(subject.getKey());
                                if (i==2)
                                    text_ge3.setText(subject.getKey());
                                if (i==3)
                                    text_ge4.setText(subject.getKey());
                                if (i==4)
                                    text_ge5.setText(subject.getKey());
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("branchSkillRef", "onCancelled: " + error);
                    }
                });



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void waitForFetch(subjectNameModel model) {
        Log.d("waitForFetch", "waitForFetch:1 " + model.getName1());

//        subjectNameModel skillModel;
//        subjectNameModel geModel;
//        if (model1 != null) {
//            skillModel = model1;
//            ArrayList<subjectNameModel> data = new ArrayList<>();
//            data.add(model1);
//            for (int i=0; i<data.size(); i++ ) {
//                Log.d("waitForFetch", "waitForFetch:1 " + model1.getName1());
//                Log.d("waitForFetch", "waitForFetch:2 " + model1.getName2());
//                Log.d("waitForFetch", "waitForFetch:3 " + model1.getName3());
//                Log.d("waitForFetch", "array " + data.get(i).getName1());
//            }
//        }
//        if (model2 != null) {
//            geModel = model2;
//        }


    }

    private void filter(String selectedValue) {
        if (selectedValue.equals("ระดับสำนักวิชา")) {

        } else if (selectedValue.equals("ระดับสาขาวิชา")) {

        }
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