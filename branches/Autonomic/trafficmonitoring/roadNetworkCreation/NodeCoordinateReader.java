package roadNetworkCreation;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Ellipse2D;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;

public class NodeCoordinateReader implements MouseListener {
	
	public NodeCoordinateReader(BufferedImage imageWithNodes, JFrame frame){
		this.imageWithNodes = imageWithNodes.createGraphics();
		this.frame = frame;
		
		this.topBorderHeight = this.frame.getInsets().top;
		this.leftBorderWidth = this.frame.getInsets().left;
	}
	
	private Graphics2D imageWithNodes;
	private JFrame frame;
	private int topBorderHeight;
	private int leftBorderWidth;
	
	public static void main(String[] args){
		
		BufferedImage image = null;
		try {
		    image = ImageIO.read(new File("kruispunten.jpg"));
		} catch (IOException e) {
		}
		
		// Use a label to display the image
	    JFrame frame = new JFrame();
	    JLabel label = new JLabel(new ImageIcon(image));
	    frame.getContentPane().add(label, BorderLayout.CENTER);
	    frame.pack();
	    frame.setVisible(true);
	    
	    //Look for mouse events
	    frame.addMouseListener(new NodeCoordinateReader(image, frame));
	}

	/**************************	 
	 * 
	 *	Simulator Nodes
	 *
	 **************************/
	
	@Override	
	public void mouseClicked(MouseEvent arg0) {
		int x = arg0.getX();
		int y = arg0.getY();
		
		System.out.println(x + " " + y);
		
		//Indicate on the image that the node coordinates have been recorded successfully
		int width = 20;
		int height = width;		
		Shape circle = new Ellipse2D.Double(x-width/2-this.leftBorderWidth, 
											y-height/2-this.topBorderHeight, 
											width, height);
		this.imageWithNodes.draw(circle);
		this.imageWithNodes.setColor(Color.GREEN);
		this.imageWithNodes.fill(circle);
		
		this.frame.repaint();
	}
	
	
	
	
	/**************************	 
	 * 
	 *	Simulator Road Segments
	 *
	 **************************/
	
	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
