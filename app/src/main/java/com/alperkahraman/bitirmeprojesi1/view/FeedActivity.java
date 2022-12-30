package com.alperkahraman.bitirmeprojesi1.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.alperkahraman.bitirmeprojesi1.R;
import com.alperkahraman.bitirmeprojesi1.adapter.PostAdapter;
import com.alperkahraman.bitirmeprojesi1.databinding.ActivityFeedBinding;
import com.alperkahraman.bitirmeprojesi1.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore firebaseFirestore;
    ArrayList<Post> postArrayList;
    private ActivityFeedBinding binding;
    PostAdapter postAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        postArrayList = new ArrayList<>();

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();



        getData();

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        postAdapter = new PostAdapter(postArrayList);
        binding.recyclerView.setAdapter(postAdapter);


    }


    private void getData(){

        firebaseFirestore.collection("Posts").orderBy("date", Query.Direction.DESCENDING).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
              if (error != null){

                  Toast.makeText(FeedActivity.this,error.getLocalizedMessage(),Toast.LENGTH_LONG).show();
              }
              if (value != null) {

                  for (DocumentSnapshot snapshot : value.getDocuments()) {

                      Map<String, Object> data = snapshot.getData();

                      String userEmail = (String) data.get("useremail");
                      String comment = (String) data.get("comment");
                      String downloadUrl = (String) data.get("downloadurl");

                      Post post = new Post(userEmail, comment, downloadUrl);
                      postArrayList.add(post);
                  }


                  postAdapter.notifyDataSetChanged();


              }


            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();        // GİRİŞ YAPTIKTAN SONRA OLUŞAN EKRANDA SAĞ ÜSTTEKİ OPTİONS KISMINI BAĞLIYORUZ.
        menuInflater.inflate(R.menu.option_menu,menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.add_post){ // UPLOAD ACTİVİTY E GİDECEK.       // BAĞLANAN OPTİONS KISMINDAKİ İTEMLER NE YAPACAK ? ONLARI YAZIYORUZ.
            Intent intentToUpload = new Intent(FeedActivity.this,UploadActivity.class);
            startActivity(intentToUpload);

        }
        else if(item.getItemId() == R.id.signout){ // ÇIKIŞ YAPACAK.
            auth.signOut(); // Çıkış yapıldığını firebase'e belirtmek için.

             Intent intentToMain = new Intent(FeedActivity.this,MainActivity.class);
             startActivity(intentToMain);
             finish();

        }
        return super.onOptionsItemSelected(item);


    }
}