package com.tizzone.go4lunch.ui.map;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.databinding.FragmentMapBinding;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.ui.list.PlaceDetailActivity;
import com.tizzone.go4lunch.utils.UserHelper;
import com.tizzone.go4lunch.viewmodels.LocationViewModel;
import com.tizzone.go4lunch.viewmodels.PlacesViewModel;
import com.tizzone.go4lunch.viewmodels.RestaurantViewModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dagger.hilt.android.AndroidEntryPoint;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.ContentValues.TAG;

@AndroidEntryPoint
public class MapFragment extends Fragment {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 546;
    private static final float DEFAULT_ZOOM = 16;
    private final int PROXIMITY_RADIUS = 1000;
    private GoogleMap mMap;
    private boolean mLocationPermissionGranted;
    private Location mLastKnownLocation;
    private final LatLng mDefaultLocation = new LatLng(65.850559, 2.377078);
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final double latitude = 78.850559;
    private final double longitude = 2.377078;
    private String key;
    private PlacesViewModel placesViewModel;
    private LocationViewModel locationViewModel;
    private Marker workmatesRestaurant;
    private Marker emptyRestaurant;
    private Context mContext;
    private PlacesClient placesClient;
    private LatLng currentLocation;
    private RestaurantViewModel restaurantViewModel;
    private List<Restaurant> newRestaurantsList;
    private FragmentMapBinding mapBinding;


    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mapBinding = FragmentMapBinding.inflate(inflater, container, false);
        View root = mapBinding.getRoot();


        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Init  ViewModels
        placesViewModel = new ViewModelProvider(this).get(PlacesViewModel.class);
        locationViewModel = new ViewModelProvider(this).get(LocationViewModel.class);
        restaurantViewModel = new ViewModelProvider(this).get(RestaurantViewModel.class);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }

        key = getText(R.string.google_maps_key).toString();
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        Places.initialize(this.getContext().getApplicationContext(), key);
        placesClient = Places.createClient(this.getContext());
    }


    public Bitmap getBitmapFromVectorDrawable(int drawableId) {
        Drawable drawable = AppCompatResources.getDrawable(mContext, drawableId);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            drawable = (DrawableCompat.wrap(drawable)).mutate();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
                drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    private final OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        @Override
        public void onMapReady(GoogleMap googleMap) {
            mMap = googleMap;

            // Prompt the user for permission.
            getLocationPermission();
            // [END_EXCLUDE]

            // Turn on the My Location layer and the related control on the map.
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();
            placesViewModel.getRestaurantsList();
            observeData();
//            restaurantViewModel.getRestaurants().observe(getActivity(), new Observer<List<Restaurant>>() {
//                @Override
//                public void onChanged(List<Restaurant> restaurants) {
//                    setMarkers(restaurants);
//                }
//            });


            // Set a listener for info window events.
            mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick(Marker marker) {
                    viewRestaurantDetail((Restaurant) marker.getTag());
                }
            });
        }

    };

    private void observeData() {
        placesViewModel.getRestaurantsList().observe(getViewLifecycleOwner(), new Observer<ArrayList<Restaurant>>() {
            @Override
            public void onChanged(ArrayList<Restaurant> restaurants) {
                setMarkers(restaurants);

            }
        });
    }

    public static LatLng getCoordinate(double lat0, double lng0, long dy, long dx) {
        double lat = lat0 + (180 / Math.PI) * (dy / 6378137);
        double lng = lng0 + (180 / Math.PI) * (dx / 6378137) / Math.cos(lat0);
        return new LatLng(lat, lng);
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
                    searchView.setFocusable(false);
                    return false;
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.length() > 2) {
                    searchView.setFocusable(false);

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
                            .setQuery(newText)
                            .build();
                    placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
                        List<String> placesPrediction = new ArrayList<>();
                        List<Restaurant> filteredRestaurants = new ArrayList<>();

                        for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                            Log.i(TAG, prediction.getPlaceId());
                            Log.i(TAG, prediction.getPrimaryText(null).toString());
                            placesPrediction.add(prediction.getPlaceId());
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


                        }
                        addRestaurant(placesPrediction);
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
                    return true;
                }
                return false;
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


    private void setMarkers(List<Restaurant> restaurants) {
        for (Restaurant restaurant : restaurants) {
            MarkerOptions markerOptions = new MarkerOptions();
            LatLng latLng = restaurant.getLocation();
            // Position of Marker on Map
            markerOptions.position(latLng);
            // Adding Title to the Marker
            markerOptions.title(restaurant.getName() + " : " + restaurant.getAddress());
            Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_restaurant_pin_red);
            BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
            UserHelper.getUsersLunchSpot(restaurant.getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                @Override
                public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listener failed.", error);
                        Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_restaurant_pin_red);
                        BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
                        // Adding No Workmates Restaurant Marker to the Camera.
                        emptyRestaurant = mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title(restaurant.getName())
                                .snippet(restaurant.getAddress())
                                .icon(mapIcon));
                        emptyRestaurant.setTag(restaurant);
                    } else {
                        int usersCount = value.size();
                        if (usersCount > 0) {
                            Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_restaurant_pin_green);
                            BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
                            // Adding Workmates Restaurant's Marker to the Camera.

                            workmatesRestaurant = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(restaurant.getName())
                                    .snippet(restaurant.getAddress())
                                    .icon(mapIcon));
                            workmatesRestaurant.setTag(restaurant);
                        } else {
                            Bitmap bitmap = getBitmapFromVectorDrawable(R.drawable.ic_restaurant_pin_red);
                            BitmapDescriptor mapIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
                            // Adding No Workmates Restaurant Marker to the Camera.
                            emptyRestaurant = mMap.addMarker(new MarkerOptions()
                                    .position(latLng)
                                    .title(restaurant.getName())
                                    .snippet(restaurant.getAddress())
                                    .icon(mapIcon));
                            emptyRestaurant.setTag(restaurant);
                        }
                    }

                }
            });

        }

    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Set the map's camera position to the current location of the device.
                        mLastKnownLocation = task.getResult();
                        if (mLastKnownLocation != null) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(),
                                            mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            locationViewModel.setUserLocation(mLastKnownLocation.getLatitude(),
                                    mLastKnownLocation.getLongitude());
                            build_retrofit_and_get_response(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                        } else {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(latitude, longitude), DEFAULT_ZOOM));
                            locationViewModel.setUserLocation(latitude,
                                    longitude);
                            build_retrofit_and_get_response(latitude, longitude);
                        }
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                        mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        build_retrofit_and_get_response(mDefaultLocation.latitude, mDefaultLocation.longitude);
                        locationViewModel.setUserLocation(mDefaultLocation.latitude,
                                mDefaultLocation.longitude);
                    }
                    mMap.setMyLocationEnabled(true);
                    mMap.getUiSettings().setMyLocationButtonEnabled(true);
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(requireActivity(),
                ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(true);
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.clear();
        } else {
            ActivityCompat.requestPermissions(requireActivity(),
                    new String[]{ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void build_retrofit_and_get_response(double latitude, double longitude) {
        newRestaurantsList = new ArrayList<>();
        placesViewModel.getRestaurants(latitude + "," + longitude, PROXIMITY_RADIUS, "restaurant", key);
//        placesViewModel.getNearByPlaces(latitude + "," + longitude, PROXIMITY_RADIUS, "restaurant", key);
//        placesViewModel.getPlacesResultsLiveData().observe(this.getActivity(), new Observer<PlacesResults>() {
//            @Override
//            public void onChanged(PlacesResults placesResults) {
//                if (placesResults != null) {
//                    try {
//                        Resources resources = mContext.getResources();
//                        // This loop will go through all the results and add marker on each location.
//                        for (int i = 0; i < placesResults.getResults().size(); i++) {
//                            Result result = placesResults.getResults().get(i);
//
//                            Double lat = result.getGeometry().getLocation().getLat();
//                            Double lng = result.getGeometry().getLocation().getLng();
//                            String placeName = result.getName();
//                            String vicinity = result.getVicinity();
//                            String placeId = result.getPlaceId();
//                            Boolean isOpen = null;
//                            if (result.getOpeningHours() != null) {
//                                isOpen = result.getOpeningHours().getOpenNow();
//                            }
//                            Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getPhotoUrl(), result.getRating(), 0,
//                                    isOpen, new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()));
//                            newRestaurantsList.add(restaurant);
//
//
//                            restaurantViewModel.setRestaurants(newRestaurantsList);
//                            locationViewModel.getUserLocation().observe(getViewLifecycleOwner(), locationModel -> {
//                                if (locationModel != null) {
//                                    currentLocation = locationModel.getLocation();
//                                }
//                            });
//                          /*  RestaurantHelper.getRestaurants(placeId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//                                @Override
//                                public void onSuccess(DocumentSnapshot documentSnapshot) {
//                                    Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
//
//                                }
//                            });*/
//                        }
//                    } catch (Exception e) {
//                        Log.d("onResponse", "There is an error");
//                        e.printStackTrace();
//                    }
//                }
//            }
//        });

    }

    private void viewRestaurantDetail(Restaurant restaurant) {
        final Context context = getContext();
        Intent intent = new Intent(context, PlaceDetailActivity.class);
        intent.putExtra("RESTAURANT", restaurant);
        context.startActivity(intent);
    }
}