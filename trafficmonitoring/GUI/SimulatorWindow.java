package GUI;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import javax.swing.*;

import node.developer.sensors.*;

import simulator.RoadNetwork;
import simulator.RoadSegment;


public class SimulatorWindow extends JFrame implements Runnable{
	
	private BufferedImage offscreen;
	private Dimension offscreensize;
	private Graphics2D offgraphics;
    private BufferedImage background;
    private Color bgColor; 
    
    //street dimensions
   
    private int x_size, y_size;
    
	private RoadNetwork network;
    
	public SimulatorWindow(RoadNetwork network, boolean gui_on){
		//Default dimensions
		this(network, gui_on, 400, 100);
	}
	
	//@Pieter
	public SimulatorWindow(RoadNetwork network, boolean gui_on, int windowXSize, int windowYSize){
		this.setSize(windowXSize, windowYSize);
		this.network = network;
		if(gui_on)
			this.setVisible(true);
		bgColor = new Color(116, 139, 148);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	
	private final int xShift = 17;
	private int xsizeSTD;
		
	private BufferedImage makeBackground(){
    	Dimension d = getSize();
    	BufferedImage background;
    	Graphics2D graphics;
		background = (BufferedImage) createImage(d.width, d.height);
	    offscreensize = d;
		graphics = background.createGraphics();
		graphics.setFont(getFont());
		
		graphics.setBackground(bgColor);
		graphics.clearRect(0,0, d.width, d.height);
		
		//TODO
		graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			
		return background;
    }
		
	public void step(){
		repaint();
	}
	
	public void paint(Graphics g){
		Dimension d = getSize();
		if ((background == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
		    background = this.makeBackground();
		}
		
		if ((offscreen == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
		    offscreen = (BufferedImage) createImage(d.width, d.height);
		    offscreensize = d;
		    if (offgraphics != null) {
		        offgraphics.dispose();
		    }
		    offgraphics = offscreen.createGraphics();
		    offgraphics.setFont(getFont());
		}
		//offgraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		offgraphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		
		//TEKEN DE DINGEN
		
		offgraphics.clearRect(0,0, d.width, d.height);
		//offgraphics.fillRect(1, 0, this.x_size, this.y_size);
		
		//paintCars(offgraphics);
		for(Enumeration<RoadSegment> e=network.getSegments().elements();e.hasMoreElements(); ){
			paintRoadSegment(offgraphics, e.nextElement());
		}
		
		for(Enumeration<RoadSegment> e=network.getSegments().elements();e.hasMoreElements(); ){
			paintCars(offgraphics, e.nextElement());
		}
		
		paintCameras(offgraphics);
		    
		g.drawImage(offscreen, 0, 0, null);
	}
	
	public void paintRoadSegment(Graphics2D g, RoadSegment segment){
		//draw road
		
		g.setColor(Color.gray);
		
		//draw road itself
		g.setStroke(new BasicStroke(8f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.drawLine((int)segment.getBegin().getX(),
					(int)segment.getBegin().getY(),
					(int)segment.getEnd().getX(),
					(int)segment.getEnd().getY());				
	}
	
	public void paintCars(Graphics2D g, RoadSegment segment){
	 	g.setColor(Color.WHITE);
	 	g.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
	 	
		if (segment != null){
			double x1 = (double)segment.getBegin().getX();
			double y1 = (double)segment.getBegin().getY();
			double x2 = (double)segment.getEnd().getX();
			double y2 = (double)segment.getEnd().getY();
			
			double deltaX = (x2-x1)/segment.LENGTH;
			double deltaY = (y2-y1)/segment.LENGTH;
									
			for (int i = 0; i < segment.LENGTH; i++) {
				if (segment.cars[i] != null)
					/*g.drawLine((int)(x1+i*deltaX),
							   (int)(y1+i*deltaY),
							   (int)(x1+i*deltaX),
							   (int)(y1+i*deltaY));*/
					g.fillRect((int)(x1+i*deltaX), (int)(y1+i*deltaY), 2, 2);
		    }
		} 
	}
	
	public void paintCameras(Graphics2D g){
		if(this.availableColors == null){
			this.availableColors = new Vector<Color>();
			for(int i = 0; i<this.colors.length; i++ ){
				this.availableColors.add(colors[i]);
			}
		}
		
		//get all current organizations larger than 1
		Vector<Integer> currentOrgs = new Vector<Integer>();
		for(Enumeration<Camera> e=network.getCameras().elements();e.hasMoreElements(); ){
			Camera c = e.nextElement();
			if(!c.isFailed() && c.getNodesContext() != null && c.getNodesContext().getPersonalOrg() != null){
				if(c.getNodesContext().getPersonalOrg().getAgents().size()>1)
					currentOrgs.add(c.getNodesContext().getPersonalOrg().getId());
			}
		}
		
		//reconfigure colors
		for(Enumeration<Integer> e = this.orgColors.keys(); e.hasMoreElements(); ){
			int id = e.nextElement();
			if(!currentOrgs.contains(id)){
				this.availableColors.add(orgColors.get(id));
				this.orgColors.remove(id);
			}
		}		
	 		 	
	 	for(Enumeration<Camera> e=network.getCameras().elements();e.hasMoreElements(); ){
	 		Camera c = e.nextElement();
	 		
	 		g.setColor(Color.WHITE);
			
			//Organization org = c.getNodesContext().getPersonalOrg();
			
			if(!c.isFailed() && c.getNodesContext() != null && c.getNodesContext().getPersonalOrg() != null){
				int orgID = c.getNodesContext().getPersonalOrg().getId();
				if(orgColors.containsKey(orgID)){
					g.setColor(orgColors.get(orgID));
				}else if(!c.isFailed() && c.getNodesContext() != null && c.getNodesContext().getPersonalOrg() != null && 
							c.getNodesContext().getPersonalOrg().getAgents().size()>1){
					orgColors.put(orgID, this.availableColors.lastElement());
					g.setColor(this.availableColors.lastElement());
					this.availableColors.remove(this.availableColors.lastElement());
				}else{
					g.setColor(Color.white);
				}
			}else{
				//Color failed camera
				g.setColor(Color.red);
			}
	 		
	 		/*
	 		g.fillRect(c.getX()-2, c.getY()-2, 4, 4);
	 		g.setColor(Color.BLACK);
	 		g.setStroke(new BasicStroke(1f));
	 		g.drawRect(c.getX()-2, c.getY()-2, 4, 4);
	 		*/
	 		
	 		g.fillRect(c.getX()-7, c.getY()-7, 14, 14);
	 		g.setColor(Color.BLACK);
	 		g.setStroke(new BasicStroke(1f));
	 		g.drawRect(c.getX()-7, c.getY()-7, 14, 14);	
	 		
	 		g.setColor(Color.black);
			Font f = new Font(Font.DIALOG, Font.PLAIN, 9);
			g.setFont(f);
			
			g.drawString(""+c.getAgentID().toString(), c.getX()-6+3, c.getY()-6+11); 	
			if(!c.isFailed() && c.getNodesContext() != null && c.getNodesContext().getPersonalOrg() != null){
				//g.drawString(""+c.getNodesContext().getPersonalOrg().getId(), c.getX()-6+3, c.getY()-10);
				if(c.getNodesContext().getPersonalOrg().getMasterID().equals(c.getNodesContext().getPersonalID())){
					//isMaster
					g.setColor(Color.red);
			 		g.setStroke(new BasicStroke(1f));
			 		g.drawRect(c.getX()-7, c.getY()-7, 14, 14);						
				}
			}
		} 
	}
	
	
	
	int orgCount;
	Hashtable<Integer, Color> orgColors = new Hashtable<Integer, Color>();
	Vector<Color> availableColors;	
	
	private Color[] colors = {Color.BLUE, Color.cyan, Color.gray,
							  Color.green, Color.lightGray, Color.magenta, Color.orange, Color.pink, Color.yellow};
	
	public void paintCamerasBU(Graphics2D g){
		
		orgCount = 0;
		orgColors = new Hashtable<Integer, Color>();
	 		 	
	 	for(Enumeration<Camera> e=network.getCameras().elements();e.hasMoreElements(); ){
	 		Camera c = e.nextElement();
	 		
	 		g.setColor(Color.WHITE);
			
			//Organization org = c.getNodesContext().getPersonalOrg();
			
			if(!c.isFailed() && c.getNodesContext() != null && c.getNodesContext().getPersonalOrg() != null){
				int orgID = c.getNodesContext().getPersonalOrg().getId();
				if(orgColors.containsKey(orgID)){
					g.setColor(orgColors.get(orgID));
				}else{
					orgColors.put(orgID, colors[orgCount%colors.length]);
					g.setColor(colors[orgCount%colors.length]);
					orgCount++;
				}
			}else{
				g.setColor(Color.red);
			}
	 		
	 		/*
	 		g.fillRect(c.getX()-2, c.getY()-2, 4, 4);
	 		g.setColor(Color.BLACK);
	 		g.setStroke(new BasicStroke(1f));
	 		g.drawRect(c.getX()-2, c.getY()-2, 4, 4);
	 		*/
	 		
	 		g.fillRect(c.getX()-7, c.getY()-7, 14, 14);
	 		g.setColor(Color.BLACK);
	 		g.setStroke(new BasicStroke(1f));
	 		g.drawRect(c.getX()-7, c.getY()-7, 14, 14);	
	 		
	 		g.setColor(Color.black);
			Font f = new Font(Font.DIALOG, Font.PLAIN, 9);
			g.setFont(f);
			
			g.drawString(""+c.getAgentID().toString(), c.getX()-6+3, c.getY()-6+11); 	
			if(!c.isFailed() && c.getNodesContext() != null && c.getNodesContext().getPersonalOrg() != null){
				g.drawString(""+c.getNodesContext().getPersonalOrg().getId(), c.getX()-6+3, c.getY()-10);
				if(c.getNodesContext().getPersonalOrg().getMasterID().equals(c.getNodesContext().getPersonalID())){
					//isMaster
					g.setColor(Color.red);
			 		g.setStroke(new BasicStroke(1f));
			 		g.drawRect(c.getX()-7, c.getY()-7, 14, 14);						
				}
			}
		} 
	}
	
	/*************
	 *           * 
	 * THREADING *
	 *           *
	 *************/
	
	private Thread runner = null;
	
	public void run() { 
	    while ((Thread.currentThread() == runner)){
	    	this.step();
	    	try {Thread.sleep (100);} catch(InterruptedException e) {}
		}
	}
	
	
	public void start() {  
	    if (runner == null) {
	      runner = new Thread (this);
	      runner.start();
	    }
	}
	  
	public void stop() { 
		runner=null;
	}
}
