package com.missionse.atlogistics.maps;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.missionse.atlogistics.resources.Resource;

public class ResourceMarker {

	private GoogleMap googleMap;

	private static Context context = null;

	private Marker marker;
	private Circle circle;

	ResourceMarker(final GoogleMap map, final Resource resource, final Context context) {
		googleMap = map;
		if (context != null) {
			this.context = context;
		}
		marker = googleMap.addMarker(new MarkerOptions()
				.icon(BitmapDescriptorFactory.fromBitmap(resizeBitmap(resource.getType().getResourceId())))
				.title(resource.getType().toString()).position(new LatLng(resource.getLat(), resource.getLon())));
		circle = googleMap.addCircle(new CircleOptions().center(new LatLng(resource.getLat(), resource.getLon()))
				.radius(30).fillColor(Color.RED).strokeWidth(0));
	}

	private Bitmap resizeBitmap(final int resourceID) {
		Bitmap b = BitmapFactory.decodeResource(context.getResources(), resourceID);
		Bitmap bhalfsize = Bitmap.createScaledBitmap(b, b.getWidth() / 2, b.getHeight() / 2, false);
		return bhalfsize;
	}

	public void setVisible(final boolean visible) {
		marker.setVisible(visible);
		circle.setVisible(visible);
	}

	public Marker getMarker() {
		return marker;
	}

}
