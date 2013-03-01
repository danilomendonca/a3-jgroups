package GUI;
import simulator.models.*;

public interface ControlPanelUser {
	public void start();
	public void stop();
	public void clearDiagrams();
	public void switchBottleNeck(int node);
	public void updatedSimulator(SimulatorModel sim);
	public void updateRate(double newRate);
	public void failNode(int nodeID);
	
	//parameters have changed, complete new sim necessary
	public void reset();
	
	public void setControl(ControlPanel control);
}
