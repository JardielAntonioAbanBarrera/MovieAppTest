package com.example.moviedb.ui.Fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.moviedb.R;
import com.example.moviedb.adapters.MovieAdapter;
import com.example.moviedb.gs.MovieGS;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MovieFragment extends Fragment {

    SwipeRefreshLayout pullToRefresh;
    ArrayList<MovieGS> List;

    public static androidx.fragment.app.Fragment newInstance() {
        MovieFragment fragment = new MovieFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        //connection.handleSSLHandshake();
        String url = "https://api.themoviedb.org/3/movie/550?api_key=5b35c58486dc9d8e393ad9418f546956";
        List = new ArrayList<>();
        sendAndRequestResponse(url, view);

        //setting an setOnRefreshListener on the SwipeDownLayout
        pullToRefresh.setOnRefreshListener(() -> {
            List = new ArrayList<>();
            sendAndRequestResponse(url, view);
            pullToRefresh.setRefreshing(false);
        });

        return view;
    }

    private void sendAndRequestResponse(String url, View view) {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Loading...");
        progressDialog.show();

        //RequestQueue initialized
        RequestQueue mRequestQueue = Volley.newRequestQueue(getContext());
        //String Request initialized
        StringRequest mStringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        //getting the whole json object from the response
                        JSONObject obj = new JSONObject(response);
                        JSONArray dataArray  = obj.getJSONArray("production_companies");
                        for (int i = 0; i < dataArray.length(); i++) {
                            JSONObject dataobj = dataArray.getJSONObject(i);
                            List.add(new MovieGS(
                                    dataobj.getString("logo_path"),
                                    dataobj.getString("name")
                            ));
                        }

                        if (getActivity()!=null) {
                            ListView listview = view.findViewById(R.id.listView);
                            MovieAdapter adapter = new MovieAdapter(getActivity(), List);
                            listview.setAdapter(adapter);
                        }
                        progressDialog.dismiss();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Try again", Toast.LENGTH_LONG).show();
                        progressDialog.dismiss();
                    }
                }, error -> {
            Toast.makeText(getContext(), error.getMessage() + "Error Detected", Toast.LENGTH_LONG).show();
            progressDialog.dismiss();
        });
        mRequestQueue.add(mStringRequest);
    }
}