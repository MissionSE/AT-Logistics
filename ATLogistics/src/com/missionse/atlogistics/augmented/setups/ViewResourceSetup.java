package com.missionse.atlogistics.augmented.setups;

import gl.Color;
import gl.GLFactory;
import gl.GLRenderer;
import gl.animations.AnimationFaceToCamera;
import gl.scenegraph.MeshComponent;
import gl.scenegraph.Shape;
import gui.GuiSetup;

import java.util.Random;

import markerDetection.MarkerObjectMap;
import util.Vec;
import v2.simpleUi.util.IO;
import worldData.Obj;
import worldData.World;
import android.app.Activity;
import android.graphics.Typeface;
import android.widget.TextView;

import com.missionse.atlogistics.resources.Resource;
import com.missionse.atlogistics.resources.ResourceManager;
import com.missionse.atlogistics.resources.ResourceType;

public class ViewResourceSetup extends DefaultMultiSetup {

	public ViewResourceSetup(final Activity parent) {
		super(parent, false);

	}

	@Override
	public void _a4_addDefaultMarkers(final MarkerObjectMap markerObjectMap) {
		// TODO Auto-generated method stub

	}

	@Override
	public void _b2_addDefaultWorldObjects(final World world) {
//		List<Resource> resources = ResourceManager.getInstance().getResources();
//		for(Resource r : resources){
//			MeshComponent m = GLFactory.getInstance().newTexturedSquare(
//					r.getResourceName(),
//					IO.loadBitmapFromId(
//							getActivity(), 
//							r.getType().getResourceId()));
//			
//			m.addChild(new AnimationFaceToCamera(getCamera(),0.5f));
//			m.setScale(new Vec(2,2,2));
//			m.setPosition(new Vec(0,0,2));
//			m.setColor(Color.green());
//			TextView v = new TextView(getActivity());
//			v.setTypeface(null, Typeface.BOLD);
//			v.setTextSize(9);
//			StringBuilder sb = new StringBuilder();
//			sb.append(r.getResourceName() + ": ");
//			if(r.getFlavorText().equals("")){
//				sb.append("(100%)");
//			}else{
//				sb.append("(" + r.getFlavorText() + ")");
//			}
//			v.setText(sb.toString());
//
//			Random ran = new Random();
//
//			MeshComponent mesh = GLFactory.getInstance().newTexturedSquare(
//					"txtView" + r.getResourceName() + r.getFlavorText() + ran.nextInt(100) , 
//					util.IO.loadBitmapFromView(v), 
//					1);
//			mesh.setColor(Color.green());
//			mesh.addAnimation(new AnimationFaceToCamera(getCamera()));
//
//			final MeshComponent itemMesh = new Shape();
//			itemMesh.addChild(m);
//			itemMesh.addChild(mesh);
//			
//			Obj o = new Obj();
//			o.setComp(itemMesh);
//			o.setPosition(Vec.getNewRandomPosInXYPlane(getCamera().getPosition(), 15, 50));
//			//o.setMyLatitude(Vec.);
//			//o.setMyLongitude(r.getLon());
//			//o.setMyAltitude(getCamera().getGPSPositionAsGeoObj().getAltitude());
//			world.add(o);
//		}
		int dist = 20;
		int alt = 4;
		int count = 0;
		for(Resource r : ResourceManager.getInstance().getResources()){
			if(r.getType() == ResourceType.SHIP)
				continue;
			if(count == 0 ){
				addResourceToWorld(new Vec(0,dist,alt),r,world);
			}else if(count == 1){
				addResourceToWorld(new Vec(dist,0,alt),r,world);
			}else if(count == 2){
				addResourceToWorld(new Vec(dist,dist,alt),r,world);
			}
			
			count++;
		}
		
//		Resource r = new Resource("Ammo", ResourceType.AMMO, 0, 0,"740 Rounds");
//		addResourceToWorld(new Vec(0,dist,alt),r,world);
//		
//		Resource r1 = new Resource(ResourceType.MEDICAL);
//		addResourceToWorld(new Vec(dist,0,alt),r1,world);
//		
//		Resource r2 = new Resource(ResourceType.FUEL);
//		addResourceToWorld(new Vec(dist,dist,alt),r2,world);
		
		
		
		

	}
	
	private void addResourceToWorld(Vec p, Resource r, World world){
		MeshComponent m = GLFactory.getInstance().newTexturedSquare(
				r.getResourceName(),
				IO.loadBitmapFromId(
						getActivity(), 
						r.getType().getResourceId()));
		
		m.addChild(new AnimationFaceToCamera(getCamera(),0.5f));
		m.setScale(new Vec(2,2,2));
		m.setPosition(new Vec(0,0,2));
		m.setColor(Color.green());
		TextView v = new TextView(getActivity());
		v.setTypeface(null, Typeface.BOLD);
		v.setTextSize(9);
		StringBuilder sb = new StringBuilder();
		sb.append(r.getResourceName() + ": ");
		if(r.getFlavorText().equals("")){
			sb.append("(100%)");
		}else{
			sb.append("(" + r.getFlavorText() + ")");
		}
		v.setText(sb.toString());

		Random ran = new Random();

		MeshComponent mesh = GLFactory.getInstance().newTexturedSquare(
				"txtView" + r.getResourceName() + r.getFlavorText() + ran.nextInt(100) , 
				util.IO.loadBitmapFromView(v), 
				1);
		mesh.setColor(Color.green());
		mesh.addAnimation(new AnimationFaceToCamera(getCamera()));

		final MeshComponent itemMesh = new Shape();
		itemMesh.addChild(m);
		itemMesh.addChild(mesh);
		itemMesh.setPosition(p);
		
		Obj o = new Obj();
		o.setComp(itemMesh);
		//o.setPosition(Vec.getNewRandomPosInXYPlane(getCamera().getPosition(), 15,20));
		//o.setPosition(p);
		//o.setMyLatitude(Vec.);
		//o.setMyLongitude(r.getLon());
		//o.setMyAltitude(getCamera().getGPSPositionAsGeoObj().getAltitude());
		world.add(o);
	}
	
	private Vec getFacingCamera(){
		Random ran = new Random();
		
		return new Vec(ran.nextInt(10), ran.nextInt(5),4);

	}

	@Override
	public void _e3_addElementsToUi(final GuiSetup guiSetup, final Activity activity) {
		// TODO Auto-generated method stub

	}

}
