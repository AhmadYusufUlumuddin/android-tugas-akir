package com.example.arkacamata.config;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.example.arkacamata.SignInActivity;

import java.util.HashMap;

public class SharePreference {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "ar_kacamata";
    private static final String IS_LOGIN = "LOGIN";
    public static final String KEY_ID_PENGGUNA = "id_tb_pengguna";
    public static final String KEY_NAMA = "nama";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ALAMAT = "alamat";
    public static final String KEY_JENIS_KELAMIN = "jenis_kelamin";
    public static final String KEY_ID_PROVINSI = "id_provinsi";
    public static final String KEY_NAMA_PROVINSI = "nama_provinsi";
    public static final String KEY_ID_KABUPATEN = "id_kabupaten";
    public static final String KEY_NAMA_KABUPATEN = "nama_kabupaten";
    public static final String KEY_ID_KECAMATAN = "id_kecamatan";
    public static final String KEY_NAMA_KECAMATAN = "nama_kecamatan";
    public static final String KEY_ID_KELURAHAN = "id_kelurahan";
    public static final String KEY_NAMA_KELURAHAN = "nama_kelurahan";

    public SharePreference(Context context){
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void create_session(String id, String nama, String email, String alamat, String jenis_kelamin,
                               String id_provinsi, String nama_provinsi, String id_kabupaten, String nama_kabupaten,
                               String id_kecamatan, String nama_kecamatan, String id_kelurahan, String nama_kelurahan){
        editor.putBoolean(IS_LOGIN, true);
        editor.putString(KEY_ID_PENGGUNA, id);
        editor.putString(KEY_NAMA, nama);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_ALAMAT, alamat);
        editor.putString(KEY_JENIS_KELAMIN, jenis_kelamin);
        editor.putString(KEY_ID_PROVINSI, id_provinsi);
        editor.putString(KEY_NAMA_PROVINSI, nama_provinsi);
        editor.putString(KEY_ID_KABUPATEN, id_kabupaten);
        editor.putString(KEY_NAMA_KABUPATEN, nama_kabupaten);
        editor.putString(KEY_ID_KECAMATAN, id_kecamatan);
        editor.putString(KEY_NAMA_KECAMATAN, nama_kecamatan);
        editor.putString(KEY_ID_KELURAHAN, id_kelurahan);
        editor.putString(KEY_NAMA_KELURAHAN, nama_kelurahan);
        editor.commit();
    }

    public void update(String nama, String email, String jenis_kelamin, String alamat,
                       String id_provinsi, String nama_provinsi, String id_kabupaten, String nama_kabupaten,
                       String id_kecamatan, String nama_kecamatan, String id_kelurahan, String nama_kelurahan){
        editor.putString(KEY_NAMA, nama);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_JENIS_KELAMIN, jenis_kelamin);
        editor.putString(KEY_ALAMAT, alamat);
        editor.putString(KEY_ID_PROVINSI, id_provinsi);
        editor.putString(KEY_NAMA_PROVINSI, nama_provinsi);
        editor.putString(KEY_ID_KABUPATEN, id_kabupaten);
        editor.putString(KEY_NAMA_KABUPATEN, nama_kabupaten);
        editor.putString(KEY_ID_KECAMATAN, id_kecamatan);
        editor.putString(KEY_NAMA_KECAMATAN, nama_kecamatan);
        editor.putString(KEY_ID_KELURAHAN, id_kelurahan);
        editor.putString(KEY_NAMA_KELURAHAN, nama_kelurahan);
        editor.commit();
    }
    public void logoutUser(){
        editor.clear();
        editor.commit();

        Intent i = new Intent(_context, SignInActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<String, String>();

        user.put(KEY_ID_PENGGUNA, pref.getString(KEY_ID_PENGGUNA, null));
        user.put(KEY_NAMA, pref.getString(KEY_NAMA, null));
        user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
        user.put(KEY_ALAMAT, pref.getString(KEY_ALAMAT, null));
        user.put(KEY_JENIS_KELAMIN, pref.getString(KEY_JENIS_KELAMIN, null));
        user.put(KEY_ID_PROVINSI, pref.getString(KEY_ID_PROVINSI, null));
        user.put(KEY_NAMA_PROVINSI, pref.getString(KEY_NAMA_PROVINSI, null));
        user.put(KEY_ID_KABUPATEN, pref.getString(KEY_ID_KABUPATEN, null));
        user.put(KEY_NAMA_KABUPATEN, pref.getString(KEY_NAMA_KABUPATEN, null));
        user.put(KEY_ID_KECAMATAN, pref.getString(KEY_ID_KECAMATAN, null));
        user.put(KEY_NAMA_KECAMATAN, pref.getString(KEY_NAMA_KECAMATAN, null));
        user.put(KEY_ID_KELURAHAN, pref.getString(KEY_ID_KELURAHAN, null));
        user.put(KEY_NAMA_KELURAHAN, pref.getString(KEY_NAMA_KELURAHAN, null));
        return user;
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(IS_LOGIN, false);
    }
}
