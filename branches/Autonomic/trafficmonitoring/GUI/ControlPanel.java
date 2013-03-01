package GUI;

import java.awt.event.*;
import java.awt.*;
import simulator.models.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ControlPanel extends JPanel implements ChangeListener, ActionListener{//extends Applet {    
	
	// model Parameters
	private double slowdown = 0.01;
	private double carRate = 15;//  = 0.30;
	private double lambda   = 0.77;
	private SimulatorModel[] sim = {new NagelSchreckenberg(), new BarlovicSchreckenberg(), new Helbing()};

	private int type = 2;
	private int[] dx_choices = {250, 750};
	private int  deltaX = 1; // default choice = 7.5m
	private boolean FirstStep = true;
	  
	//TODO ROB
	//private RoadCanvas canvas;
	  
	// Labels of the two scrollbars
	private JLabel label_slowdown, label_density, label_lambda;
	private JButton start, stop, clear;
	private JButton bottleNeck;
	private JButton failNode;
	private JScrollBar sb_slowdown;
	private JScrollBar sb_car_rate;
	private JScrollBar sb_lambda;
	private JComboBox model_choice, dx_choice;
	
	private ControlPanelUser owner;
	
	public ControlPanel(ControlPanelUser owner){
		this.owner = owner;
		this.init();
		owner.setControl(this);
	}

	public void init() {  
		//SIMULATOR COMBOBOX
		
	    //parameters
	    JPanel parameterPanel = new JPanel();
	    parameterPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Parameters"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
	    parameterPanel.setLayout(new BoxLayout(parameterPanel, BoxLayout.PAGE_AXIS));	    
	    
		    //Car Rate
		    parameterPanel.add(new JLabel("Car Rate"));
		    //sb_car_rate = getSB (0, 100, (int)carRate);
		    sb_car_rate = getSB (0, 20, 15);
		    sb_car_rate.getModel().addChangeListener(this);
		    JPanel densityPanel = new JPanel();
		    densityPanel.setLayout(new BoxLayout(densityPanel, BoxLayout.X_AXIS));
		    densityPanel.add(sb_car_rate);
		    //densityPanel.add(label_density = new JLabel(this.carRate+""));
		    parameterPanel.add(densityPanel);
		    		    
		//Buttons
	    JPanel buttonPanel = new JPanel();
	    buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Control"),
                BorderFactory.createEmptyBorder(5,5,5,5)));
	    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
	   
	    	//start
	    	buttonPanel.add(start = new JButton("Start"));
	    	start.addActionListener(this);
	
	    	//stop
	    	buttonPanel.add(stop = new JButton("Stop"));
	    	stop.addActionListener(this);	    
	    		    
		    //BottleNeck
		    buttonPanel.add(bottleNeck = new JButton("Bottleneck on/off"));
		    bottleNeck.addActionListener(this);
		    
		    //BottleNeck
		    buttonPanel.add(failNode = new JButton("FailNode"));
		    failNode.addActionListener(this);
		    
	    
		    
		    //CANVAS AANMAKEN
	    //this.parentApp.resetSimulator(density, dx_choices[deltaX]/100.0, sim[type]);
	    //canvas = new RoadCanvas (density, slowdown, lambda,
		//		     dx_choices[deltaX]/100.0,  sim[type], this.parentApp.getOpenRoadSimulator(), this.parentApp);
	
	    //canvas.start();
	    
	    setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
		add(parameterPanel);
		add(buttonPanel);
		parameterPanel.setAlignmentY(TOP_ALIGNMENT);
		buttonPanel.setAlignmentY(TOP_ALIGNMENT);
	
	    validate();
	}

  private JScrollBar getSB (int min, int max, int init) {
    final int inc = 1;
    return new JScrollBar(JScrollBar.HORIZONTAL, init, inc, min, 
			 max+inc);
  }

  private void setPercentLabel (JLabel l, int v) {
    l.setText (String.valueOf (v)+"%");
  }
  
  	/***********
	 * GETTERS *
	 ***********/
  
  	public double getDxChoice(){
  		return dx_choices[deltaX]/100.0;
	}
  	
  	public SimulatorModel getSimType(){
  		return sim[type];
  	}
	  
	public double getSlowdown(){
		return slowdown;
	}
	  
	public double getCarRate(){
		return carRate;
	}
	  
	public double getlambda(){
		return lambda;
	}
  
  	/***************************
  	 * STATE AND ACTION EVENTS *
  	 ***************************/
  	
  public void stateChanged(ChangeEvent changeEvent) {    
	    if (changeEvent.getSource() == sb_car_rate.getModel()) {
	    	int dens = sb_car_rate.getValue();
	    	if (dens != carRate) {
	    		carRate = dens;      
	    		owner.updateRate(carRate);
	    	}
	    }else if (changeEvent.getSource() == sb_slowdown.getModel()) {
	    	int v = sb_slowdown.getValue();
	    	double slow =  v*0.01;
	    	if (slow != slowdown) {
	    		slowdown = slow;
	    		setPercentLabel (label_slowdown, v);
	    	}
	    }else if (changeEvent.getSource() == sb_lambda.getModel()) {
	    	int v = sb_lambda.getValue();
	    	double lamb =  v*0.01;
	    	if (lamb != lambda) {
	    		lambda = lamb;
	    		setPercentLabel (label_lambda, v);
	    	}
	    }
  }
	
	public void actionPerformed(ActionEvent e) {
      int v;
      if (e.getSource() == start) {  
          owner.start();
      } else if (e.getSource() == stop) {
          owner.stop();
      } else if (e.getSource() == clear) {
      	 owner.clearDiagrams();
      }else if (e.getSource() == bottleNeck) {
      	 owner.switchBottleNeck(Integer.parseInt(JOptionPane.showInputDialog("Crossing to block:")));
      }else if (e.getSource() == failNode) {
         	 owner.failNode(Integer.parseInt(JOptionPane.showInputDialog("Node to fail:")));
      }else if (e.getSource() == model_choice) {
          type = 0;
          while (!model_choice.getSelectedItem().equals (sim[type].getName())) 
          	type++;
          
          if ((sim[type] instanceof NagelSchreckenberg) || (sim[type] instanceof BarlovicSchreckenberg)) {
          	lambda = 1.0;
          	slowdown = 0.5;
          } else {
          	lambda = 0.77;
          	slowdown = 0.01;
          }
          v = (int)(slowdown*100);
          sb_slowdown.setValue(v);      
          setPercentLabel (label_slowdown, v);
          v = (int)(lambda*100);
          sb_lambda.setValue(v);      
          setPercentLabel (label_lambda, v);

          owner.updatedSimulator(sim[type]);
          
      }else if (e.getSource() == dx_choice) {
      		
      		deltaX = 0;
      		while (!dx_choice.getSelectedItem().equals (String.valueOf (dx_choices[deltaX]/100.0))) deltaX++;

      		          
      		owner.reset();
      		validate();
       }
      
  }
	
	public boolean handleEvent (Event evt) {
	    switch (evt.id) {
	    case Event.SCROLL_LINE_UP:
	    case Event.SCROLL_LINE_DOWN:
	    case Event.SCROLL_PAGE_UP:
	    case Event.SCROLL_PAGE_DOWN:
	    case Event.SCROLL_ABSOLUTE:
	      if (evt.target == sb_car_rate) {
		int dens = sb_car_rate.getValue();
		if (dens != carRate) {
		  carRate = dens;      
		  owner.updateRate(carRate);
		}
	      } else if (evt.target == sb_slowdown) {
		int v = sb_slowdown.getValue();
		double slow =  v*0.01;
		if (slow != slowdown) {
		  slowdown = slow;
		  setPercentLabel (label_slowdown, v);
		}
	      } else if (evt.target == sb_lambda) {
		int v = sb_lambda.getValue();
		double lamb =  v*0.01;
		if (lamb != lambda) {
		  lambda = lamb;
		  setPercentLabel (label_lambda, v);
		}
	      } 
	    }
	    return super.handleEvent(evt);
	  }

	  public boolean action (Event evt, Object arg) {  
	    int v;

	    if (evt.target == start) {  
	      owner.start();
	    } else if (evt.target == stop) {
	      owner.stop();
	    } else if (evt.target == clear) {
	    	//TODO ROB
	      //canvas.ClearDiagrams();
	    } else if (evt.target == model_choice) {
	      type = 0;
	      while (!arg.equals (sim[type].getName())) type++;

	      if ((sim[type] instanceof NagelSchreckenberg) ||
		  (sim[type] instanceof BarlovicSchreckenberg)) {
		lambda = 1.0;
		slowdown = 0.5;
	      } else {
		lambda = 0.77;
		slowdown = 0.01;
	      }

	      v = (int)(slowdown*100);
	      sb_slowdown.setValue(v);      
	      setPercentLabel (label_slowdown, v);
	      v = (int)(lambda*100);
	      sb_lambda.setValue(v);      
	      setPercentLabel (label_lambda, v);
	     
	      owner.updatedSimulator(sim[type]);
	    } else if (evt.target == dx_choice) {

	     

	      deltaX = 0;
	      while (!arg.equals (String.valueOf (dx_choices[deltaX]/100.0))) deltaX++;

	     	      
	      owner.reset();
	    } else {
	      return super.action(evt, arg);
	    }
	    return true;
	  }
  
}
