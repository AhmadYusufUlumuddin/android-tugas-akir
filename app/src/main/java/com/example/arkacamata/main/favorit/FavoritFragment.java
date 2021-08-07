package com.example.arkacamata.main.favorit;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.arkacamata.R;
import com.example.arkacamata.config.Config;
import com.example.arkacamata.config.SharePreference;
import com.example.arkacamata.config.UserAPIServices;
import com.example.arkacamata.config.adapter.MyAdapterKacamata;
import com.example.arkacamata.config.item.ItemKacamata;

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

public class FavoritFragment extends Fragment {
    ArrayList<ItemKacamata> itemKacamataArrayList = new ArrayList<>();
    RecyclerView recyclerView_kacamata;
    MyAdapterKacamata myAdapterKacamata;
    SharePreference sharePreference;
    String id_tb_pengguna;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_favorit, container, false);
        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharePreference = new SharePreference(getContext());
        HashMap<String, String> user = sharePreference.getUserDetails();
        id_tb_pengguna = user.get(sharePreference.KEY_ID_PENGGUNA);

        recyclerView_kacamata = view.findViewById(R.id.recyclerView_kacamata);
        recyclerView_kacamata.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL,false));
    }

    @Override
    public void onResume() {
        super.onResume();
        getData();
    }

    private void getData() {
        itemKacamataArrayList.clear();
        final ProgressDialog pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Tunggu sebentar...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(true);
        pDialog.show();

        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        builder.addFormDataPart("id_tb_pengguna",id_tb_pengguna);
        MultipartBody requestBody = builder.build();

        UserAPIServices api = Config.getRetrofit(Config.URL).create(UserAPIServices.class);
        Call<ResponseBody> post = api.kacamata_favorit(requestBody);
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
                        String id_tb_kacamata   = c.getString("id_tb_kacamata");
                        String nama_kacamata    = c.getString("nama_kacamata");
                        String foto_kacamata    = c.getString("foto_kacamata");
                        String harga_kacamata   = c.getString("harga_kacamata");
                        String nama_kategori    = c.getString("nama_kategori");

                        itemKacamataArrayList.add(new ItemKacamata(id_tb_kacamata, nama_kacamata, foto_kacamata, harga_kacamata, nama_kategori));
                    }
                    myAdapterKacamata = new MyAdapterKacamata(itemKacamataArrayList,getContext());
                    recyclerView_kacamata.setAdapter(myAdapterKacamata);
                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Tidak bisa mengirim data!", Toast.LENGTH_LONG).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(), "Tidak bisa mengirim data!!", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(getContext(), "Tidak bisa mengirim data!!!", Toast.LENGTH_LONG).show();
            }
        });
    }
}
