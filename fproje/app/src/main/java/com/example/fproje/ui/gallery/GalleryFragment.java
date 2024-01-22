package com.example.fproje.ui.gallery;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.fproje.R;
import com.example.fproje.databinding.FragmentGalleryBinding;
import com.example.fproje.ui.adapter.MyAdapter;
import com.google.firebase.Firebase;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class GalleryFragment extends Fragment {

    private FragmentGalleryBinding binding;
    private MyAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentGalleryBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        adapter = new MyAdapter(requireContext(), new ArrayList<>());
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        getDataFromFirebase();

        return root;
    }

    private void getDataFromFirebase() {
        DatabaseReference photosReference = FirebaseDatabase.getInstance().getReference().child("photos");
        photosReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    ArrayList<String> dataList = new ArrayList<>();

                    for (DataSnapshot photoSnapshot : dataSnapshot.getChildren()) {
                        String imageUri = photoSnapshot.child("imageUri").getValue(String.class);
                        String email = photoSnapshot.child("user").child("email").getValue(String.class);

                        ArrayList<String> labelList = new ArrayList<>();

                        for (DataSnapshot labelSnapshot : photoSnapshot.child("labels").getChildren()) {
                            String label = labelSnapshot.getValue(String.class);
                            labelList.add(label);
                        }
                        String formattedLabels = formatLabels(labelList);
                        String data = "Image Url: " + (imageUri != null ? imageUri : "") +
                                "\nLabels: " + formattedLabels +
                                "\n User Email: " + (email != null ? email : "");

                        dataList.add(data);
                    }
                    adapter.setData(dataList);
                } else {
                    Toast.makeText(requireContext(), "Veriler Alınamadı!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Veri çekme işlemi iptal edildi.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String formatLabels(ArrayList<String>labelList){
        if(labelList==null || labelList.isEmpty()){
            return "";
        }
        StringBuilder formattedLabels=new StringBuilder();
        for(String label: labelList){
            formattedLabels.append(label).append(",");
        }
        return formattedLabels.substring(0,formattedLabels.length()-2);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}