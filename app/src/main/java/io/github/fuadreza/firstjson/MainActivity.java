package io.github.fuadreza.firstjson;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getSimpleName();

    private ListView list;

    private ProgressDialog pDialog;

    // URL to get contacts JSON
    private static String url = "https://api.androidhive.info/contacts/";

    ArrayList<HashMap<String, String>> kontakList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        kontakList = new ArrayList<>();

        list = (ListView) findViewById(R.id.listKontak);

        new GetKontak().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     */
    private class GetKontak extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //Menampilkan progres dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Tunggu bentarr..");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            HttpHandler sh = new HttpHandler();

            // Making a request to url and getting response
            String jsonStr = sh.buatManggilService(url);

            Log.e(TAG, "Response dari URL : " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    //Ambil data
                    JSONArray kontaks = jsonObj.getJSONArray("contacts");

                    //Loop ke semmua kontak
                    for (int i = 0; i < kontaks.length(); i++) {
                        JSONObject k = kontaks.getJSONObject(i);

                        String id = k.getString("id");
                        String nama = k.getString("name");
                        String email = k.getString("email");
                        String alamat = k.getString("address");
                        String jenis_kelamin = k.getString("gender");

                        //Phone pake node, jadinya harus di dibuat Objek untuk ngambil dalamnya

                        JSONObject telpon = k.getJSONObject("phone");
                        String mobile = telpon.getString("mobile");
                        String home = telpon.getString("home");
                        String office = telpon.getString("office");

                        //tmp Hash Map untuk setiap kontak
                        HashMap<String, String> kontak = new HashMap<>();

                        //masukan node child ke HashMap key => value
                        kontak.put("id", id);
                        kontak.put("nama", nama);
                        kontak.put("email", email);
                        kontak.put("mobile", mobile);
                        kontak.put("alamat", alamat);

                        kontakList.add(kontak);
                    }
                } catch (final JSONException er) {
                    Log.e(TAG, "JSON Parsing error : " + er.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, "JSON Parsing error : " + er.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Cannot get json from server");
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "Tidak bisa mendapatkan JSON dari server, coba cek LogCat", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

            //Tutup progress dialog
            if (pDialog.isShowing()) {
                pDialog.dismiss();

                /**
                 * Update parse JSON ke RecyclerView
                 */
                ListAdapter adapter = new SimpleAdapter(
                        MainActivity.this, kontakList,
                        R.layout.list_item_kontak, new String[]{"nama", "email", "mobile", "alamat"},
                        new int[]{R.id.nama, R.id.email, R.id.nomor, R.id.alamat}
                );

                list.setAdapter(adapter);
            }
        }
    }
}
