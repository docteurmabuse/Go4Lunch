package com.tizzone.go4lunch.ui.list;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.PlacesListAdapter;
import com.tizzone.go4lunch.databinding.FragmentListBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ListViewFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 44;
    private PlacesListAdapter placesListAdapter;
    private String key;
    private FragmentListBinding fragmentListBinding;
    private final int PERMISSION_ID = 44;
    private final LatLng mDefaultLocation = new LatLng(48.850559, 2.377078);
    private boolean mLocationPermissionGranted;
    // The geographical location where the device is currently located. That is, the last-known
    // location retrieved by the Fused Location Provider.
    private Location lastKnownLocation;
    private LatLng currentLocation;
    private static final int AUTOCOMPLETE_REQUEST_CODE = 1;
    private EditText queryText;
    private LocationViewModel locationViewModel;
    private SearchView.SearchAutoComplete searchAutoComplete;
    private PlacesClient placesClient;

    public static LatLng getCoordinate(double lat0, double lng0, long dy, long dx) {
        double lat = lat0 + (180 / Math.PI) * (dy / 6378137);
        double lng = lng0 + (180 / Math.PI) * (dx / 6378137) / Math.cos(lat0);
        return new LatLng(lat, lng);
    }

    private void setBounds(Location location, int mDistanceInMeters) {
        double latRadian = Math.toRadians(location.getLatitude());

        double degLatKm = 110.574235;
        double degLongKm = 110.572833 * Math.cos(latRadian);
        double deltaLat = mDistanceInMeters / 1000.0 / degLatKm;
        double deltaLong = mDistanceInMeters / 1000.0 / degLongKm;

        double minLat = location.getLatitude() - deltaLat;
        double minLong = location.getLongitude() - deltaLong;
        double maxLat = location.getLatitude() + deltaLat;
        double maxLong = location.getLongitude() + deltaLong;

        Log.d(TAG, "Min: " + minLat + "," + minLong);
        Log.d(TAG, "Max: " + maxLat + "," + maxLong);

    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getText(R.string.google_maps_key).toString();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity());
//        initAutocomplete();
        //Set current user position in adapter
        locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        setHasOptionsMenu(true);
        Places.initialize(this.getContext().getApplicationContext(), key);
        placesClient = Places.createClient(this.getContext());

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);

        super.onCreateOptionsMenu(menu, inflater);
//        //MenuItem menuItem =
//        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setBackgroundColor(Color.WHITE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        //  getActivity().setContentView(R.layout.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            /**
             * Called when the user submits the query. This could be due to a key press on the
             * keyboard or due to pressing a submit button.
             * The listener can override the standard behavior by returning true
             * to indicate that it has handled the submit request. Otherwise return false to
             * let the SearchView handle the submission by launching any associated intent.
             *
             * @param query the query text that is to be submitted
             * @return true if the query has been handled by the listener, false to let the
             * SearchView perform the default action.
             */
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    initAutocomplete(query);
                }
                return true;
            }

            /**
             * Called when the query text is changed by the user.
             *
             * @param newText the new content of the query text field.
             * @return false if the SearchView should perform the default action of showing any
             * suggestions if available, true if the action was handled by the listener.
             */
            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    initAutocomplete(newText);
                }
                return false;
            }
        });
        // Get the intent, verify the action and get the query
        Intent intent = getActivity().getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            initAutocomplete(query);
        }

        // Get SearchView autocomplete object.
//        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
//        searchAutoComplete.setBackgroundColor(Color.WHITE);
//        searchAutoComplete.setTextColor(Color.BLACK);
//        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);

    }

    private void initAutocomplete(String query) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        RectangularBounds bounds = null;
        if (currentLocation != null) {
            // Create a RectangularBounds object.
            bounds = RectangularBounds.newInstance(
                    getCoordinate(currentLocation.latitude, currentLocation.longitude, -1000, -1000),
                    getCoordinate(currentLocation.latitude, currentLocation.longitude, 1000, 1000));
        }

        // Use the builder to create a FindAutocompletePredictionsRequest.
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                // Call either setLocationBias() OR setLocationRestriction().
                .setLocationBias(bounds)
                //.setLocationRestriction(bounds)
                .setOrigin(currentLocation)
                .setCountries("FR")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(query)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                Log.i(TAG, prediction.getPlaceId());
                Log.i(TAG, prediction.getPrimaryText(null).toString());

                //placesListAdapter.setPlaces(prediction., key);

            }
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });

    }

    /**
     * Called when the hidden state (as returned by {@link #isHidden()} of
     * the fragment has changed.  Fragments start out not hidden; this will
     * be called whenever the fragment changes state from that.
     *
     * @param hidden True if the fragment is now hidden, false otherwise.
     */
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        View root = fragmentListBinding.getRoot();
        List<Restaurant> places = new ArrayList<>();

        placesListAdapter = new PlacesListAdapter(places, key, getContext(), currentLocation);

        RecyclerView recyclerViewPlaces = root.findViewById(R.id.listViewPlaces);
        recyclerViewPlaces.setHasFixedSize(true);
        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(root.getContext()));

        //Set current user position in adapter
        //LocationViewModel locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), locationModel -> {
            if (locationModel != null) {

                placesListAdapter.setCurrentLocation(locationModel.getLocation());
                this.currentLocation = locationModel.getLocation();
            }
        });

        //Set retrofit place in adapter
        PlacesViewModel placesViewModel = new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
        placesViewModel.getPlacesResultsLiveData().observe(getViewLifecycleOwner(), placesResults -> {
            ArrayList<Restaurant> restaurants = new ArrayList<>();
            Resources resources = this.getResources();
            for (Result place : placesResults.getResults()) {
                Restaurant restaurant = new Restaurant(place.getPlaceId(), place.getName(), place.getVicinity(), place.getPhotoUrl(resources), place.getRating(), 0,
                        place.getOpeningHours().getOpenNow(), new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLat()));
                restaurants.add(restaurant);
            }

            placesListAdapter.setPlaces(restaurants, key);
        });

        recyclerViewPlaces.setAdapter(placesListAdapter);

        Context context = root.getContext();
        return root;
    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentListBinding = null;
    }
}