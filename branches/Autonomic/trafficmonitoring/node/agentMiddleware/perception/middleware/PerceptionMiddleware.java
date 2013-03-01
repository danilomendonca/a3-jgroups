package node.agentMiddleware.perception.middleware;


import java.util.*;

import node.agentMiddleware.perception.interfaces.*;
import node.developer.sensors.*;

import utilities.NodeID;

/**
 * One perception middleware per node!
 * Each perception middleware is directly connected with its camera.
 * 
 * @author Robrecht
 *
 */
public class PerceptionMiddleware implements Perception, Runnable{
	
	private Camera camera;
	private Vector<NeighbourPerceive> neighbourPerceivers;
	private Vector<TrafficJamInfoPerceive> trafficJamPerceivers;
	
	
	public PerceptionMiddleware(Camera camera){
		this.camera = camera;
		neighbourPerceivers = new Vector<NeighbourPerceive>();
		trafficJamPerceivers = new Vector<TrafficJamInfoPerceive>();
	}
	
	/*******************************
	 *                             *
	 * PRIVATE AND PACKAGE METHODS *
	 *                             *
	 *******************************/	
	
	//NIET CONSTANT DOORSTUREN!!!
	
	//Traffic threshold al in rekening brengen
	
	//THRESHOLD == 0.3
	
	private ArrayList<NodeID> neighbourCache;
	
	/*
	 * Only sends when neighbours have changed!!!
	 */
	private synchronized void neighbourPerceptStep(){
		if(neighbourCache == null || !neighbourCache.equals(camera.getAliveNeighbourIDlist())){
			//System.out.println("*** update neighbours on "+this.getLocalCamera().getAgentID());
			this.neighbourCache = camera.getAliveNeighbourIDlist();					
			for(Enumeration<NeighbourPerceive> e = neighbourPerceivers.elements(); e.hasMoreElements(); ){				
				NeighbourPerceive perceiver = e.nextElement();
				perceiver.changeNeighbours(camera.getAliveNeighbourIDlist());
			}
		}
	}
	
	private float threshold = (float)0.8;
	
	//-1: undefined (start)
	//0: no traffic
	//1: normal traffic
	//2: congested
	private int trafficState = -1;
	private float velocityCache = -1;
	
	
	private int stateTimer = 0;
	private int nbTimesOnOtherSideOfThreshold = 0;
	
	/*
	 * Only sends when traffic is passes threshold in any direction!!
	 * Threshold = 0.3
	 */
	private synchronized void trafficJamPerceptStep(){
		
			int newState = -1;
			
			if((float)camera.getAvgVelocity() < threshold && camera.getDensity() != 0){				
				newState = 2;
			}else if((float)camera.getAvgVelocity() >= threshold){
				newState = 1;
			}else{
				newState = 0;
			}
		
			if(newState != trafficState){
				nbTimesOnOtherSideOfThreshold++;
			}else{
				nbTimesOnOtherSideOfThreshold = 0;
			}
			
			if(nbTimesOnOtherSideOfThreshold > 100){				
				nbTimesOnOtherSideOfThreshold = 0;
				trafficState = newState;
				
				//System.out.println("$$$ update traffic on "+this.getLocalCamera().getAgentID()+" "+camera.getAvgVelocity());
				
				//@Pieter
				sendTrafficJamInfo();
			}
						
//			int newState = -1;
//			
//			if((float)camera.getAvgVelocity() < threshold && camera.getDensity() != 0){
//				newState = 2;
//			}else if((float)camera.getAvgVelocity() >= threshold){
//				newState = 1;
//			}else{
//				newState = 0;
//			}
//		
//			if(stateTimer > 50){
//				stateTimer = 0;
//				//System.out.println("==="+this.getLocalCamera().getAgentID());
//				if(true || newState != trafficState){
//					trafficState = newState;
//			
//					//System.out.println("$$$ update traffic on "+this.getLocalCamera().getAgentID());
//					
//					for(Enumeration<TrafficJamInfoPerceive> e = trafficJamPerceivers.elements(); e.hasMoreElements(); ){
//						TrafficJamInfoPerceive perceiver = e.nextElement();
//						perceiver.setAvgVelocity((float)camera.getAvgVelocity());
//						perceiver.setDensity((float)camera.getDensity());
//						perceiver.setIntensity((float)camera.getIntensity());
//						
//						count++;
//						if(count%20 == 0)
//							System.out.println(count);
//					}
//				}
//			}
//			
//			stateTimer++;
	}

	//@Pieter
	public void sendTrafficJamInfo() {
		for(Enumeration<TrafficJamInfoPerceive> e = trafficJamPerceivers.elements(); e.hasMoreElements(); ){
			TrafficJamInfoPerceive perceiver = e.nextElement();
			perceiver.setAvgVelocity((float)camera.getAvgVelocity());
			perceiver.setDensity((float)camera.getDensity());
			perceiver.setIntensity((float)camera.getIntensity());
			
		}
	}
	
	/*
	 * Only sends when traffic is passes threshold in any direction!!
	 * Threshold = 0.3
	 */
	private synchronized void trafficJamPerceptStepBU(){
		
			int newState = -1;
			
			if((float)camera.getAvgVelocity() < threshold && camera.getDensity() != 0){
				newState = 2;
			}else if((float)camera.getAvgVelocity() >= threshold){
				newState = 1;
			}else{
				newState = 0;
			}
			
			if(newState == trafficState && stateTimer <55){
				stateTimer ++;
			}
			if(newState != trafficState){
				trafficState = newState;
				stateTimer = 0;
			}
		
			//if(stateTimer<51)
			//	System.out.println(">>> stateTimer "+stateTimer+" on : "+this.getLocalCamera().getAgentID());
			
			if(stateTimer == 50){
				//trafficState = newState;
				//System.out.println("$$$ update traffic on "+this.getLocalCamera().getAgentID());
				
				sendTrafficJamInfo();
			}
		
	}
	
	/*
	 * Only sends when traffic is passes threshold in any direction!!
	 * Threshold = 0.3
	 */
	private synchronized void trafficJamPerceptStepBUBU(){
		if(velocityCache <0 || Math.abs(velocityCache - (float)camera.getAvgVelocity()) > 0.4 ){
			//System.out.println("^^^ velocity change on "+this.getLocalCamera().getAgentID()+" "+camera.getAvgVelocity());			
			velocityCache = (float)camera.getAvgVelocity();
			int newState = -1;
			
			if((float)camera.getAvgVelocity() < threshold && camera.getDensity() != 0){
				newState = 2;
			}else if((float)camera.getAvgVelocity() >= threshold){
				newState = 1;
			}else{
				newState = 0;
			}
		
			if(newState != trafficState){
				trafficState = newState;
			
				//System.out.println("$$$ update traffic on "+this.getLocalCamera().getAgentID());
				
				sendTrafficJamInfo();
			}
		}
	}
	
	/*************
	 *           *
	 * THREADING *
	 *           *
	 *************/
	
	public void run(){
		this.neighbourPerceptStep();
		this.trafficJamPerceptStep();
		
		//System.out.println("traffic on: "+this.getLocalCamera().getAgentID()+" velo: "+this.getLocalCamera().getAvgVelocity()+" den: "+this.getLocalCamera().getDensity());
	}
	
	/*******************************************
	 *                                         *
	 * IMPLEMENTATION OF PERCEPTION INTERFACES *
	 *                                         *
	 *******************************************/
	
	public synchronized void senseNeighbours(NeighbourPerceive callback){
		this.neighbourPerceivers.add(callback);
		
		ArrayList<NodeID> neighbours = camera.getAliveNeighbourIDlist();
		//this.neighbourCache = neighbours;	
		callback.changeNeighbours(neighbours);
	}
	
//	-1: undefined (start)
	//0: no traffic
	//1: normal traffic
	//2: congested
	
	public synchronized void senseTrafficJamInfo(TrafficJamInfoPerceive callback){
		this.trafficJamPerceivers.add(callback);
		
		float velocity = (float)camera.getAvgVelocity();
		float density = (float)camera.getDensity();
		float intensity = (float)camera.getIntensity();
		
		if(velocity < threshold && density != 0){
			this.trafficState = 2;
		}else if(velocity >= threshold){
			this.trafficState = 1;
		}else{
			this.trafficState = 0;
		}
		
		callback.setAvgVelocity(velocity);
		callback.setDensity(density);
		callback.setIntensity(intensity);
	}
	
	//@Pieter
	public boolean seesTrafficCongestion(){
		return (this.trafficState == 2);
	}
	
	public void stopNeighbourSensing(NeighbourPerceive callback){
		this.neighbourPerceivers.remove(callback);
	}
	
	public void stopTrafficJamInfoSensing(TrafficJamInfoPerceive callback){
		this.trafficJamPerceivers.remove(callback);
	}
	
	/************************
	 *                      *
	 * SELF-HEALING METHODS *
	 *                      *
	 ************************/
	
	public Camera getLocalCamera(){
		return this.camera;
	}
	
}
