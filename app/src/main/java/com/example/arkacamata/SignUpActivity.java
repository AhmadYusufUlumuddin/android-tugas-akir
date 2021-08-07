package com.example.arkacamata;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.arkacamata.config.Config;
import com.example.arkacamata.config.SharePreference;
import com.example.arkacamata.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    Button btn_daftar;
    RadioGroup radioGroup;
    EditText et_nama, et_email, et_password, et_password_ulangi, et_alamat;
    String jenis_kelamin, id_tb_provinsi, nama_provinsi, id_tb_kabkota, nama_kabkota, id_tb_kecamatan, nama_kecamatan,
            id_tb_kelurahan, nama_kelurahan;
    SharePreference sharePreference;
    SearchableSpinner sp_provinsi, sp_kabkota, sp_kecamatan, sp_kelurahan;
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
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Daftar");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        sharePreference = new SharePreference(this);
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);

        et_nama = findViewById(R.id.et_nama);
        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        et_password_ulangi = findViewById(R.id.et_password_ulangi);
        sp_provinsi = findViewById(R.id.sp_provinsi);
        sp_kabkota = findViewById(R.id.sp_kabkota);
        sp_kecamatan = findViewById(R.id.sp_kecamatan);
        sp_kelurahan = findViewById(R.id.sp_kelurahan);
        et_alamat = findViewById(R.id.et_alamat);
        radioGroup = findViewById(R.id.rg_jenis_kelamin);
        btn_daftar = findViewById(R.id.btn_daftar);
        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                daftar();
            }
        });
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        });

        sp_provinsi.setOnItemSelectedListener(this);
        sp_provinsi.setTitle("Silahkan Pilih");
        sp_provinsi.setPositiveButton("OK");
        sp_kabkota.setOnItemSelectedListener(this);
        sp_kabkota.setTitle("Silahkan Pilih");
        sp_kabkota.setPositiveButton("OK");
        sp_kecamatan.setOnItemSelectedListener(this);
        sp_kecamatan.setTitle("Silahkan Pilih");
        sp_kecamatan.setPositiveButton("OK");
        sp_kelurahan.setOnItemSelectedListener(this);
        sp_kelurahan.setTitle("Silahkan Pilih");
        sp_kelurahan.setPositiveButton("OK");
        getProvinsi();
    }

    private void daftar() {
        et_nama.setError(null);
        et_email.setError(null);
        et_alamat.setError(null);
        et_password.setError(null);
        et_password_ulangi.setError(null);
        String nama = et_nama.getText().toString();
        String email = et_email.getText().toString();
        String alamat = et_alamat.getText().toString();
        String password = et_password.getText().toString();
        String password_ulangi = et_password_ulangi.getText().toString();

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
        }  if (TextUtils.isEmpty(alamat)){
            et_alamat.setError("Silahkann diisi..");
            focusView = et_alamat;
            cancel = true;
        } if (TextUtils.isEmpty(password)){
            et_password.setError("Silahkann diisi..");
            focusView = et_password;
            cancel = true;
        } if (TextUtils.isEmpty(password_ulangi)){
            et_password_ulangi.setError("Silahkann diisi..");
            focusView = et_password_ulangi;
            cancel = true;
        } else if (!password_ulangi.equals(password)) {
            et_password_ulangi.setError("Password tidak sama..");
            focusView = et_password_ulangi;
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
            builder.addFormDataPart("nama",nama);
            builder.addFormDataPart("email",email);
            builder.addFormDataPart("alamat",alamat);
            builder.addFormDataPart("password",password);
            builder.addFormDataPart("jenis_kelamin",jenis_kelamin);
            builder.addFormDataPart("id_tb_provinsi",id_tb_provinsi);
            builder.addFormDataPart("id_tb_kabupaten",id_tb_kabkota);
            builder.addFormDataPart("id_tb_kecamatan",id_tb_kecamatan);
            builder.addFormDataPart("id_tb_kelurahan",id_tb_kelurahan);
            MultipartBody requestBody = builder.build();

            UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
            Call<ResponseBody> post = api.signup(requestBody);
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
                                alertSuccess();
                            } else {
                                Toast.makeText(getApplicationContext(), "Gagal Mendaftar.", Toast.LENGTH_LONG).show();
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

    private void alertSuccess(){
        new AlertDialog.Builder(this)
                .setTitle("Berhasil")
                .setMessage("Silahkan konfirmasi pendaftaran melalui E-mail yang anda masukan.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        onBackPressed();
                    }
                })
                .setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.sp_provinsi){
            id_tb_provinsi = list_provinsi_id.get(position);
            nama_provinsi = list_provinsi_nama.get(position);
            getKabkota();
        } else if (parent.getId() == R.id.sp_kabkota){
            id_tb_kabkota = list_kabkota_id.get(position);
            nama_kabkota = list_kabkota_nama.get(position);
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
                    sp_kabkota.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_dropdown_item, list_kabkota_nama));
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
        builder.addFormDataPart("id_tb_kabupaten",id_tb_kabkota);
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