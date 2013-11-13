package com.missionse.atlogistics.popup;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.MediaStore;

import com.missionse.atlogistics.R;
import com.missionse.atlogistics.resources.Resource;
import com.missionse.atlogistics.resources.ResourceManager;
import com.missionse.atlogistics.resources.ResourceType;

public class PopupManager {
	
	public static void addResource(Activity base, double lat, double lon){
		AlertDialog.Builder builder = new AlertDialog.Builder(base);
		builder.setTitle(R.string.identify_resource).setItems(ResourceType.valuesAsCharSequence(),
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(final DialogInterface dialog, final int which) {
						ResourceType resourceType = ResourceType.values()[which];
						Resource resource = new Resource(resourceType.getDescription(),resourceType);
						ResourceManager.getInstance().addResource(resource);
						
						//TODO: Need to add handler
					}
				});
		builder.create();
		builder.show();

	}

}
