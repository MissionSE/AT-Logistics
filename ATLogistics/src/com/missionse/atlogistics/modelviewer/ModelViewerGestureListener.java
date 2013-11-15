package com.missionse.atlogistics.modelviewer;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.missionse.atlogistics.gesturedetector.PanGestureDetector;
import com.missionse.atlogistics.gesturedetector.RotationGestureDetector;

public class ModelViewerGestureListener implements
		GestureDetector.OnGestureListener,
		ScaleGestureDetector.OnScaleGestureListener,
		RotationGestureDetector.OnRotationGestureListener,
		PanGestureDetector.OnPanGestureListener {

	private ModelController modelController;
	private ModelAnimationController animationController;

	public void setController(final ModelController modelController, final ModelAnimationController animationController) {
		this.modelController = modelController;
		this.animationController = animationController;
	}

	@Override
	public boolean onDown(final MotionEvent e) {
		return true;
	}

	@Override
	public boolean onScroll(final MotionEvent e1, final MotionEvent e2, final float distanceX, final float distanceY) {
		if (e2.getPointerCount() == 1) {
			modelController.translate(-distanceX / 100.0f, distanceY / 100.0f, 0);
		}
		return true;
	}

	@Override
	public boolean onScale(final ScaleGestureDetector detector) {
		modelController.scale(detector.getScaleFactor());
		return true;
	}

	@Override
	public boolean onScaleBegin(final ScaleGestureDetector detector) {
		return true;
	}

	@Override
	public void onScaleEnd(final ScaleGestureDetector detector) {
	}

	@Override
	public boolean onFling(final MotionEvent e1, final MotionEvent e2, final float velocityX, final float velocityY) {
		return false;
	}

	@Override
	public void onLongPress(final MotionEvent e) {
		if (animationController.isRotating()) {
			animationController.stopRotation();
		} else {
			animationController.startXRotation(2000);
		}
	}

	@Override
	public void onShowPress(final MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(final MotionEvent e) {
		return false;
	}

	@Override
	public boolean onRotate(final RotationGestureDetector detector, final float angle) {
		modelController.rotate(0f, 0f, -detector.getAngle());
		return true;
	}

	@Override
	public boolean onRotateBegin(final RotationGestureDetector detector, final float angle) {
		return true;
	}

	@Override
	public void onRotateEnd() {
	}

	@Override
	public boolean onPan(final PanGestureDetector detector, final float distanceX, final float distanceY) {
		modelController.rotate(-distanceX / 3f, -distanceY / 3f, 0);
		return true;
	}

	@Override
	public boolean onPanBegin(final PanGestureDetector detector, final float distanceX, final float distanceY) {
		return true;
	}

	@Override
	public void onPanEnd() {
	}
}
