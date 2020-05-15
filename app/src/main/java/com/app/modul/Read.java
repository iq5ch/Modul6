package com.app.modul;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Read extends AppCompatActivity implements ListView.OnItemClickListener {

    private ListView listView;
    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        listView = (ListView) findViewById(R.id.listView);
        listView.setOnItemClickListener(this);
        getJSON();
    }

    private void showMahasiswa () {
        JSONObject jsonObject = null;
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString(Konfigurasi.TAG_ID);
                String nama = jo.getString(Konfigurasi.TAG_NAMA);

                HashMap<String, String> mhs = new HashMap<>();
                mhs.put(Konfigurasi.TAG_ID, id);
                mhs.put(Konfigurasi.TAG_NAMA, nama);
                list.add(mhs);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                Read.this, list, R.layout.list_item,
                new String[]{Konfigurasi.TAG_ID, Konfigurasi.TAG_NAMA},
                new int[]{R.id.id, R.id.nama});
        listView.setAdapter(adapter);
    }

    private void getJSON () {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute () {
                super.onPreExecute();
                loading = ProgressDialog.show(Read.this, "Mangambil data...", "Mohon tunggu...", false, false);
            }

            @Override
            protected void onPostExecute (String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                showMahasiswa();
            }

            @Override
            protected String doInBackground (Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Konfigurasi.URL_GET_ALL);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int positon, long id) {
        Intent intent = new Intent(this, Select.class);
        HashMap<String, String> map = (HashMap) parent.getItemAtPosition(positon);
        String mhsId = map.get(Konfigurasi.TAG_ID).toString();
        intent.putExtra(Konfigurasi.MHS_ID, mhsId);
        startActivity(intent);
    }

}
