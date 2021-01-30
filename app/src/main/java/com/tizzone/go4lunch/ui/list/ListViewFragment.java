package com.tizzone.go4lunch.ui.list;

import android.app.SearchManager;
import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.PhotoMetadata;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.adapters.PlacesListAdapter;
import com.tizzone.go4lunch.databinding.FragmentListBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.RestaurantViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;
import io.reactivex.rxjava3.disposables.Disposable;

import static android.content.ContentValues.TAG;

@AndroidEntryPoint
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
    private RestaurantViewModel restaurantViewModel;
    private PlacesViewModel placesViewModel;

    private SearchView.SearchAutoComplete searchAutoComplete;
    private PlacesClient placesClient;
    private ArrayList<Restaurant> restaurants;
    private SearchView searchView;

    //FOR DATA
    private Disposable disposable;


    private List<Restaurant> newRestaurantsList;

    public static LatLng getCoordinate(double lat0, double lng0, long dy, long dx) {
        double lat = lat0 + (180 / Math.PI) * (dy / 6378137);
        double lng = lng0 + (180 / Math.PI) * (dx / 6378137) / Math.cos(lat0);
        return new LatLng(lat, lng);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        key = getText(R.string.google_maps_key).toString();
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity());


        setHasOptionsMenu(true);
        Places.initialize(this.getContext().getApplicationContext(), key);
        placesClient = Places.createClient(this.getContext());
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        fragmentListBinding = FragmentListBinding.inflate(inflater, container, false);
        View root = fragmentListBinding.getRoot();
//        List<Restaurant> places = new ArrayList<>();
//        placesViewModel =
//                new ViewModelProvider(requireActivity()).get(PlacesViewModel.class);
//        container.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
//            @Override
//            public void onViewAttachedToWindow(View view) {
//
//            }
//
//            @Override
//            public void onViewDetachedFromWindow(View view) {
//                // Manage this event.
//                setRetrofitInAdapter();
//                Toast.makeText(getActivity(), "You close the search", Toast.LENGTH_LONG).show();
//            }
//        });
//        placesViewModel.init();
//
//
//        placesListAdapter = new PlacesListAdapter(places, currentLocation, getContext());
//
//        RecyclerView recyclerViewPlaces = root.findViewById(R.id.listViewPlaces);
//        recyclerViewPlaces.setHasFixedSize(true);
//        recyclerViewPlaces.setLayoutManager(new LinearLayoutManager(root.getContext()));
//
//
//        setRetrofitInAdapter();
//        recyclerViewPlaces.setAdapter(placesListAdapter);
//
//        restaurantViewModel.getRestaurants().observe(this.getActivity(), this::onChanged);
        return root;
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     * This gives subclasses a chance to initialize themselves once
     * they know their view hierarchy has been completely created.  The fragment's
     * view hierarchy is not however attached to its parent at this point.
     *
     * @param view               The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //Init ViewModels
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);



        initRecycleView();
        observeData();
        // placesViewModel.getRestaurantsList();
    }

    private void observeData() {
        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), locationModel -> {
            if (locationModel != null) {
                placesListAdapter.setCurrentLocation(locationModel.getLocation());
                this.currentLocation = locationModel.getLocation();
            }
        });

        placesViewModel.getRestaurantsList().observe(getViewLifecycleOwner(), restaurants -> {
            Log.e(TAG, "onChanged: " + restaurants.size());
            placesListAdapter.setPlaces(restaurants, currentLocation);
        });
    }

    private void initRecycleView() {
        fragmentListBinding.listViewPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        placesListAdapter = new PlacesListAdapter(restaurants, currentLocation, getContext());
        fragmentListBinding.listViewPlaces.setAdapter(placesListAdapter);
    }

    /**
     * Called when the fragment is no longer in use.  This is called
     * after {@link #onStop()} and before {@link #onDetach()}.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.disposeWhenDestroy();
    }

    private void disposeWhenDestroy() {
        if (this.disposable != null && this.disposable.isDisposed()) this.disposable.dispose();
    }

    public void onResume() {
        super.onResume();
        //Set current user position in adapter
        //LocationViewModel locationViewModel = new ViewModelProvider(requireActivity()).get(LocationViewModel.class);
        locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), locationModel -> {
            if (locationModel != null) {
                placesListAdapter.setCurrentLocation(locationModel.getLocation());
                this.currentLocation = locationModel.getLocation();
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentListBinding = null;
    }

    // Update UI

    private void updateUIWithListOfRestaurant(PlacesResults placesResults) {

    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.options_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
//        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getContext().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        //searchView.setBackgroundColor(Color.WHITE);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));
        searchView.setIconifiedByDefault(true);
        searchView.setIconified(false);

        //searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        // searchView.setBackgroundColor(Color.WHITE);
        searchView.findViewById(R.id.search_close_btn)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Log.d("called", "this is called.");

                        searchView.setIconified(true);
                        //initRestaurants();
                        Toast.makeText(getActivity(), "You close the search", Toast.LENGTH_LONG).show();

                    }
                });

        //  getActivity().setContentView(R.layout.search);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (query.length() > 2) {
                    initAutocomplete(query);
                    searchView.setFocusable(false);
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    initAutocomplete(newText);
                    searchView.setFocusable(false);
                    return true;
                }
                return false;
            }
        });
    }


    // Get the intent, verify the action and get the query
//        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
//            String query = intent.getStringExtra(SearchManager.QUERY);
//            initAutocomplete(query);
//        }


    // Get SearchView autocomplete object.
//        final SearchView.SearchAutoComplete searchAutoComplete = (SearchView.SearchAutoComplete)searchView.findViewById(androidx.appcompat.R.id.search_src_text);
//        searchAutoComplete.setBackgroundColor(Color.WHITE);
//        searchAutoComplete.setTextColor(Color.BLACK);
//        searchAutoComplete.setDropDownBackgroundResource(android.R.color.white);
    private void initAutocomplete(String query) {
        // Set the fields to specify which types of place data to
        // return after the user has made a selection.
        // Create a new token for the autocomplete session. Pass this to FindAutocompletePredictionsRequest,
        // and once again when the user makes a selection (for example when calling fetchPlace()).
        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();
        // Create a RectangularBounds object.
        RectangularBounds bounds = RectangularBounds.newInstance(
                getCoordinate(currentLocation.latitude, currentLocation.longitude, -100, -100),
                getCoordinate(currentLocation.latitude, currentLocation.longitude, 100, 100));
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
            // List<String> placesPrediction = new ArrayList<>();
            List<Restaurant> filteredRestaurants = new ArrayList<>();

            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                //  Log.i(TAG, prediction.getPlaceId());
                // Log.i(TAG, prediction.getPrimaryText(null).toString());
                //placesPrediction.add(prediction.getPlaceId());
                //   placesViewModel.getDetailByPlaceId(prediction.getPlaceId(), "name,geometry,vicinity,photos,rating,formatted_phone_number", key);
                // addRestaurant(prediction.getPlaceId());
//                for (Result place :prediction.getPlaceId()) {
//                    Boolean isOpen = null;
//                    if (place.getOpeningHours() != null)
//                        isOpen = place.getOpeningHours().getOpenNow();
//                    Restaurant restaurant = new Restaurant(place.getPlaceId(), place.getName(), place.getVicinity(), place.getPhotoUrl(resources), place.getRating(), 0,
//                            isOpen, new LatLng(place.getGeometry().getLocation().getLat(), place.getGeometry().getLocation().getLat()));
//                    restaurants.add(restaurant);
//                }
                for (Restaurant restaurant : restaurants) {
                    Log.i(TAG, prediction.getPrimaryText(null).toString() + ":" +
                            restaurant.getName());
                    Log.i(TAG, prediction.getPlaceId() + ":" +
                            restaurant.getUid());
                    if (restaurant.getUid().contains(prediction.getPlaceId())) {
                        filteredRestaurants.add(restaurant);


                    }
                }

            }
            restaurantViewModel.setRestaurants(filteredRestaurants);
            // addRestaurant(placesPrediction);
            //  placesListAdapter.setFilter(placesPrediction);
            //placesListAdapter.setPlaces(filteredRestaurants,currentLocation);
            //restaurantViewModel.setRestaurants(filteredRestaurants);
            //restaurants = new ArrayList<>();
            //placesListAdapter.setPlaces(restaurants,currentLocation);
            // setRetrofitDetailInAdapter(placesPrediction);

        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });
    }

    private void addRestaurant(List<String> placesId) {

        // Specify the fields to return.
        newRestaurantsList = new ArrayList<>();
        List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.ADDRESS, Place.Field.TYPES, Place.Field.PHOTO_METADATAS,
                Place.Field.WEBSITE_URI, Place.Field.PHONE_NUMBER, Place.Field.RATING, Place.Field.TYPES, Place.Field.LAT_LNG, Place.Field.OPENING_HOURS);
        //restaurants = new ArrayList<>();
        for (String placeId : placesId) {
            // Construct a request object, passing the place ID and fields array.
            FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);
            placesClient.fetchPlace(request).addOnSuccessListener((response) -> {

                Place place = response.getPlace();
                createList(response.getPlace());
            });
        }
        restaurantViewModel.setRestaurants(newRestaurantsList);
    }

    private void createList(Place place) {
        List<Place.Type> types = place.getTypes();
        for (int i = 0; i < place.getTypes().size(); i++) {
            if (types.get(i).name().contains("RESTAURANT")) {
                final List<PhotoMetadata> metadata = place.getPhotoMetadatas();
                // Create a FetchPhotoRequest.
                if (metadata != null) {
                    String photoMetadata = metadata.get(0).toString();
                    photoMetadata = photoMetadata.substring(1, photoMetadata.length() - 1);
                    String[] photoArray = photoMetadata.split(",");
                    Map<String, String> hashMapPhoto = new HashMap<>();

                    for (String val : photoArray) {
                        String[] name = val.split("=");
                        hashMapPhoto.put(name[0].trim(), name[1].trim());
                    }
                    String photoReference = hashMapPhoto.get("photoReference");
                    String staticUrl = "https://maps.googleapis.com/maps/api/place/photo?";
                    String mKey = getString(R.string.google_maps_key);
                    String photoUrl = staticUrl + "maxwidth=400&photoreference=" + photoReference + "&key=" + mKey;
                    Restaurant restaurant = new Restaurant(place.getId(), place.getName(), place.getAddress(), photoUrl, place.getRating().floatValue(), 0,
                            place.isOpen(), place.getLatLng());
                    newRestaurantsList.add(restaurant);
                }

            }

            Log.i(TAG, "Place found: " + place.getName());
        }
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

    private void onChanged(List<Restaurant> restaurants) {
        placesListAdapter.setPlaces(restaurants, currentLocation);
    }


}