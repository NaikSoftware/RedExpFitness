package redexp.ua.redexp.view.session;

import android.Manifest;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import pl.droidsonroids.gif.AnimationListener;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;
import redexp.ua.redexp.R;
import redexp.ua.redexp.databinding.ViewSessionBinding;
import redexp.ua.redexp.viewmodel.SessionViewModel;

/**
 * Created on 3/27/2016.
 */
public class SessionFragment extends Fragment implements SessionViewModel.Callback, OnMapReadyCallback {

    private ViewSessionBinding mBinding;
    private SessionViewModel mViewModel;
    private GoogleMap mGoogleMap;
    private PolylineOptions mPolyline;
    private boolean isFirstCameraUpdate = true;

    @Override
    public void onCreate(@Nullable final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewModel = new SessionViewModel(getContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable final Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.view_session, container, false);

        initMap(mBinding.mapView, savedInstanceState);
        mBinding.setViewModel(mViewModel);

        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (actionBar != null) actionBar.setTitle(R.string.current_session);

        return mBinding.getRoot();
    }

    private void initMap(MapView mapView, Bundle savedInstanceState) {
        mPolyline = new PolylineOptions().color(0xffff0000);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        MapsInitializer.initialize(getActivity());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mGoogleMap.setMyLocationEnabled(true);
    }

    @Override
    public void addPoint(LatLng point) {
        mPolyline.add(point);
        mGoogleMap.clear();
        mGoogleMap.addPolyline(mPolyline);
        if (mGoogleMap != null) {
            if (isFirstCameraUpdate) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(point, 16));
                isFirstCameraUpdate = false;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mBinding.mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mBinding.mapView.onPause();
    }

    @Override
    public void onDestroyView() {
        mBinding.mapView.onDestroy();
        super.onDestroyView();

    }

    @Override
    public void onDestroy() {
        mViewModel.onDestroy();
        super.onDestroy();
    }
}
