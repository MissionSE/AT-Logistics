package com.missionse.atlogistics.maps;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.missionse.atlogistics.R;

public class RightMapsFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener,
LocationListener, OnMyLocationButtonClickListener, OnMapClickListener, OnMapLongClickListener {

	private static final LatLng HOME = new LatLng(32.865240, -80.020439);
	private static final float HOME_BEARING = 27f;

	private GoogleMap map;
	private LocationClient mLocationClient;

	private View view;

	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000)
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private DualMapContainer mapContainer;
	public void setMapContainer(final DualMapContainer container) {
		mapContainer = container;
		mapContainer.setRightMap(this);
	}

	public GoogleMap getMap() {
		return map;
	}

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null) {
				parent.removeView(view);
			}
		}
		try {
			view = inflater.inflate(R.layout.fragment_map_right, container, false);
		} catch (InflateException e) {
			// Map already exists.
		}

		return view;
	}

	@Override
	public boolean onMyLocationButtonClick() {
		return false;
	}

	@Override
	public void onLocationChanged(final Location location) {
	}

	@Override
	public void onConnectionFailed(final ConnectionResult result) {
	}

	@Override
	public void onConnected(final Bundle bundle) {
		mLocationClient.requestLocationUpdates(REQUEST, this);
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		mLocationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mLocationClient != null) {
			mLocationClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_right)).getMap();
			if (map != null) {
				setUpMap();
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (mLocationClient == null) {
			mLocationClient = new LocationClient(getActivity(), this, this);
		}
	}

	private void setUpMap() {
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(this);
		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);
		map.setBuildingsEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
		map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				map.animateCamera(CameraUpdateFactory
						.newCameraPosition(new CameraPosition(HOME, 17.5f, 0, HOME_BEARING)));
			}
		});
		map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(final CameraPosition posRight) {
				if (mapContainer.getLeftMap() != null) {
						mapContainer.getLeftMap().animateCamera(CameraUpdateFactory.zoomTo(posRight.zoom - 3));
				}
				if (mapContainer.getLeft() != null) {
					mapContainer.getLeft().drawZoomedViewPolygon();
				}
			}
		});
	}

	@Override
	public void onMapLongClick(final LatLng location) {
	}

	@Override
	public void onMapClick(final LatLng location) {
		Log.e("RightMap", "onMapClick: " + location);
	}
}
