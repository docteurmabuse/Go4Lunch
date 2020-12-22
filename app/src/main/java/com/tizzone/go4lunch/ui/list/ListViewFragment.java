package com.tizzone.go4lunch.ui.list;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.PlacesListAdapters;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;

import java.util.ArrayList;
import java.util.List;

public class ListViewFragment extends Fragment {

    private PlacesViewModel placesViewModel;
    private RecyclerView recyclerViewPlaces;
    private PlacesListAdapters placesListAdapter;
    private List<Result> places;
    private String key;

    /**
     * Called to do initial creation of a fragment.  This is called after
     * and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     *
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getText(R.string.google_maps_key).toString();
        double latitude = -33.8670522;
        double longitude = 151.1957362;
        int radius = 1000;
        placesListAdapter = new PlacesListAdapters();
        placesViewModel =
                new ViewModelProvider(this).get(PlacesViewModel.class);
        placesViewModel.getPlacesResultsLiveData().observe(this, new Observer<PlacesResults>() {
            @Override
            public void onChanged(PlacesResults placesResults) {
                if (placesResults != null) {
                    placesListAdapter.setmPlaces(placesResults.getResults(), key);
                }
            }
        });
        placesViewModel.getNearByPlaces(latitude + "," + longitude, radius, "restaurant", key);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        places = new ArrayList<>();

        View root = inflater.inflate(R.layout.fragment_dashboard, container, false);
        // final TextView textView = root.findViewById(R.id.text_dashboard);
        // placesViewModel.getText().observe(getViewLifecycleOwner(), s -> textView.setText(s));

        recyclerViewPlaces = root.findViewById(R.id.listViewPlaces);
        recyclerViewPlaces.setHasFixedSize(true);
        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(root.getContext()));

        recyclerViewPlaces.setAdapter(placesListAdapter);

        Context context = root.getContext();
        // placesListAdapter = new PlacesListAdapters(places);
        return root;
    }

    public void onResume() {
        super.onResume();
    }


}