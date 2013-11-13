package com.missionse.atlogistics.maps;

import com.google.android.gms.maps.GoogleMap;


public class DualMapContainer {

	private LeftMapsFragment leftFragment;
	private RightMapsFragment rightFragment;

	public DualMapContainer() {

	}

	public void setLeftMap(final LeftMapsFragment fragment) {
		leftFragment = fragment;
	}

	public void setRightMap(final RightMapsFragment fragment) {
		rightFragment = fragment;
	}

	public GoogleMap getLeftMap() {
		return leftFragment.getMap();
	}

	public GoogleMap getRightMap() {
		return rightFragment.getMap();
	}

	public LeftMapsFragment getLeft() {
		return leftFragment;
	}

	public RightMapsFragment getRight() {
		return rightFragment;
	}

}
