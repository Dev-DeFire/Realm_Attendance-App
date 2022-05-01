package com.nishantnitinmishra.realmattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.nishantnitinmishra.realmattendance.Adapters.ClassListAdapter;
import com.nishantnitinmishra.realmattendance.realm.Class_Names;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmAsyncTask;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    Realm realm;
    RealmAsyncTask transaction;

    private  String position_bg = "0";

    @SuppressLint("UseCompatLoadingForDrawables")

    BottomAppBar bottomAppBar;
    FloatingActionButton fab_main;
    RecyclerView recyclerView;
    TextView sample;

    ClassListAdapter mAdapter;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Realm.init(this);

        getWindow().setEnterTransition(null);

        bottomAppBar = findViewById(R.id.bottomAppBar);
        fab_main = findViewById(R.id.fab_main);
        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialogBox();
            }
        });

        realm = Realm.getDefaultInstance();

        RealmResults<Class_Names> results;

        results = realm.where(Class_Names.class)
                .findAll();


        sample = findViewById(R.id.classes_sample);
        recyclerView = findViewById(R.id.recyclerView_main);

        recyclerView.setHasFixedSize(true);
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);

        recyclerView.setLayoutManager(staggeredGridLayoutManager);

        mAdapter = new ClassListAdapter( results,MainActivity.this);
        recyclerView.setAdapter(mAdapter);


    }
    private void showDialogBox() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.class_dialog,null);
        builder.setView(view);
        final AlertDialog dialog=builder.create();
        dialog.show();
        final EditText class_edit=view.findViewById(R.id.className);
        final EditText sub_edit= view.findViewById(R.id.subName);
        TextView cancel = view.findViewById(R.id.cancel_btn_popup);
        TextView add = view.findViewById(R.id.add_btn_popup);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String className = class_edit.getText().toString();
                String subjectName = sub_edit.getText().toString();
                if(className.isEmpty()||subjectName.isEmpty()){
                    Toast.makeText(MainActivity.this,"Error ! Fields can't be empty !",Toast.LENGTH_LONG).show();
                }
                else{
                    addClass(className,subjectName);
                    dialog.dismiss();
                }

            }
        } );
    }

    private void addClass(final String cname, final String sname) {

        Random rand = new Random();
        int randomNum = rand.nextInt((5 - 0) + 1) + 0;
        position_bg=Integer.toString(randomNum);
        final ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
        progressDialog.setMessage("Creating class..");
        progressDialog.show();

        transaction = realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Class_Names class_name = realm.createObject(Class_Names.class);
                String id = cname + sname;
                class_name.setId(id);
                class_name.setName_class(cname);
                class_name.setName_subject(sname);
                class_name.setPosition_bg(position_bg);
            }
        }, new Realm.Transaction.OnSuccess() {
            @Override
            public void onSuccess() {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Successfully created", Toast.LENGTH_SHORT).show();
            }
        }, new Realm.Transaction.OnError() {
            @Override
            public void onError(Throwable error) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Error!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        realm.refresh();
        realm.setAutoRefresh(true);
        super.onResume();
    }
}