package com.example.fproje.ui.label;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.fproje.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class LabelFragment extends Fragment {

    private EditText labelEditText, descriptionEditText;
    private Button addButton;
    private ListView listView;
    private ArrayList<String> labelList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_label, container, false);

        labelEditText = view.findViewById(R.id.label);
        descriptionEditText = view.findViewById(R.id.description);
        addButton = view.findViewById(R.id.btn1);
        listView = view.findViewById(R.id.listView);

        labelList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, labelList);
        listView.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("labels").child(uid);
        }

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addLabel();
            }
        });

        return view;
    }

    private void addLabel() {
        String label = labelEditText.getText().toString().trim();
        String description = descriptionEditText.getText().toString().trim();

        if (!label.isEmpty() && !description.isEmpty() && currentUser != null) {
            // Firebase'e etiketi ekle
            if (databaseReference != null) {
                String key = label; // Etiket ismini anahtar olarak kullan

                // Etiket ve açıklamayı içeren bir Map oluştur
                Map<String, Object> labelData = new HashMap<>();
                labelData.put("label", label);
                labelData.put("description", description);

                // Firebase'e Map'i ekleyin
                databaseReference.child(key).setValue(labelData)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                // Başarıyla eklendi
                                Log.d("LabelFragment", "Etiket Firebase'e başarıyla eklendi");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Hata durumunda
                                Log.e("LabelFragment", "Etiket Firebase'e eklenirken hata oluştu", e);
                            }
                        });
            }

            // Clear the input fields
            labelEditText.setText("");
            descriptionEditText.setText("");
        }
    }

}
