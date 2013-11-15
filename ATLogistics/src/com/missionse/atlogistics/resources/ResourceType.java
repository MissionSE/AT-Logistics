package com.missionse.atlogistics.resources;

import com.missionse.atlogistics.R;

public enum ResourceType {

	FOODWATER(R.drawable.food, "Food/Water"),
	AMMO(R.drawable.ammo, "Ammo"),
	CLOTHING(R.drawable.clothing, "Clothing"),
	FUEL(R.drawable.fuel, "Fuel"),
	MEDICAL(R.drawable.medical,"Medical"),
	HELO(R.drawable.helos, "Helo"),
	HELOLANDING(R.drawable.helo_landing, "Helo Landing"),
	SHIP(R.drawable.ussgeorgewashington, "Ship"),
	GROUND(R.drawable.ground_track, "Ground"),
	WAYPOINTS(R.drawable.ic_launcher, "Waypoint");

	private int resource;
	private String text;

	ResourceType(final int resourceId, final String description) {
		resource = resourceId;
		text = description;
	}

	public int getResourceId() {
		return resource;
	}

	public String getDescription() {
		return text;
	}

	public static CharSequence[] valuesAsCharSequence() {
		CharSequence[] value = new CharSequence[values().length];
		ResourceType[] types = values();
		for (int i = 0; i < types.length; i++) {
			value[i] = types[i].toString();
		}
		return value;
	}


}
