package com.missionse.atlogistics.resources;

import java.util.ArrayList;
import java.util.List;

public class ResourceManager {

	private static ResourceManager instance;

	private List<Resource> resources;
	private List<ResourceChangeListener> listeners;

	public static ResourceManager getInstance() {
		if (instance == null) {
			instance = new ResourceManager();
		}
		return instance;
	}

	private ResourceManager() {
		listeners = new ArrayList<ResourceChangeListener>();
		resources = new ArrayList<Resource>();
	}

	public List<Resource> getResources() {
		return this.resources;
	}

	public void addResource(final Resource r) {
		resources.add(r);
	}

	public void removeResource(final Resource r) {
		resources.remove(r);
	}

	public void addListener(final ResourceChangeListener listener) {
		listeners.add(listener);
		listener.onResourcesChanged();
	}

	public void notifyResourcesChanged() {
		for (ResourceChangeListener listener : listeners) {
			listener.onResourcesChanged();
		}
	}
}
