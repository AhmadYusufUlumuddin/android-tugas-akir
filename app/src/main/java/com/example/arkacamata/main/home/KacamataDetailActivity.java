package com.example.arkacamata.main.home;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.arkacamata.config.Config;
import com.example.arkacamata.config.Download;
import com.example.arkacamata.config.DownloadService;
import com.example.arkacamata.config.SharePreference;
import com.example.arkacamata.config.UserAPIServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.arkacamata.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class KacamataDetailActivity extends AppCompatActivity implements View.OnClickListener {
    String id_tb_pengguna, id_tb_kacamata, nama_kacamata, harga_kacamata, nama_kategori, deskripsi_kacamata,
            file_3d = null, foto_kacamata, status_favorit;
    ImageView imv;
    TextView tv_nama_kacamata, tv_harga_kacamata, tv_kategori_kacamata, tv_deskripsi_kacamata;
    Button btn_lihat, btn_favorit, btn_download;
    LinearLayout ln_file, ln_download;
    public static final String MESSAGE_PROGRESS = "message_progress";
    static final int PERMISSION_REQUEST_CODE = 1;
    ProgressBar mProgressBar;
    TextView mProgressText;
    SharePreference sharePreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kacamata_detail);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle b = getIntent().getExtras();
        id_tb_kacamata = b.getString("id_tb_kacamata");

        getSupportActionBar().setTitle("Detail Kacamata");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        sharePreference = new SharePreference(this);
        HashMap<String, String> user = sharePreference.getUserDetails();
        id_tb_pengguna = user.get(sharePreference.KEY_ID_PENGGUNA);

        tv_nama_kacamata = findViewById(R.id.tv_nama_kacamata);
        tv_harga_kacamata = findViewById(R.id.tv_harga_kacamata);
        tv_kategori_kacamata = findViewById(R.id.tv_kategori_kacamata);
        tv_deskripsi_kacamata = findViewById(R.id.tv_deskripsi_kacamata);
        mProgressBar = findViewById(R.id.progress);
        mProgressText = findViewById(R.id.progress_text);

        ln_file = findViewById(R.id.ln_file);
        ln_download = findViewById(R.id.ln_download);
        imv = findViewById(R.id.imv);
        btn_favorit = findViewById(R.id.btn_favorit);
        btn_lihat = findViewById(R.id.btn_lihat);
        btn_download = findViewById(R.id.btn_download);
        btn_favorit.setOnClickListener(this);
        btn_lihat.setOnClickListener(this);
        btn_download.setOnClickListener(this);

        registerReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_kacamata",id_tb_kacamata);
        builder.addFormDataPart("id_tb_pengguna",id_tb_pengguna);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.kacamata_detail(requestBody);
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
                        nama_kacamata    = c.getString("nama_kacamata");
                        harga_kacamata    = c.getString("harga_kacamata");
                        nama_kategori   = c.getString("nama_kategori");
                        deskripsi_kacamata   = c.getString("deskripsi_kacamata");
                        file_3d   = c.getString("file_3d");
                        foto_kacamata   = c.getString("foto_kacamata");

                        tv_nama_kacamata.setText(nama_kacamata);
                        tv_harga_kacamata.setText(harga_kacamata);
                        tv_kategori_kacamata.setText(nama_kategori);
                        tv_deskripsi_kacamata.setText(deskripsi_kacamata);

                        Glide.with(KacamataDetailActivity.this)
                                .load(Config.URL_FOTO_KACAMATA+foto_kacamata)
                                .thumbnail(0.5f)
                                .into(imv);

                        setDownloadInvoice();
                    }

                    JSONObject c = jsonObj.getJSONObject("result2");
                    int total = c.getInt("status");
                    if (total>0){
                        btn_favorit.setText("Hapus Favorit");
                        status_favorit = "HAPUS";
                    } else {
                        btn_favorit.setText("Tambah Favorit");
                        status_favorit = "TAMBAH";
                    }
//                    for(int i=0;i<Config.jsonArray2.length();i++) {
//                        JSONObject c = Config.jsonArray2.getJSONObject(i);
//                        int total = c.getInt("status");
//                        if (total>0){
//                            btn_favorit.setText("Hapus Dari Favorit");
//                            btn_favorit.setEnabled(false);
//                        } else {
//                            btn_favorit.setText("Tambahkan Ke Favorit");
//                            btn_favorit.setEnabled(true);
//                        }
//                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(KacamataDetailActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(KacamataDetailActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(KacamataDetailActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == btn_favorit){
            kirimFavorit();
        } else if (v == btn_lihat){
            Intent intent = new Intent(this, KacamataARActivity.class);
            intent.putExtra("file_3d",file_3d);
            startActivity(intent);
        } else if (v == btn_download){
            if(checkPermission()){
                startDownload();
            } else {
                requestPermission();
            }
        }
    }

    private void kirimFavorit() {
        final ProgressDialog pDialog = new ProgressDialog(this);
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_kacamata",id_tb_kacamata);
        builder.addFormDataPart("id_tb_pengguna",id_tb_pengguna);
        builder.addFormDataPart("status_favorit",status_favorit);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.kirim_favorit(requestBody);
        post.enqueue(new Callback<ResponseBody>(){
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                pDialog.dismiss();
                try {
                    String json = response.body().string();
                    Log.d("catatan",json);
                    JSONObject jsonObj = new JSONObject(json);
                    Config.jsonArray = jsonObj.getJSONArray("result");
                    for(int i=0;i<Config.jsonArray.length();i++) {
                        JSONObject c = Config.jsonArray.getJSONObject(i);
                        String status    = c.getString("status");
                        if (status.equals("1")){
                            if (status_favorit.equals("TAMBAH")){
                                status_favorit = "HAPUS";
                                btn_favorit.setText("Hapus Favorit");
                                Toast.makeText(KacamataDetailActivity.this, "Berhasil Menambahkan Ke Favorit!", Toast.LENGTH_LONG).show();
                            } else {
                                status_favorit = "TAMBAH";
                                btn_favorit.setText("Tambah Favorit");
                                Toast.makeText(KacamataDetailActivity.this, "Berhasil Menghapus Favorit!", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(KacamataDetailActivity.this, "Gagal!", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(KacamataDetailActivity.this, "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(KacamataDetailActivity.this, "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(KacamataDetailActivity.this, "Internet Gagal", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setDownloadInvoice(){
        if (!file_3d.isEmpty()){
            final File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), Config.IMAGE_DIRECTORY_NAME);
            final File mediaFile = new File(mediaStorageDir.getPath() + File.separator + file_3d);
            if (mediaStorageDir.exists()) {
                if (mediaFile.exists()) {
                    ln_download.setVisibility(View.GONE);
                    ln_file.setVisibility(View.VISIBLE);
                    Log.d("catatan","file ada");
                } else {
                    ln_download.setVisibility(View.VISIBLE);
                    ln_file.setVisibility(View.GONE);
                    Log.d("catatan","file tidak ada");
                }
            } else {
                Log.d("catatan","direktori tidak ada");
                if(!mediaStorageDir.exists()) {
                    mediaStorageDir.mkdirs();
                    Log.d("catatan","direktori dibuat");
                }
            }
        } else {
            ln_download.setVisibility(View.GONE);
            ln_file.setVisibility(View.GONE);
        }
    }

    private void registerReceiver() {
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MESSAGE_PROGRESS);
        bManager.registerReceiver(broadcastReceiver, intentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().equals(MESSAGE_PROGRESS)){
                Download download = intent.getParcelableExtra("download");
                mProgressBar.setProgress(download.getProgress());
                if(download.getProgress() == 100){
                    mProgressText.setText("File Download Complete");
                    ln_file.setVisibility(View.VISIBLE);
                    ln_download.setVisibility(View.GONE);
                } else {
                    mProgressText.setText(String.format("Downloaded (%d/%d) MB",download.getCurrentFileSize(),download.getTotalFileSize()));
                }
            }
        }
    };

    private boolean checkPermission(){
        int result = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED){
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission(){
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSION_REQUEST_CODE);
    }

    private void startDownload(){
        ln_download.setVisibility(View.VISIBLE);
        Intent intent = new Intent(this, DownloadService.class);
        intent.putExtra("file_3d",file_3d);
        startService(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDownload();
                } else {
                    Toast.makeText(KacamataDetailActivity.this,"Permission Denied, Please allow to proceed !", Toast.LENGTH_LONG).show();
                }
                break;
        }
    }
}
