package com.example.fproje.ui.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.fproje.R;
import com.example.fproje.databinding.ActivityMainBinding;
import com.example.fproje.ui.gallery.GalleryFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class PhotoFragment extends Fragment {

    public class User {
        private String username;
        private String email;

        public User() {
            // Firebase için boş kurucu metod
        }

        public User(String username, String email) {
            this.username = username;
            this.email = email;
        }

        public String getUsername() {
            return username;
        }

        public String getEmail() {
            return email;
        }
    }

    private ListView listView;
    private static ArrayList<String> labelList;
    private ArrayAdapter<String> adapter;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;
    private ImageView imageView;
    private Button btnGallery;
    private Button btnAdd;
    private ArrayList<CheckBox> checkBoxList;
    private ActivityResultLauncher<String> mGetContent;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);

        listView = view.findViewById(R.id.plistView);
        labelList = new ArrayList<>();
        adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, labelList);
        listView.setAdapter(adapter);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid();
            databaseReference = FirebaseDatabase.getInstance().getReference("labels").child(uid);
            retrieveLabelsFromFirebase();
        }

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                // Seçilen resmi işleme koymak için gerekli kodu ekleyin
                imageView.setImageURI(uri);
            }
        });

        btnGallery = view.findViewById(R.id.btn1);
        btnGallery.setOnClickListener(v -> mGetContent.launch("image/*"));

        imageView = view.findViewById(R.id.imageView);
        btnAdd = view.findViewById(R.id.btn2);
        checkBoxList = new ArrayList<>();

        btnAdd.setOnClickListener(v -> saveToFirebase());

        return view;
    }

    private void retrieveLabelsFromFirebase() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    labelList.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        // Her bir etiket düğümü için 'label' ve 'description' değerlerini çek
                        String label = snapshot.child("label").getValue(String.class);

                        // Etiketi listeye ekle
                        labelList.add(label);
                    }
                    addCheckboxesToLabels();
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Hataları ele alın
                }
            });
        }
    }


    private void addCheckboxesToLabels() {
        // CheckBox listesini temizle
        checkBoxList.clear();

        // CheckBox'ları içeren HashMap listesini oluştur
        ArrayList<HashMap<String, Object>> listData = new ArrayList<>();

        for (String label : labelList) {
            // CheckBox oluştur
            CheckBox checkBox = new CheckBox(requireContext());
            checkBox.setText(label);
            checkBoxList.add(checkBox); // Oluşturulan CheckBox'ı listeye ekle

            // CheckBox'ı HashMap içine ekle
            HashMap<String, Object> listItem = new HashMap<>();
            listItem.put("label", label);
            listItem.put("checkBox", checkBox);
            listData.add(listItem);
        }

        // CheckBox'ları ListView içine eklemek için özel bir adapter kullan
        String[] from = {"label"};
        int[] to = {R.id.labelTextView};

        SimpleAdapter adapter = new SimpleAdapter(requireContext(), listData, R.layout.list_item_layout, from, to) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = super.getView(position, convertView, parent);

                // CheckBox'ı ListView içine yerleştir
                CheckBox checkBox = view.findViewById(R.id.checkBox);
                checkBox.setChecked(checkBoxList.get(position).isChecked());
                checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> checkBoxList.get(position).setChecked(isChecked));

                return view;
            }
        };

        // ListView'a adapter'ı set et
        listView.setAdapter(adapter);
    }

    private void saveToFirebase() {
        if (databaseReference == null) {
            // databaseReference null ise, hata mesajını göster ve metodu sonlandır
            Toast.makeText(requireContext(), "Veritabanı referansı bulunamadı", Toast.LENGTH_SHORT).show();
            return;
        }

        String imageUri = getImageUri();
        String selectedLabel = getSelectedLabel();

        if (!imageUri.isEmpty() && !selectedLabel.isEmpty()) {
            // Firebase etiket referansını oluştur
            DatabaseReference labelRef = databaseReference.child(selectedLabel).child("photos");

            // Rastgele bir anahtar al
            String key = labelRef.push().getKey();

            // Kullanıcı bilgilerini oluştur
            User user = new User(currentUser.getDisplayName(), currentUser.getEmail());

            // Fotoğraf bilgilerini oluştur
            Map<String, Object> imageData = new HashMap<>();
            imageData.put("imageUri", imageUri);

            // Kullanıcı ve fotoğraf bilgilerini birleştir
            Map<String, Object> photoData = new HashMap<>();
            photoData.put("imageUri", imageUri);  // Doğru anahtarı kullanmaya dikkat edin

            // Kullanıcı ve fotoğraf bilgilerini birleştir
            Map<String, Object> combinedData = new HashMap<>();
            combinedData.put("user", user);
            combinedData.put("photo", photoData);  // Doğru anahtarı kullanmaya dikkat edin

            labelRef.child(key).setValue(combinedData, (databaseError, databaseReference) -> {
                if (databaseError == null) {
                    // Başarılı bir şekilde kaydedildi
                    Toast.makeText(requireContext(), "Veritabanına kaydedildi", Toast.LENGTH_SHORT).show();
                } else {
                    // Hata oluştu
                    Toast.makeText(requireContext(), "Kayıt hatası: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(requireContext(), "Resim veya etiket seçilmedi", Toast.LENGTH_SHORT).show();
        }
    }



    private String getSelectedLabel() {
        for (CheckBox checkBox : checkBoxList) {
            if (checkBox.isChecked()) {
                return checkBox.getText().toString();
            }
        }
        return "";
    }

    private String getImageUri() {
        // Bu metodu, seçilen resmin URI'sini döndürecek şekilde uygulamaya göre düzenleyin
        Uri imageUri = getImageUriFromImageView();
        return imageUri != null ? imageUri.toString() : "";
    }

    private Uri getImageUriFromImageView() {
        // ImageView'a atanmış olan resmi al
        Drawable drawable = imageView.getDrawable();

        if (drawable instanceof BitmapDrawable) {
            // Eğer resim bir BitmapDrawable ise, Bitmap nesnesini al
            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();

            // Bitmap'i MediaStore.Images.Media insert ederek bir URI elde et
            String imageUriString = MediaStore.Images.Media.insertImage(
                    requireActivity().getContentResolver(),
                    bitmap,
                    "Title",
                    "Description"
            );

            // URI'yi parse et ve döndür
            return Uri.parse(imageUriString);
        }

        // Eğer resim bir BitmapDrawable değilse, null döndür
        return null;
    }
}
