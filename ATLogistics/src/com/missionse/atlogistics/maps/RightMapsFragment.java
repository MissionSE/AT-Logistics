package com.missionse.atlogistics.maps;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Fragment;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.missionse.atlogistics.ATLogistics;
import com.missionse.atlogistics.R;
import com.missionse.atlogistics.resources.Resource;
import com.missionse.atlogistics.resources.ResourceChangeListener;
import com.missionse.atlogistics.resources.ResourceManager;
import com.missionse.atlogistics.resources.ResourceType;

public class RightMapsFragment extends Fragment implements ConnectionCallbacks, OnConnectionFailedListener,
		LocationListener, OnMyLocationButtonClickListener, OnMapClickListener, OnMapLongClickListener,
		ResourceChangeListener {

	private static final LatLng HOME = new LatLng(11.05, 124.367);
	private static final float HOME_BEARING = 27f;

	private GoogleMap map;
	private LocationClient mLocationClient;

	private View view;

	private HashMap<Resource, ResourceMarker> markers;
	private HashMap<ResourceType, Boolean> markerVisibilities;

	private boolean isLoaded = false;

	private Polyline waypoint1, waypoint2;
	private boolean waypointsVisible = false;

	private static final LocationRequest REQUEST = LocationRequest.create().setInterval(5000).setFastestInterval(16) // 16ms = 60fps
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

	private DualMapContainer mapContainer;

	public RightMapsFragment() {
		markers = new HashMap<Resource, ResourceMarker>();
		markerVisibilities = new HashMap<ResourceType, Boolean>();

		for (ResourceType resourceType : ResourceType.values()) {
			markerVisibilities.put(resourceType, Boolean.TRUE);
		}
	}

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
						.newCameraPosition(new CameraPosition(HOME, 5.2f, 0, HOME_BEARING)));
				isLoaded = true;
			}
		});
		map.setOnCameraChangeListener(new OnCameraChangeListener() {
			@Override
			public void onCameraChange(final CameraPosition posRight) {
				if (mapContainer.getLeftMap() != null) {
					mapContainer.getLeftMap().animateCamera(CameraUpdateFactory.zoomTo(posRight.zoom - 1));

				}
				if (mapContainer.getLeft() != null) {
					mapContainer.getLeft().drawZoomedViewPolygon();
				}
			}
		});
		map.setInfoWindowAdapter(new CustomInfoWindowAdapter(getActivity(), markers));

		map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
			@Override
			public void onInfoWindowClick(final Marker marker) {
				for (Entry<Resource, ResourceMarker> entry : markers.entrySet()) {
					if (entry.getValue().getMarker().equals(marker)) {
						((ATLogistics) getActivity()).onResourceClicked(entry.getKey());
					}
				}
				for (Entry<Resource, ResourceMarker> entry : markers.entrySet()) {
					if (entry.getValue().getMarker().equals(marker)) {
						if (entry.getKey().getType().equals(ResourceType.HELO)) {
							setWaypointVisibility(!waypointsVisible);
						}
					}
				}
			}
		});

		waypoint1 = map.addPolyline(new PolylineOptions().add(new LatLng(10.2017, 128.3524), new LatLng(13.6041, 123.0000))
				.width(10)
				.color(Color.BLUE));
		waypoint2 = map.addPolyline(new PolylineOptions().add(new LatLng(13.6041, 123.0000), new LatLng(11.05, 124.367))
				.width(10)
				.color(Color.BLUE));
		setWaypointVisibility(false);

		ResourceManager.getInstance().addListener(this);
	}

	public void setWaypointVisibility(final boolean visible) {
		if (waypoint1 != null && waypoint2 != null) {
			waypoint1.setVisible(visible);
			waypoint2.setVisible(visible);
			waypointsVisible = visible;
		}
	}

	@Override
	public void onMapLongClick(final LatLng location) {
	}

	@Override
	public void onMapClick(final LatLng location) {
		Log.e("RightMap", "onMapClick: " + location);
	}

	@Override
	public void onResourcesChanged() {
		for (Resource resource : ResourceManager.getInstance().getResources()) {
			ResourceMarker marker = new ResourceMarker(map, resource, getActivity());
			markers.put(resource, marker);
		}
	}

	public void setResourceVisibility(final ResourceType resourceType, final boolean isChecked) {
		markerVisibilities.put(resourceType, Boolean.valueOf(isChecked));
		for (Map.Entry<Resource, ResourceMarker> entry : markers.entrySet()) {
			Resource r = entry.getKey();
			ResourceMarker m = entry.getValue();
			if (r.getType() == resourceType) {
				m.setVisible(isChecked);
			}
		}
	}

	public void showMarker(final int sendId) {
		for (Entry<Resource, ResourceMarker> entry : markers.entrySet()) {
			if (entry.getKey().getId() == sendId) {
				entry.getValue().getMarker().showInfoWindow();
				map.animateCamera(CameraUpdateFactory.newLatLng(entry.getValue().getMarker().getPosition()), 250, null);
			}
		}
	}

	public boolean isMapLoaded() {
		return isLoaded;
	}
}
