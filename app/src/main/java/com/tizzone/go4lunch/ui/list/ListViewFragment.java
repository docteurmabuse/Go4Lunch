package com.tizzone.go4lunch.ui.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.PlacesListAdapters;
import com.tizzone.go4lunch.api.APIClient;
import com.tizzone.go4lunch.api.GoogleMapAPI;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.places.Result;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListViewFragment extends Fragment {

    private ListViewModel listViewModel;
    private RecyclerView recyclerViewPlaces;
    private PlacesListAdapters placesListAdapter;
    private List<Result> places;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listViewModel =
                new ViewModelProvider(this).get(ListViewModel.class);
        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final TextView textView = root.findViewById(R.id.text_dashboard);
        listViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));
        // BEGIN_INCLUDE(initializeRecyclerView)
        recyclerViewPlaces = (RecyclerView) root.findViewById(R.id.listViewPlaces);
        Context context = root.getContext();
        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(context));
        places = new ArrayList<>();

        double latitude = -33.8670522;
        double longitude = 151.1957362;
        //   recyclerView.setAdapter(new PlacesListAdapters(DummyContent.ITEMS));
        String key = getText(R.string.google_maps_key).toString();
        //String currentLocation = location.getLatitude() + "," + location.getLongitude();
        int radius = 1500;
        GoogleMapAPI googleMapAPI = APIClient.getClient().create(GoogleMapAPI.class);
        //googleMapAPI.getNearByPlaces("48.850167,2.39077", 5, "restaurant", "AIzaSyBK_IN5GbLg77wSfRKVx1qrJHOVc2Tdv5g").enqueue(new Callback<PlacesResults>() {
        //googleMapAPI.getNearByPlaces(currentLocation, radius, "restaurant", key).enqueue(new Callback<PlacesResults>() {
        googleMapAPI.getNearByPlaces(latitude + "," + longitude, 1500, "restaurant", "AIzaSyBK_IN5GbLg77wSfRKVx1qrJHOVc2Tdv5g").enqueue(new Callback<PlacesResults>() {
            @Override
            public void onResponse(Call<PlacesResults> call, Response<PlacesResults> response) {
                if (response.isSuccessful()) {
                    places = response.body().getResults();
                }
            }

            @Override
            public void onFailure(Call<PlacesResults> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        placesListAdapter = new PlacesListAdapters(places);
        recyclerViewPlaces.setAdapter(placesListAdapter);
        return root;
    }

    public void onResume() {
        super.onResume();
        //recyclerViewPlaces.setAdapter(placesListAdapter);
    }
}