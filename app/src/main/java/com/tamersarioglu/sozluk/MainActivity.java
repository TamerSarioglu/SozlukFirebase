package com.tamersarioglu.sozluk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private Toolbar toolbar;
    private RecyclerView recyclerView;
    private ArrayList<Kelimeler> kelimelerListe;
    private KelimelerAdapter adapter;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("kelimeler");

        toolbar.setTitle("Sözlük Uygulaması");
        setSupportActionBar(toolbar);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        kelimelerListe = new ArrayList<>();
        adapter = new KelimelerAdapter(this, kelimelerListe);
        recyclerView.setAdapter(adapter);
        tumKelimeleriGetir();

    }

    private void init() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerView);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.arama_menu, menu);

        MenuItem item = menu.findItem(R.id.action_ara);
        SearchView searchView = (SearchView) item.getActionView();
        searchView.setOnQueryTextListener(this);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        Log.e("gönderilen Arama", query);
        tumKelimeleriAra(query);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        Log.e("harf girdikçe Arama", newText);
        tumKelimeleriAra(newText);
        return false;
    }

    public void tumKelimeleriGetir(){

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                kelimelerListe.clear();

                for (DataSnapshot d: snapshot.getChildren()){
                    Kelimeler kelime = d.getValue(Kelimeler.class);
                    kelime.setKelime_id(d.getKey());

                    kelimelerListe.add(kelime);
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void tumKelimeleriAra(final String arananKelime){

        myRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                kelimelerListe.clear();

                for (DataSnapshot d: snapshot.getChildren()){
                    Kelimeler kelime = d.getValue(Kelimeler.class);

                    if (kelime.getIngilizce().contains(arananKelime)){
                        kelime.setKelime_id(d.getKey());
                        kelimelerListe.add(kelime);
                    }
                }

                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}