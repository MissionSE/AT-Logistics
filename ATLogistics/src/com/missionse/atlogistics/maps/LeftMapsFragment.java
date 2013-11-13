package com.missionse.atlogistics.maps;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
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
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.missionse.atlogistics.R;

public class LeftMapsFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener,
LocationListener, OnMyLocationButtonClickListener, OnMapClickListener, OnMapLongClickListener {

	private static final LatLng HOME = new LatLng(32.865240, -80.020439);
	private static final float HOME_BEARING = 27f;

	private GoogleMap map;
	private LocationClient locationClient;

	private Polygon zoomedViewPolygon;

	private View view;

	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000)
			.setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private DualMapContainer mapContainer;
	public void setMapContainer(final DualMapContainer container) {
		mapContainer = container;
		mapContainer.setLeftMap(this);
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
			view = inflater.inflate(R.layout.fragment_map_left, container, false);
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
		locationClient.requestLocationUpdates(REQUEST, this);
	}

	@Override
	public void onDisconnected() {
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpMapIfNeeded();
		setUpLocationClientIfNeeded();
		locationClient.connect();
	}

	@Override
	public void onPause() {
		super.onPause();
		if (locationClient != null) {
			locationClient.disconnect();
		}
	}

	private void setUpMapIfNeeded() {
		if (map == null) {
			map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map_left)).getMap();
			if (map != null) {
				setUpMap();
			}
		}
	}

	private void setUpLocationClientIfNeeded() {
		if (locationClient == null) {
			locationClient = new LocationClient(getActivity(), this, this);
		}
	}

	private void setUpMap() {
		map.setMyLocationEnabled(true);
		map.setOnMyLocationButtonClickListener(this);
		map.setOnMapClickListener(this);
		map.setOnMapLongClickListener(this);
		map.setBuildingsEnabled(true);
		map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
		map.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
			@Override
			public void onMapLoaded() {
				map.animateCamera(CameraUpdateFactory
						.newCameraPosition(new CameraPosition(HOME, 15f, 0, HOME_BEARING)));
			}
		});

		map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
			@Override
			public void onMapClick(final LatLng latLng) {
				if (mapContainer.getRightMap() != null) {
					mapContainer.getRightMap().animateCamera(CameraUpdateFactory.newLatLng(latLng));
					drawZoomedViewPolygon();
				}
			}
		});

		map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(final Marker marker) {
				if (mapContainer.getRightMap() != null) {
					mapContainer.getRightMap().animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
					drawZoomedViewPolygon();
				}
				return false;
			}
		});
	}

	@Override
	public void onMapLongClick(final LatLng location) {
	}

	@Override
	public void onMapClick(final LatLng location) {
	}

	public void drawZoomedViewPolygon() {
		int fillColor = Color.argb(30, 0, 0, 0);
		float strokeWidth = 0f;
		int strokeColor = Color.GRAY;

		if (mapContainer.getRightMap() != null) {
			final VisibleRegion rightVR = mapContainer.getRightMap().getProjection().getVisibleRegion();
			final List<LatLng> rightVRpoints = new ArrayList<LatLng>();
			rightVRpoints.add(rightVR.farLeft);
			rightVRpoints.add(rightVR.farRight);
			rightVRpoints.add(rightVR.nearRight);
			rightVRpoints.add(rightVR.nearLeft);

			if (zoomedViewPolygon == null) {
				zoomedViewPolygon = map.addPolygon(new PolygonOptions()
				.addAll(rightVRpoints)
				.fillColor(fillColor)
				.strokeWidth(strokeWidth)
				.strokeColor(strokeColor));
			} else {
				zoomedViewPolygon.setFillColor(fillColor);
				zoomedViewPolygon.setStrokeWidth(strokeWidth);
				zoomedViewPolygon.setPoints(rightVRpoints);
				zoomedViewPolygon.setStrokeColor(strokeColor);
			}
		}
	}
}
