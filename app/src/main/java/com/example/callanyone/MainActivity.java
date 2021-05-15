package com.example.callanyone;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends Activity {

    ListView lstView;
    ArrayList<String> arrayList;
    ArrayAdapter arrayAdapter;
    Button btnCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        init();

    }

    public void init() {

        btnCall = findViewById(R.id.btnCall);
        lstView = findViewById(R.id.lstView);
        arrayList = new ArrayList<>();
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, arrayList);
        lstView.setAdapter(arrayAdapter);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != (PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 100);
        }else{
            readContacts();
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != (PackageManager.PERMISSION_GRANTED)) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE}, 101);
        } else {
            btnClick();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResult) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResult);
        if(arrayList.size()<=0)
            readContacts();
        btnClick();
    }

    public void btnClick() {
        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Random random = new Random();
                if (arrayList.size() > 0) {
                    try {
                        int randomNumber = random.nextInt(arrayList.size());
                        Intent intent;
                        intent = new Intent(Intent.ACTION_CALL);
                        intent.setData(Uri.parse("tel:" + arrayList.get(randomNumber)));
                        startActivity(intent);
                    } catch (SecurityException e) {
                        Toast.makeText(MainActivity.this, "Need Call Permission", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(MainActivity.this, "No SIM Found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private void readContacts() {

        ContentResolver contentResolver = getContentResolver();
        Cursor crName = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (crName.moveToFirst()) {
            do {
                String name = crName.getString(crName.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                getPhoneNumber(name);
            } while (crName.moveToNext());
            arrayAdapter.notifyDataSetChanged();
        }
    }

    public void getPhoneNumber(String name) {
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null, "DISPLAY_NAME = '" + name + "'", null, null);
        if (cursor.moveToFirst()) {
            String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
            while (phones.moveToNext()) {
                String number = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                arrayList.add(number);
            }
            phones.close();
        }
        cursor.close();
    }

    /*Button btnWeb;
    Button btnCall;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnWeb = (Button) findViewById(R.id.btnWeb);
        btnCall = (Button) findViewById(R.id.btnCall);

        btnWeb.setOnClickListener(this);
        btnCall.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnWeb:
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://developer.android.com"));
                startActivity(intent);
                break;

            case R.id.btnCall:
                intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:05422481227"));
                startActivity(intent);
                break;
        }
    }*/
}