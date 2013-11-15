package com.missionse.atlogistics.maps;

import java.util.HashMap;

import android.app.Activity;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.missionse.atlogistics.R;
import com.missionse.atlogistics.resources.Resource;
import com.missionse.atlogistics.resources.ResourceManager;

public class CustomInfoWindowAdapter implements InfoWindowAdapter {

	// These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
	// "title" and "snippet".
	private final View mWindow;
	private final View mContents;
	private final HashMap<Resource, ResourceMarker> resourceMarkers;

	CustomInfoWindowAdapter(final Activity activity, final HashMap<Resource, ResourceMarker> markers) {
		mWindow = activity.getLayoutInflater().inflate(R.layout.custom_info_window, null);
		mContents = activity.getLayoutInflater().inflate(R.layout.custom_info_contents, null);
		resourceMarkers = markers;
	}

	@Override
	public View getInfoWindow(final Marker marker) {
		render(marker, mWindow);
		return mWindow;
	}

	@Override
	public View getInfoContents(final Marker marker) {
		render(marker, mContents);
		return mContents;
	}

	private void render(final Marker marker, final View view) {
		//int badge = 0;

		//for (Resource resource : ResourceManager.getInstance().getResources()) {
		//	if (resourceMarkers.get(resource).equals(marker)) {
		//		badge = resource.getType().getResourceId();
		//		break;
		//	}
		//}

		//((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

		TextView amountUi = ((TextView) view.findViewById(R.id.amount));
		for (Resource resource : ResourceManager.getInstance().getResources()) {
			if (resourceMarkers.get(resource).getMarker().equals(marker)) {
				SpannableString titleText = new SpannableString(resource.getType().getDescription());
				titleText.setSpan(new ForegroundColorSpan(Color.BLUE), 0, titleText.length(), 0);

				TextView titleUi = ((TextView) view.findViewById(R.id.title));
				titleUi.setText(titleText);

				String text = resource.getFlavorText() + " (" + resource.getPercent() + "%)";
				int color = Color.BLACK;
				if (resource.getPercent() < 25.0f) {
					color = Color.RED;
				}

				SpannableString flavorText = new SpannableString(text);
				flavorText.setSpan(new ForegroundColorSpan(color), 0, flavorText.length(), 0);
				amountUi.setText(flavorText);
				break;
			}
		}

	}
}
