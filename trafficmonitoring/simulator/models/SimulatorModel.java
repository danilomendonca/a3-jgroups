package simulator.models;

public interface SimulatorModel {
  void init(int MAXSPEED, int LENGTH, double deltaX);
  int updateSpeed(int speed, int gap, 
		  double prob_slowdown, double lambda);
  String getName();
}
