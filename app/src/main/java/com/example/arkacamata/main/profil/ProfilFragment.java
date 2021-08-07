package com.example.arkacamata.main.profil;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.example.arkacamata.R;
import com.example.arkacamata.config.SharePreference;

import java.util.HashMap;

public class ProfilFragment extends Fragment implements View.OnClickListener {
    SharePreference sharePreference;
    String nama, email, jenis_kelamin, alamat, nama_provinsi, nama_kabupaten, nama_kecamatan, nama_kelurahan;
    EditText et_nama, et_email, et_jenis_kelamin, et_alamat, et_nama_provinsi, et_nama_kabupaten,
            et_nama_kecamatan, et_nama_kelurahan;
    Button btn_profil, btn_password;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profil, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharePreference = new SharePreference(getContext());

        et_nama = view.findViewById(R.id.et_nama);
        et_email = view.findViewById(R.id.et_email);
        et_alamat = view.findViewById(R.id.et_alamat);
        et_jenis_kelamin = view.findViewById(R.id.et_jenis_kelamin);
        et_nama_provinsi = view.findViewById(R.id.et_nama_provinsi);
        et_nama_kabupaten = view.findViewById(R.id.et_nama_kabupaten);
        et_nama_kecamatan = view.findViewById(R.id.et_nama_kecamatan);
        et_nama_kelurahan = view.findViewById(R.id.et_nama_kelurahan);
        btn_profil = view.findViewById(R.id.btn_profil);
        btn_password = view.findViewById(R.id.btn_password);
        btn_profil.setOnClickListener(this);
        btn_password.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        HashMap<String, String> user = sharePreference.getUserDetails();
        nama = user.get(sharePreference.KEY_NAMA);
        email = user.get(sharePreference.KEY_EMAIL);
        jenis_kelamin = user.get(sharePreference.KEY_JENIS_KELAMIN);
        alamat = user.get(sharePreference.KEY_ALAMAT);
        nama_provinsi = user.get(sharePreference.KEY_NAMA_PROVINSI);
        nama_kabupaten = user.get(sharePreference.KEY_NAMA_KABUPATEN);
        nama_kecamatan = user.get(sharePreference.KEY_NAMA_KECAMATAN);
        nama_kelurahan = user.get(sharePreference.KEY_NAMA_KELURAHAN);

        et_nama.setText(nama);
        et_email.setText(email);
        et_jenis_kelamin.setText(jenis_kelamin);
        et_alamat.setText(alamat);
        et_nama_provinsi.setText(nama_provinsi);
        et_nama_kabupaten.setText(nama_kabupaten);
        et_nama_kecamatan.setText(nama_kecamatan);
        et_nama_kelurahan.setText(nama_kelurahan);
    }

    @Override
    public void onClick(View v) {
        if (v == btn_profil){
            startActivity(new Intent(getContext(),ProfilUbahActivity.class));
        } else if (v == btn_password){
            startActivity(new Intent(getContext(),ProfilPasswordActivity.class));
        }
    }
}
