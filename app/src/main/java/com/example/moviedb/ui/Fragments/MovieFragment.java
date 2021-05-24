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
import com.example.moviedb.tools.internetConnection;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.MetadataChanges;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MovieFragment extends Fragment {

    SwipeRefreshLayout pullToRefresh;
    ArrayList<MovieGS> List;

    // Firebase
    FirebaseFirestore mFirestore;
    FirebaseFirestoreSettings settings;

    String img, title, checkDataExist = "";
    internetConnection connection = new internetConnection();

    public static androidx.fragment.app.Fragment newInstance() {
        MovieFragment fragment = new MovieFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_movie, container, false);

        mFirestore = FirebaseFirestore.getInstance();
        settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true)
                .build();
        mFirestore.setFirestoreSettings(settings);

        pullToRefresh = view.findViewById(R.id.pullToRefresh);

        if (!connection.isNetworkConnectionAvailable(getContext())){
            dataOffline(view);
        } else {
            String url = "https://api.themoviedb.org/3/movie/550?api_key=5b35c58486dc9d8e393ad9418f546956";
            sendAndRequestResponse(url, view);

            //setting an setOnRefreshListener on the SwipeDownLayout
            pullToRefresh.setOnRefreshListener(() -> {
                if (!connection.isNetworkConnectionAvailable(getContext())) {
                    dataOffline(view);
                    pullToRefresh.setRefreshing(false);
                } else {
                    sendAndRequestResponse(url, view);
                    pullToRefresh.setRefreshing(false);
                }

            });
        }

        return view;
    }

    private void sendAndRequestResponse(String url, View view) {
        List = new ArrayList<>();
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

                            img = dataobj.getString("logo_path");
                            title = dataobj.getString("name");
                            mFirestore.collection("movies")
                                    .whereEqualTo("title", dataobj.getString("name"))
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        for (QueryDocumentSnapshot document : task.getResult()) { checkDataExist = document.getData().get("img").toString(); }
                                        if (checkDataExist == "") {
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("img", img);
                                            map.put("title", title);
                                            mFirestore.collection("movies").add(map).addOnSuccessListener(documentReference -> {
                                            }).addOnFailureListener(e -> Toast.makeText(getContext(), "No se crearon los datos correctamente", Toast.LENGTH_LONG).show());
                                        }
                                    });

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

    private void dataOffline (View view) {
        List = new ArrayList<>();
        mFirestore.collection("movies")
                .addSnapshotListener(MetadataChanges.INCLUDE, (querySnapshot, e) -> {
                    for (DocumentChange change : querySnapshot.getDocumentChanges()) {
                        if (change.getType() == DocumentChange.Type.ADDED) {
                            List.add(new MovieGS(
                                    change.getDocument().getData().get("img").toString(),
                                    change.getDocument().getData().get("title").toString()
                            ));
                        }
                    }
                    if (getActivity()!=null) {
                        ListView listview = view.findViewById(R.id.listView);
                        MovieAdapter adapter = new MovieAdapter(getActivity(), List);
                        listview.setAdapter(adapter);
                    }
                });
    }
}