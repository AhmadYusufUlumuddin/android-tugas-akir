package com.example.arkacamata.main.profil;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.example.arkacamata.config.Config;
import com.example.arkacamata.config.SharePreference;
import com.example.arkacamata.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.arkacamata.R;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfilUbahActivity extends AppCompatActivity implements View.OnClickListener, RadioGroup.OnCheckedChangeListener, AdapterView.OnItemSelectedListener {
    SharePreference sharePreference;
    EditText et_nama, et_email, et_alamat;
    RadioGroup rg_jenis_kelamin;
    Button btn_simpan;
    String id_tb_pengguna, nama, email, jenis_kelamin, alamat, id_tb_provinsi, nama_provinsi, id_tb_kabupaten, nama_kabupaten,
        id_tb_kecamatan, nama_kecamatan, id_tb_kelurahan, nama_kelurahan;
    SearchableSpinner sp_provinsi, sp_kabupaten, sp_kecamatan, sp_kelurahan;
    ArrayList<String> list_provinsi_id = new ArrayList<>();
    ArrayList<String> list_provinsi_nama = new ArrayList<>();
    ArrayList<String> list_kabkota_id = new ArrayList<>();
    ArrayList<String> list_kabkota_nama = new ArrayList<>();
    ArrayList<String> list_kecamatan_id = new ArrayList<>();
    ArrayList<String> list_kecamatan_nama = new ArrayList<>();
    ArrayList<String> list_kelurahan_id = new ArrayList<>();
    ArrayList<String> list_kelurahan_nama = new ArrayList<>();
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profil_ubah);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sharePreference = new SharePreference(this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);

        HashMap<String, String> user = sharePreference.getUserDetails();
        id_tb_pengguna = user.get(sharePreference.KEY_ID_PENGGUNA);
        nama = user.get(sharePreference.KEY_NAMA);
        email = user.get(sharePreference.KEY_EMAIL);
        jenis_kelamin = user.get(sharePreference.KEY_JENIS_KELAMIN);
        alamat = user.get(sharePreference.KEY_ALAMAT);
        id_tb_provinsi = user.get(sharePreference.KEY_ID_PROVINSI);
        nama_provinsi = user.get(sharePreference.KEY_NAMA_PROVINSI);
        id_tb_kabupaten = user.get(sharePreference.KEY_ID_KABUPATEN);
        nama_kabupaten = user.get(sharePreference.KEY_NAMA_KABUPATEN);
        id_tb_kecamatan = user.get(sharePreference.KEY_ID_KECAMATAN);
        nama_kecamatan = user.get(sharePreference.KEY_NAMA_KECAMATAN);
        id_tb_kelurahan = user.get(sharePreference.KEY_ID_KELURAHAN);
        nama_kelurahan = user.get(sharePreference.KEY_NAMA_KELURAHAN);

        getSupportActionBar().setTitle("Profil");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        et_email = findViewById(R.id.et_email);
        et_nama = findViewById(R.id.et_nama);
        et_alamat = findViewById(R.id.et_alamat);
        rg_jenis_kelamin = findViewById(R.id.rg_jenis_kelamin);
        rg_jenis_kelamin.setOnCheckedChangeListener(this);
        sp_provinsi = findViewById(R.id.sp_provinsi);
        sp_kabupaten = findViewById(R.id.sp_kabkota);
        sp_kecamatan = findViewById(R.id.sp_kecamatan);
        sp_kelurahan = findViewById(R.id.sp_kelurahan);
        btn_simpan = findViewById(R.id.btn_simpan);
        btn_simpan.setOnClickListener(this);

        et_email.setText(email);
        et_nama.setText(nama);
        et_alamat.setText(alamat);
        if (jenis_kelamin.equals("Laki-Laki")){
            ((RadioButton)rg_jenis_kelamin.getChildAt(0)).setChecked(true);
        } else {
            ((RadioButton)rg_jenis_kelamin.getChildAt(1)).setChecked(true);
        }
        list_kabkota_id.add(id_tb_kabupaten);
        list_kabkota_nama.add(nama_kabupaten);
        sp_kabupaten.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_kabkota_nama));
        list_kecamatan_id.add(id_tb_kecamatan);
        list_kecamatan_nama.add(nama_kecamatan);
        sp_kecamatan.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_kecamatan_nama));
        list_kelurahan_id.add(id_tb_kelurahan);
        list_kelurahan_nama.add(nama_kelurahan);
        sp_kelurahan.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_kelurahan_nama));

        sp_provinsi.setOnItemSelectedListener(this);
        sp_provinsi.setTitle("Silahkan Pilih");
        sp_provinsi.setPositiveButton("OK");
        sp_kabupaten.setOnItemSelectedListener(this);
        sp_kabupaten.setTitle("Silahkan Pilih");
        sp_kabupaten.setPositiveButton("OK");
        sp_kecamatan.setOnItemSelectedListener(this);
        sp_kecamatan.setTitle("Silahkan Pilih");
        sp_kecamatan.setPositiveButton("OK");
        sp_kelurahan.setOnItemSelectedListener(this);
        sp_kelurahan.setTitle("Silahkan Pilih");
        sp_kelurahan.setPositiveButton("OK");
        getProvinsi();
    }

    @Override
    public void onClick(View v) {
        if (v == btn_simpan){
            simpan();
        }
    }

    private void simpan() {
        et_nama.setError(null);
        et_email.setError(null);
        et_alamat.setError(null);
        nama = et_nama.getText().toString();
        email = et_email.getText().toString();
        alamat = et_alamat.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(nama)){
            et_nama.setError("Silahkann diisi..");
            focusView = et_nama;
            cancel = true;
        } if (TextUtils.isEmpty(email)){
            et_email.setError("Silahkann diisi..");
            focusView = et_email;
            cancel = true;
        } if (TextUtils.isEmpty(alamat)){
            et_alamat.setError("Silahkann diisi..");
            focusView = et_alamat;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            final ProgressDialog pDialog = new ProgressDialog(this);
            pDialog.setMessage("Tunggu sebentar...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();

            MultipartBody.Builder builder = new MultipartBody.Builder();
            builder.setType(MultipartBody.FORM);
            builder.addFormDataPart("id_tb_pengguna",id_tb_pengguna);
            builder.addFormDataPart("nama",nama);
            builder.addFormDataPart("email",email);
            builder.addFormDataPart("jenis_kelamin",jenis_kelamin);
            builder.addFormDataPart("alamat",alamat);
            builder.addFormDataPart("id_tb_provinsi",id_tb_provinsi);
            builder.addFormDataPart("id_tb_kabupaten",id_tb_kabupaten);
            builder.addFormDataPart("id_tb_kecamatan",id_tb_kecamatan);
            builder.addFormDataPart("id_tb_kelurahan",id_tb_kelurahan);
            MultipartBody requestBody = builder.build();

            UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
            Call<ResponseBody> post = api.profil_ubah(requestBody);
            post.enqueue(new Callback<ResponseBody>(){
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    pDialog.dismiss();
                    try {
                        String json = response.body().string();
                        JSONObject jsonObj = new JSONObject(json);
                        Config.jsonArray = jsonObj.getJSONArray("result");
                        for(int i=0;i<Config.jsonArray.length();i++) {
                            JSONObject c = Config.jsonArray.getJSONObject(i);
                            String status = c.getString("status");

                            if (status.equals("1")) {
                                Toast.makeText(getApplicationContext(), "Berhasil Mengubah Profil.", Toast.LENGTH_LONG).show();
                                sharePreference.update(nama, email, jenis_kelamin, alamat, id_tb_provinsi, nama_provinsi,
                                        id_tb_kabupaten, nama_kabupaten, id_tb_kecamatan, nama_kecamatan,
                                        id_tb_kelurahan, nama_kelurahan);
                                onBackPressed();
                            } else {
                                Toast.makeText(getApplicationContext(), "Gagal Mengubah Profil.", Toast.LENGTH_LONG).show();
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    pDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId){
            case R.id.rb_laki2:
                jenis_kelamin = "Laki-Laki";
                break;
            case R.id.rb_perempuan:
                jenis_kelamin = "Perempuan";
                break;
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_provinsi){
            id_tb_provinsi = list_provinsi_id.get(position);
            nama_provinsi = list_provinsi_nama.get(position);
            getKabkota();
        } else if (parent.getId() == R.id.sp_kabkota){
            id_tb_kabupaten = list_kabkota_id.get(position);
            nama_kabupaten = list_kabkota_nama.get(position);
            getKecamatan();
        } else if (parent.getId() == R.id.sp_kecamatan){
            id_tb_kecamatan = list_kecamatan_id.get(position);
            nama_kecamatan = list_kecamatan_nama.get(position);
            getKelurahan();
        } else if (parent.getId() == R.id.sp_kelurahan){
            id_tb_kelurahan = list_kelurahan_id.get(position);
            nama_kelurahan = list_kelurahan_nama.get(position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getProvinsi(){
        pDialog.show();
        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.provinsi();
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray("result");

                    for(int i=0;i<Config.jsonArray.length();i++) {
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        String id  = c.getString("id_tb_provinsi");
                        String nama  = c.getString("nama_provinsi");

                        list_provinsi_id.add(id);
                        list_provinsi_nama.add(nama);
                    }
                    sp_provinsi.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_provinsi_nama));
                    for (int x = 0; x<list_provinsi_id.size();x++){
                        if (list_provinsi_nama.get(x).toLowerCase().equals(nama_provinsi.toLowerCase())){
                            sp_provinsi.setSelection(x);
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getKabkota(){
        pDialog.show();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_provinsi",id_tb_provinsi);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.kabkota(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    list_kabkota_id.clear();
                    list_kabkota_nama.clear();
                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray("result");

                    for(int i=0;i<Config.jsonArray.length();i++) {
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        String id  = c.getString("id_tb_kabupaten");
                        String nama  = c.getString("nama_kabupaten");

                        list_kabkota_id.add(id);
                        list_kabkota_nama.add(nama);
                    }
                    sp_kabupaten.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_kabkota_nama));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getKecamatan(){
        pDialog.show();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_kabupaten",id_tb_kabupaten);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.kecamatan(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    list_kecamatan_id.clear();
                    list_kecamatan_nama.clear();
                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray("result");
                    for(int i=0;i<Config.jsonArray.length();i++) {
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        String id  = c.getString("id_tb_kecamatan");
                        String nama  = c.getString("nama_kecamatan");

                        list_kecamatan_id.add(id);
                        list_kecamatan_nama.add(nama);
                    }
                    sp_kecamatan.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_kecamatan_nama));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void getKelurahan(){
        pDialog.show();
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_kecamatan",id_tb_kecamatan);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.kelurahan(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    list_kelurahan_id.clear();
                    list_kelurahan_nama.clear();
                    String json = response.body().string();
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray("result");
                    for(int i=0;i<Config.jsonArray.length();i++) {
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        String id  = c.getString("id_tb_kelurahan");
                        String nama  = c.getString("nama_kelurahan");

                        list_kelurahan_id.add(id);
                        list_kelurahan_nama.add(nama);
                    }
                    sp_kelurahan.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_kelurahan_nama));
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(getApplicationContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
