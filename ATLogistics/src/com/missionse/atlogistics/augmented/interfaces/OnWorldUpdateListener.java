package com.missionse.atlogistics.augmented.interfaces;

import java.util.List;

import com.missionse.atlogistics.augmented.setups.DefaultMultiSetup;
import com.missionse.atlogistics.resources.Resource;

public interface OnWorldUpdateListener {
	
	public void onWorldUpdate(DefaultMultiSetup setup);
	
	public List<Resource> getAssetObjectsForWorld();

}
