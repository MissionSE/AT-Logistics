package com.missionse.atlogistics.augmented.setups;

import geo.GeoObj;
import gl.CustomGLSurfaceView;
import gl.GL1Renderer;
import gl.GLCamera;
import gl.GLFactory;
import gui.GuiSetup;

import java.util.LinkedList;
import java.util.List;

import markerDetection.MarkerDetectionSetup;
import markerDetection.MarkerObjectMap;
import markerDetection.UnrecognizedMarkerListener;
import system.EventManager;
import util.Vec;
import worldData.SystemUpdater;
import worldData.World;
import actions.Action;
import actions.ActionCalcRelativePos;
import actions.ActionMoveCameraBuffered;
import actions.ActionRotateCameraBuffered;
import actions.ActionRotateCameraBufferedDirect;
import actions.ActionWASDMovement;
import actions.ActionWaitForAccuracy;
import android.app.Activity;
import android.location.Location;
import android.util.Log;

import com.missionse.atlogistics.augmented.interfaces.OnWorldUpdateListener;

public abstract class DefaultMultiSetup extends MarkerDetectionSetup {
	
	public static String LOG_TAG = "DefaultMultiSetup";
	
	public volatile GLCamera mCamera;
	public volatile World mWorld;
	private Activity mParentActivity;
	private volatile MarkerObjectMap mMarkerMap;
	private Thread mWorldUpdateThread;
	private volatile boolean mRunUpdateThread;
	private List<OnWorldUpdateListener> mWorldUpdateListeners;
	private GL1Renderer mRenderer;
	
	private boolean mWaitForGps;
	private ActionWaitForAccuracy  mMinAccuracyAction;
	private Action mRotatedGLCameraAction;

	private ActionWASDMovement wasdAction;

	private ActionRotateCameraBuffered rotateGLCameraAction;
	
	public DefaultMultiSetup(Activity parent, boolean waitForGps){
		super(true);
		
		mMarkerMap = new MarkerObjectMap();
		mRunUpdateThread = false;
		mWorldUpdateListeners = new LinkedList<OnWorldUpdateListener>();
		mWaitForGps = waitForGps;
		mParentActivity = parent;
		
	}
	
	@Override
	public void _a_initFieldsIfNecessary(){
		mCamera = new GLCamera(new Vec(0,0,7));
		mWorld = new World(mCamera);
	}
	
	
	@Override
	public UnrecognizedMarkerListener _a2_getUnrecognizedMarkerListener() {
		return new UnrecognizedMarkerListener() {
			@Override
			public void onUnrecognizedMarkerDetected(int markerCode,
					float[] mat, int startIdx, int endIdx, int rotationValue) {
				System.out.println("unrecognized markerCode=" + markerCode);
				Log.w(getClass().getSimpleName(), "unrecognized markerCode=" + markerCode);
			}
		};
	}
	
	@Override
	public void _a3_registerMarkerObjects(MarkerObjectMap markerObjectMap) {
		markerObjectMap.putAll(mMarkerMap);
		mMarkerMap = markerObjectMap;
		_a4_addDefaultMarkers(mMarkerMap);
	}
	
	public abstract void _a4_addDefaultMarkers(MarkerObjectMap markerObjectMap);
	
	
	@Override
	public void _b_addWorldsToRenderer(GL1Renderer renderer,
			GLFactory objectFactory, GeoObj currentPosition) {
		mRenderer = renderer;
		
		if(!mWaitForGps){
			_b2_addDefaultWorldObjects(mWorld);
		}
		
		renderer.addRenderElement(mWorld);
		
	}

	public abstract void _b2_addDefaultWorldObjects(World world);
	
	@Override
	public void onPause(Activity a) {
		super.onPause(a);
		mRunUpdateThread = false;
		
	}
	
	@Override
	public void onResume(Activity a) {
		super.onResume(a);
		mRunUpdateThread = true;
		if(mWorldUpdateThread == null || !mWorldUpdateThread.isAlive()){
			mWorldUpdateThread = new Thread(new Runnable(){
				@Override
				public void run(){
					while(mRunUpdateThread){
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						if(mRunUpdateThread){
							//getWorld().add(MeshComponentFactory.createDefaultInfo(getActivity(), getCamera()));
							//new CommandShowToast(getActivity(), "ADDING TO WORLD").execute();
							notifyWorldUpdateListeners();
						}
					}
				}
			});
			mWorldUpdateThread.start();
		}
	}
	
	public void notifyWorldUpdateListeners(){
		for(OnWorldUpdateListener l : mWorldUpdateListeners){
			//l.onWorldUpdate(getActivity(), getCamera(), getWorld(), markermap);
			//TODO: Update interface to base in this
		}
	}
	
	public void addOnWorldUpdateListener(OnWorldUpdateListener l){
		mWorldUpdateListeners.add(l);
	}
	
	public void removeWorldUpdateListener(OnWorldUpdateListener l){
		mWorldUpdateListeners.remove(l);
	}
	
	@Override
	public void _c_addActionsToEvents(final EventManager eventManager,
			CustomGLSurfaceView arView, SystemUpdater updater) {
		arView.addOnTouchMoveListener(new ActionMoveCameraBuffered(getCamera(), 5,
				25));
		Action rot = new ActionRotateCameraBuffered(getCamera());
		updater.addObjectToUpdateCycle(rot);
		eventManager.addOnOrientationChangedAction(rot);
		eventManager.addOnTrackballAction(new ActionMoveCameraBuffered(getCamera(),
				1, 25));
		eventManager.addOnLocationChangedAction(new ActionCalcRelativePos(
				getWorld(), getCamera()));
		
		if(mWaitForGps){
			Log.d(LOG_TAG, "Adding min. accuracy action to eventmanager.");
			mMinAccuracyAction = new ActionWaitForAccuracy(getActivity(), 26.0f, 10) {
				@Override
				public void minAccuracyReachedFirstTime(Location l,
						ActionWaitForAccuracy a) {
					Log.d(LOG_TAG, "min. accuracy has been reached.");
					_b2_addDefaultWorldObjects(getWorld());
					if (!eventManager.getOnLocationChangedAction().remove(this)) {
						Log.e(LOG_TAG,
								"Could not remove minAccuracyAction from the onLocationChangedAction list");
					}
				}
			};
			
			eventManager.addOnLocationChangedAction(mMinAccuracyAction);
		}
		
		mRotatedGLCameraAction= new ActionRotateCameraBuffered(getCamera());
		eventManager.addOnOrientationChangedAction(mRotatedGLCameraAction);
		
	}
	
	@Override
	public void _d_addElementsToUpdateThread(SystemUpdater updater) {
		updater.addObjectToUpdateCycle(mWorld);
		updater.addObjectToUpdateCycle(mRotatedGLCameraAction);
	}
	
	@Override
	public void _e2_addElementsToGuiSetup(GuiSetup guiSetup, Activity activity) {
		
		if(mWaitForGps && mMinAccuracyAction != null)
			guiSetup.addViewToTop(mMinAccuracyAction.getView());
		
		_e3_addElementsToUi(guiSetup, activity);
	}

	public abstract void _e3_addElementsToUi(GuiSetup guiSetup, Activity activity);
	

	public World getWorld(){
		return mWorld;
	}
	
	public GLCamera getCamera(){
		return mCamera;
	}
	
	public MarkerObjectMap getMarkerMap(){
		return mMarkerMap;
	}
	
	public Activity getParentActivity(){
		return mParentActivity;
	}
	
	public GL1Renderer getRenderer(){
		return this.mRenderer;
	}
	
}
