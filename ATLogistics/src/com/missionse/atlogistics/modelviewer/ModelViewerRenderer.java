package com.missionse.atlogistics.modelviewer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import rajawali.Object3D;
import rajawali.lights.DirectionalLight;
import rajawali.parser.LoaderOBJ;
import rajawali.parser.ParsingException;
import rajawali.renderer.RajawaliRenderer;
import android.content.Context;
import android.util.Log;

public class ModelViewerRenderer extends RajawaliRenderer {
	private static final String TAG = "ModelViewerRenderer";

	protected ModelViewerFragment fragment;

	private DirectionalLight directionalLight;
	private Object3D objectGroup;

	private ModelController modelController;
	private ModelAnimationController animationController;

	private int modelID;

	public ModelViewerRenderer(final Context context, final ModelViewerFragment modelViewerFragment, final int model) {
		super (context);
		setFrameRate(60);
		fragment = modelViewerFragment;
		modelID = model;
		modelController = new ModelController();
		animationController = new ModelAnimationController(this);
	}

	@Override
	public void onSurfaceCreated(final GL10 gl, final EGLConfig config) {
		fragment.showLoader();
		super.onSurfaceCreated(gl, config);
	}

	@Override
	public void onDrawFrame(final GL10 glUnused) {
		super.onDrawFrame(glUnused);
		if (fragment.loaderShown()) {
			fragment.hideLoader();
			fragment.onObjectLoaded();
		}
	}

	@Override
	protected void initScene() {

		directionalLight = new DirectionalLight();
		directionalLight.setPosition(0, 0, 4);
		directionalLight.setPower(1);

		getCurrentScene().addLight(directionalLight);
		getCurrentCamera().setZ(16);

		LoaderOBJ objParser = new LoaderOBJ(this, modelID);

		try {
			objParser.parse();
			objectGroup = objParser.getParsedObject();

			Log.d(TAG, "Number of Objects: " + objectGroup.getNumObjects());
			for (int i = 0; i < objectGroup.getNumObjects(); i++) {
				Log.d(TAG, "Object " + i + ": " + objectGroup.getChildAt(i).getName());
			}

			addChild(objectGroup);
			modelController.setObjectGroup(objectGroup);
			animationController.setObject(objectGroup);

		} catch (ParsingException e) {
			e.printStackTrace();
		}
	}

	public ModelController getController() {
		return modelController;
	}

	public ModelAnimationController getAnimator() {
		return animationController;
	}
}
