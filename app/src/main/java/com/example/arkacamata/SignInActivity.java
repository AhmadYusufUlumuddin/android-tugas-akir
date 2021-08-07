package com.example.arkacamata;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.example.arkacamata.config.Config;
import com.example.arkacamata.config.SharePreference;
import com.example.arkacamata.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignInActivity extends AppCompatActivity {
    Button btn_masuk, btn_daftar;
    EditText et_email, et_password;
    SharePreference sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setTitle("Masuk");
        sharedPreferences = new SharePreference(this);
        if (sharedPreferences.isLoggedIn() == true) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        et_email = findViewById(R.id.et_email);
        et_password = findViewById(R.id.et_password);
        btn_masuk = findViewById(R.id.btn_masuk);
        btn_daftar = findViewById(R.id.btn_daftar);
        btn_masuk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        btn_daftar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this,SignUpActivity.class));
            }
        });
        requestPermissions();
    }

    private void requestPermissions(){
        Dexter.withActivity(this).withPermissions(
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA
        ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
                createDirectory();
            }
            @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {}
        }).check();
    }

    private void createDirectory(){
        final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Config.IMAGE_DIRECTORY_NAME);
        if (!mediaStorageDir.exists()) {
            Log.d("catatan", String.valueOf(mediaStorageDir.mkdirs()));
        }
    }

    private void login(){
        et_email.setError(null);
        et_password.setError(null);
        String email = et_email.getText().toString();
        String password = et_password.getText().toString();
        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(email)){
            et_email.setError("Silahkann diisi..");
            focusView = et_email;
            cancel = true;
        } else if (TextUtils.isEmpty(password)){
            et_password.setError("Silahkann diisi..");
            focusView = et_password;
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
            builder.addFormDataPart("email",email);
            builder.addFormDataPart("password",password);
            MultipartBody requestBody = builder.build();

            UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
            Call<ResponseBody> post = api.signin(requestBody);
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
                                String status_konfirmasi      = c.getString("status_konfirmasi");
                                if (status_konfirmasi.equals("SUDAH DIKONFIRMASI")){
                                    String id      = c.getString("id_tb_pengguna");
                                    String nama    = c.getString("nama");
                                    String email   = c.getString("email");
                                    String jenis_kelamin     = c.getString("jenis_kelamin");
                                    String alamat     = c.getString("alamat");
                                    String id_provinsi     = c.getString("id_tb_provinsi");
                                    String nama_provinsi     = c.getString("nama_provinsi");
                                    String id_kabupaten     = c.getString("id_tb_kabupaten");
                                    String nama_kabupaten     = c.getString("nama_kabupaten");
                                    String id_kecamatan     = c.getString("id_tb_kecamatan");
                                    String nama_kecamatan     = c.getString("nama_kecamatan");
                                    String id_kelurahan     = c.getString("id_tb_kelurahan");
                                    String nama_kelurahan     = c.getString("nama_kelurahan");

                                    sharedPreferences.create_session(id, nama, email, alamat, jenis_kelamin, id_provinsi, nama_provinsi,
                                            id_kabupaten, nama_kabupaten, id_kecamatan, nama_kecamatan, id_kelurahan, nama_kelurahan);
                                    Intent intent = new Intent(SignInActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    SignInActivity.this.finish();
                                } else {
                                    alertKonfirmasi();
                                }
                            } else {
                                Toast.makeText(getApplicationContext(), "Username dan password salah.", Toast.LENGTH_LONG).show();
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

    private void alertKonfirmasi() {
        new AlertDialog.Builder(this)
                .setTitle("Gagal")
                .setMessage("Silahkan konfirmasi pendaftaran melalui di E-mail yang anda masukan saat pendaftaran.")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
    }
}
