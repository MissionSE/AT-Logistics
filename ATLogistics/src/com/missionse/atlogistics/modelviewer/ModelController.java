package com.missionse.atlogistics.modelviewer;

import java.util.ArrayList;

import rajawali.Object3D;
import rajawali.materials.Material;
import rajawali.math.vector.Vector3;


public class ModelController {

	private Object3D objectGroup;
	private ArrayList<Object3D> objectList;

	private boolean rotationLocked, scaleLocked, translationLocked;

	public ModelController() {
	}

	public void setObjectGroup(final Object3D group) {
		objectGroup = group;
	}

	public void lockRotation() {
		rotationLocked = true;
	}

	public void unlockRotation() {
		rotationLocked = false;
	}

	public boolean isRotationLocked() {
		return rotationLocked;
	}

	public void lockScale() {
		scaleLocked = true;
	}

	public void unlockScale() {
		scaleLocked = false;
	}

	public boolean isScaleLocked() {
		return scaleLocked;
	}

	public void lockTranslation() {
		translationLocked = true;
	}

	public void unlockTranslation() {
		translationLocked = false;
	}

	public boolean isTranslationLocked() {
		return translationLocked;
	}

	public void rotate(final float x, final float y, final float z) {
		if (!rotationLocked && objectGroup != null) {
			objectGroup.rotateAround(Vector3.Y, x);
			objectGroup.rotateAround(Vector3.X, y);
			objectGroup.rotateAround(Vector3.Z, z);
		}
	}

	public void scale(final float scaleFactor) {
		scale (scaleFactor, scaleFactor, scaleFactor);
	}

	public void scale(final float x, final float y, final float z) {
		if (!scaleLocked && objectGroup != null) {
			Vector3 scale = objectGroup.getScale();
			objectGroup.setScaleX(scale.x * x);
			objectGroup.setScaleY(scale.y * y);
			objectGroup.setScaleZ(scale.z * z);
		}
	}

	public void translate(final float x, final float y, final float z) {
		if (!translationLocked && objectGroup != null) {
			objectGroup.setX(objectGroup.getX() + x);
			objectGroup.setY(objectGroup.getY() + y);
			objectGroup.setZ(objectGroup.getZ() + z);
		}
	}

	public void setRotation(final float x, final float y, final float z) {
		if (!rotationLocked && objectGroup != null) {
			objectGroup.setRotX(x);
			objectGroup.setRotY(y);
			objectGroup.setRotZ(z);
		}
	}

	public void setScale(final float x, final float y, final float z) {
		if (!scaleLocked && objectGroup != null) {
			objectGroup.setScaleX(x);
			objectGroup.setScaleY(y);
			objectGroup.setScaleZ(z);
		}
	}

	public void setPosition(final float x, final float y, final float z) {
		if (!translationLocked && objectGroup != null) {
			objectGroup.setX(x);
			objectGroup.setY(y);
			objectGroup.setZ(z);
		}
	}

	public void reset() {
		if (objectGroup != null) {
			objectGroup.setPosition(new Vector3());
			objectGroup.setRotation(new Vector3());
			objectGroup.setScale(1);
		}
	}

	public void center() {
		if (objectGroup != null) {
			objectGroup.setPosition(new Vector3());
		}
	}

	public int getAmbientColor(final String objectName) {
		int color = -1;
		if (objectGroup != null) {
			Object3D object = objectGroup.getChildByName(objectName);
			if (null != object) {
				Material material = object.getMaterial();
				if (null != material) {
					color = material.getAmbientColor();
				}
			}
		}

		return color;
	}

	public boolean setAmbientColor(final String objectName, final int color) {
		boolean successful = false;
		if (objectGroup != null) {
			Object3D object = objectGroup.getChildByName(objectName);
			if (null != object) {
				Material material = object.getMaterial();
				if (null != material) {
					material.setAmbientColor(color);
					successful = true;
				}
			}
		}

		return successful;
	}

	public ArrayList<String> getObjectList() {
		ArrayList<String> objectNames = new ArrayList<String>();

		if (objectList != null) {
			for (Object3D object : objectList) {
				if (object.getName() != null) {
					objectNames.add(object.getName());
				}
			}
		}
		return objectNames;
	}

	public float getXRotation() {
		float xRotation = 0.0f;
		if (objectGroup != null) {
			xRotation = (float) objectGroup.getRotX();
		}
		return xRotation;
	}

	public float getYRotation() {
		float yRotation = 0.0f;
		if (objectGroup != null) {
			yRotation = (float) objectGroup.getRotY();
		}
		return yRotation;
	}

	public float getZRotation() {
		float zRotation = 0.0f;
		if (objectGroup != null) {
			zRotation = (float) objectGroup.getRotZ();
		}
		return zRotation;
	}

	public float getXScale() {
		float xScale = 0.0f;
		if (objectGroup != null) {
			xScale = (float) objectGroup.getScaleX();
		}
		return xScale;
	}

	public float getYScale() {
		float yScale = 0.0f;
		if (objectGroup != null) {
			yScale = (float) objectGroup.getScaleY();
		}
		return yScale;
	}

	public float getZScale() {
		float zScale = 0.0f;
		if (objectGroup != null) {
			zScale = (float) objectGroup.getScaleZ();
		}
		return zScale;
	}

	public float getXPosition() {
		float xPosition = 0.0f;
		if (objectGroup != null) {
			xPosition = (float) objectGroup.getX();
		}
		return xPosition;
	}

	public float getYPosition() {
		float yPosition = 0.0f;
		if (objectGroup != null) {
			yPosition = (float) objectGroup.getY();
		}
		return yPosition;
	}

	public float getZPosition() {
		float zPosition = 0.0f;
		if (objectGroup != null) {
			zPosition = (float) objectGroup.getZ();
		}
		return zPosition;
	}
}
