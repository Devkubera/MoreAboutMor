package com.example.moreaboutmoreapp.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moreaboutmoreapp.Adapters.SubjectAdapter;
import com.example.moreaboutmoreapp.Models.SubjectModel;
import com.example.moreaboutmoreapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SubjectListSearchActivity extends AppCompatActivity {

    ImageView btn_back;
    Button btn_addSubject;
    TextView add_subjectBox1;
    EditText add_subjectBox2;
    EditText add_subjectBox3;
    EditText add_subjectBox4;
    EditText add_subjectBox5;

    // use for get item
    List<SubjectModel> items = new ArrayList<>();
    // use for save array list
    List<SubjectModel> subjectModels = new ArrayList<>();
    // use for get selected list value
    Object selectedArray = new ArrayList<>();
    // sharepreference Key
    final String shareprefKey = "subjectData";

    ListView subjectLV;
    BottomSheetDialog bottomSheetDialog;

    List<SubjectModel> suggestions = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_list_search);

        // back to subject list page
        btn_back = findViewById(R.id.Btn_BackAddSubject);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // add subject in first type
        btn_addSubject = findViewById(R.id.Btn_AddSubject);
        btn_addSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // search subject 1
        add_subjectBox1 = findViewById(R.id.subject_addBox1);
        add_subjectBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), SearchNewPageActivity.class);
//                startActivity(intent);

                // set bottom sheet
                bottomSheetDialog = new BottomSheetDialog(SubjectListSearchActivity.this, R.style.BottomSheetDialog);
                View BottomSheetView = LayoutInflater.from(SubjectListSearchActivity.this)
                        .inflate(R.layout.bottom_sheet_search, v.findViewById(R.id.BottomSheetContainerNewSearch));

                // declaration
                subjectLV = (ListView) BottomSheetView.findViewById(R.id.subjectLV);
                SearchView searchBox = BottomSheetView.findViewById(R.id.search_boxs);
                TextView header = BottomSheetView.findViewById(R.id.text_header);
                //Button btn_select = bottomSheetDialog.findViewById(R.id.search_box);

                // set text header
                header.setText("วิชาที่ 1");

                // new code
                ArrayList<SubjectModel> subjectModelArrayList = new ArrayList<>();
                // Create the adapter to convert the array to views
                SubjectAdapter adapter = new SubjectAdapter(getApplicationContext(), subjectModelArrayList);
                // Attach the adapter to a ListView
                subjectLV.setAdapter(adapter);
                adapter.clear();

                items = loadArrayList(shareprefKey);
                // set data to model
                for (SubjectModel data : items) {
                    subjectModels.add(data);
                }
                // add all data
                adapter.addAll(items);

                /** I work to addAll(items) Populating Data into ListView */

                // Or even append an entire new collection
                // Fetching some data, data has now returned
                // If data was JSON, convert to ArrayList of User objects.

                // set adapter
//                subjectLV.setAdapter(new ArrayAdapter<SubjectModel>(getApplicationContext(), R.layout.list_sample_view, subjectModels));

                // make search suggestion function
                searchBox.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        items.clear();
                        adapter.clear();
                        for (SubjectModel data : subjectModels) {
                            // name.lowercase and passcode.lowercase = text.lowercase
                            if (data.getName().toLowerCase().startsWith(newText.toLowerCase()) || data.getPasscode().toLowerCase().startsWith(newText.toLowerCase())) {
                                items.add(data);
                            }
                        }
                        adapter.addAll(items);
                        subjectLV.setAdapter(adapter);
                        return true;
                    }
                });

                // get value from user taps in list view position
                subjectLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //String selectedWord = parent.getAdapter().getItem(position).toString();
                        SubjectModel model = (SubjectModel) parent.getItemAtPosition(position);
                        Toast.makeText(SubjectListSearchActivity.this, "Selected word: " + model.getName(), Toast.LENGTH_SHORT).show();
                        Log.d("SELECTED LIST", "onItemClick: " + model.getName());
                    }
                });

                BottomSheetView.setNestedScrollingEnabled(true);
                bottomSheetDialog.setContentView(BottomSheetView);
                bottomSheetDialog.show();
            }
        });

    }

    private ArrayList<SubjectModel> loadArrayList(String key){
        SharedPreferences prefs = this.getSharedPreferences(key, MODE_PRIVATE);
        Gson gson = new Gson();
        String json = prefs.getString("dataSet", null);
        Type type = new TypeToken<ArrayList<SubjectModel>>() {}.getType();
        return gson.fromJson(json, type);
    }



}