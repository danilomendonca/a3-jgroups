package simulator;

import java.util.Enumeration;

import node.agentMiddleware.communication.middleware.MessageProfiler;
import simulator.models.SimulatorModel;
import a3Solution.A3CameraNode;

public class TrafficSimulator implements GUI.ControlPanelUser, Runnable {
	private GUI.ControlPanel control;
	private int test;

	private RoadNetwork network;

	public TrafficSimulator(int scenario, int test) {
		this.test = test;
		network = new RoadNetwork(scenario);
		// System.out.println("failTime: "+failTime);
	}

	public void init(int cameraRange) {
		for (Enumeration<RoadSegment> e = this.network.getSegments().elements(); e
				.hasMoreElements();) {
			e.nextElement().init(control.getDxChoice(), control.getSimType());
		}
		network.createCameras(cameraRange);
	}

	public void step() {

		System.out.println(" ------------------------------- Step: "
				+ currentStep + "------------------------------");

		if (test == 1)
			scenario1Step();
		else if (test == 2)
			scenario2Step();
		else if (test == 3)
			scenario3Step();
		else if (test == 5)
			scenario5step();
		else if (test == 6)
			scenario6step();
		else if (test == 7)
			scenario7step();
		else if (test == 8)
			scenario8step();
		else if (test == 9)
			scenario9step();
		else if (test == 10)
			scenario10step();
		else if (test == 11)
			scenario11step();
		else if (test == 12)
			scenario12step();
		else if (test == 13)
			scenario13step();
		else if (test == 14)
			scenario14step();
		else if (test == 15)
			scenario15step();

		// @Pieter
		this.advance();

		System.out
				.println(" -------------------------------------------------------------");
	}

	// @Pieter
	public void advance() {
		/*
		 * for(Enumeration<RoadSegment> e = this.getSegments();
		 * e.hasMoreElements(); ){ this.printCars(e.nextElement()); }
		 */

		// System.out.println(1);

		for (Enumeration<RoadSegment> e = this.network.getSegments().elements(); e
				.hasMoreElements();) {
			e.nextElement().updateSpeeds(this.control.getSlowdown(),
					this.control.getlambda());
		}

		// System.out.println(2);

		for (Enumeration<Node> e = this.network.getNodes().elements(); e
				.hasMoreElements();) {
			e.nextElement().step();
		}

		// System.out.println(3);

		for (Enumeration<RoadSegment> e = this.network.getSegments().elements(); e
				.hasMoreElements();) {
			e.nextElement().moveVehicles();
		}

		// System.out.println(4);

		for (Enumeration<RoadSegment> e = this.network.getSegments().elements(); e
				.hasMoreElements();) {
			e.nextElement().transferVehicles();
		}

		/*
		 * int realCars = 0; for(Enumeration<RoadSegment> e =
		 * this.getSegments(); e.hasMoreElements(); ){
		 * realCars+=e.nextElement().numberOfCars(); }
		 * 
		 * //System.out.println("In - out: "+
		 * CarCounter.getCounter().getCurrentCars()+"; real cars: "+realCars);
		 * System.out.println("difference: "+
		 * (CarCounter.getCounter().getCurrentCars() - realCars));
		 */
	}

	private void printCars(RoadSegment segment) {
		String result = segment.getBegin().getX() + ": [";

		for (int i = 0; i < segment.cars.length; i++) {
			if (segment.cars[i] != null)
				result += segment.cars[i].speed + " ";
			else
				result += " empty ";
		}
		System.out.println(result + "]");

	}

	// Control panel user

	public void setControl(GUI.ControlPanel control) {
		this.control = control;
	}

	public void clearDiagrams() {

	}

	public RoadNetwork getNetwork() {
		return network;
	}

	public void switchBottleNeck(int node) {
		// ((Crossing)network[3][3]).changeBlock();
		network.switchBottleNeck(node);
		// cr33.changeBlock();
		// s1.switchOnOff();
		// s2.switchOnOff();
		// s3.switchOnOff();
		// s4.switchOnOff();
	}

	public void updatedSimulator(SimulatorModel sim) {

	}

	public void reset() {

	}

	public void updateRate(double carRate) {
		for (Enumeration<Source> e = this.network.getSources().elements(); e
				.hasMoreElements();) {
			e.nextElement().updateRate(carRate);
		}
	}

	/************
	 * * SCENARIO * *
	 ************/

	public void setNodeFailureScenario(A3CameraNode[] failingNodes,
			int[] failTimes) {
		this.failingNodes = failingNodes;
		this.failTimes = failTimes;
	}

	private int currentStep = 0;

	private A3CameraNode[] failingNodes = new A3CameraNode[0];
	private int[] failTimes = new int[0];

	// @Pieter
	private void scenario3Step() {

		if (currentStep == 1) {
			this.switchBottleNeck(1);
			this.updateRate(20);
		}

		if (currentStep == 200) {
			// SlaveNodeFailure
			failNode(2);
			this.updateRate(0);
		}

		if (currentStep == 250) {
			this.switchBottleNeck(1);
		}

		// Problemen!
		if (currentStep == 300) {
			this.switchBottleNeck(1);
			this.updateRate(20);
		}

		if (currentStep == 550) {
			this.switchBottleNeck(1);
		}

		currentStep++;

		// if(currentStep == 1){
		// this.switchBottleNeck(2);
		// this.updateRate(18);
		// }
		//
		// if(currentStep == 1000){
		// this.updateRate(0);
		// this.switchBottleNeck(2);
		// }
		//
		// if(currentStep == 2100){
		// MessageProfiler.getMessageProfiler().flushResults(3);
		// System.exit(0);
		// }
		//
		// currentStep++;
	}

	// @Pieter
	private void scenario2Step() {

		if (currentStep == 1) {
			this.switchBottleNeck(1);
			this.updateRate(20);
		}

		if (currentStep == 200) {
			// SlaveNodeFailure
			failNode(2);
			this.updateRate(0);
		}

		if (currentStep == 250) {
			this.switchBottleNeck(1);
		}

		currentStep++;

		// if(currentStep == 1){
		// this.switchBottleNeck(1);
		// this.updateRate(18);
		// }
		//
		// if(currentStep == 900){
		// this.updateRate(0);
		// this.switchBottleNeck(1);
		// }
		//
		// if(currentStep == 1800){
		// MessageProfiler.getMessageProfiler().flushResults(2);
		// System.exit(0);
		// }
		//
		// currentStep++;
	}

	// @Pieter
	private void scenario1Step() {
		if (currentStep == 1) {
			System.out.println("Step: " + currentStep);
			this.switchBottleNeck(1);
			this.updateRate(20);
		}

		if (currentStep == 190) {
			System.out.println("Step: " + currentStep);
			this.updateRate(0);
		}

		if (currentStep == 210) {
			System.out.println("Step: " + currentStep);
			// MasterWithSlavesNodeFailure
			failNode(4);
		}

		if (currentStep == 225) {
			System.out.println("Step: " + currentStep);
			this.updateRate(20);
		}

		if (currentStep == 275) {
			System.out.println("Step: " + currentStep);
			this.updateRate(0);
		}

		if (currentStep == 285) {
			System.out.println("Step: " + currentStep);
			// SlaveNodeFailure
			failNode(2);
		}

		if (currentStep == 310) {
			System.out.println("Step: " + currentStep);
			// Camera Introduction case B
			// bringNodeBackOnline(4);
		}

		if (currentStep == 330) {
			System.out.println("Step: " + currentStep);
			this.switchBottleNeck(1);
		}

		currentStep++;

		// if(currentStep == 1){
		// this.switchBottleNeck(1);
		// this.updateRate(18);
		// }
		//
		// if(currentStep == 900){
		// this.updateRate(0);
		// this.switchBottleNeck(1);
		// }
		//
		// if(currentStep == 1800){
		// MessageProfiler.getMessageProfiler().flushResults(1);
		// System.exit(0);
		// }
		//
		// currentStep++;
	}

	public void failNode(int i) {
		// @Pieter
		// MessageProfiler.getMessageProfiler().printNodeFailure(""+i);
		i = i - 1;
		System.out.println("<<<NODE " + failingNodes[i].getID() + " FAILED");
		failingNodes[i].fail();
		// SimulatedCommunicationNetwork.getInstance().isolateNode(
		// failingNodes[i].getComMid().getNodeIdentifier());
	}

	// @Pieter
	// public void bringNodeBackOnline(int i) {
	// i = i - 1;
	// System.out.println("<<<NODE "
	// + failingNodes[i].getComMid().getNodeIdentifier()
	// + " BACK ONLINE");
	//
	// failingNodes[i].bringBackOnline();
	//
	// SimulatedCommunicationNetwork.getInstance().deIsolateNode(
	// failingNodes[i].getComMid().getNodeIdentifier());
	// }

	private Thread runner = null;

	public void start() {
		if (runner == null) {
			runner = new Thread(this, "TrafficSimulatorThread");
			runner.start();
		}
	}

	public void stop() {
		runner = null;
	}

	public void run() {
		while ((Thread.currentThread() == runner)) {
			step();
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
	}

	/******************
	 * * SELF-SCENARIOS * *
	 ******************/

	private String[] toPrint = { "AliveSignal" };

	private void scenario5step() {
		// T-splits: bottleneck, slave node failure
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 400 + this.failTime) {
			this.failNode(5);
			MessageProfiler.getMessageProfiler().reset();
		}

		if (currentStep == 600 + this.failTime) {
			// MessageProfiler.getMessageProfiler().printInOnce(toPrint, true);
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true);
			System.exit(0);
		}

		currentStep++;
	}

	private void scenario6step() {
		// T-splits: bottleneck, no node failure
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 400 + this.failTime) {
			MessageProfiler.getMessageProfiler().reset();
		}

		if (currentStep == 600 + this.failTime) {
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true);
			System.exit(0);
		}

		currentStep++;
	}

	private void scenario7step() {
		// T-splits: bottleneck, master node failure
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		// if (currentStep == 400 + this.failTime) {
		// this.failNode(this.failingNodes[0].getContext().getPersonalOrg()
		// .getMasterID().getId());
		// MessageProfiler.getMessageProfiler().reset();
		// }

		if (currentStep == 600 + this.failTime) {
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true);
			System.exit(0);
		}
		currentStep++;
	}

	private void scenario8step() {
		// T-splits: bottleneck, slave node failure
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 400) {
			this.failNode(2);
			MessageProfiler.getMessageProfiler().reset();
		}

		if (currentStep == 600) {
			// MessageProfiler.getMessageProfiler().printInOnce(toPrint, true);
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true);
			System.exit(0);
		}

		currentStep++;
	}

	// this.failingNodes[0].getContext().getPersonalOrg().getAgents();

	int aliveSignals = 0;

	private void scenario9step() {
		// checking response time
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 301) {
			aliveSignals = MessageProfiler.getMessageProfiler().getAmountFor(
					"AliveSignal");
		}

		if (currentStep == 400) {
			aliveSignals = MessageProfiler.getMessageProfiler().getAmountFor(
					"AliveSignal")
					- aliveSignals;
		}

		if (currentStep == 400 + this.failTime) {
			this.failNode(5);
			MessageProfiler.getMessageProfiler().reset();
		}

		// if (currentStep >= 400 + this.failTime) {
		// if (this.failingNodes[0].getContext().getPersonalOrg() != null) {
		// int masterID = this.failingNodes[0].getContext()
		// .getPersonalOrg().getMasterID().getId();
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg() != null) {
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents().size() == 5
		// && !this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents()
		// .contains(new utilities.NodeID(5))) {
		// String[] items = { "Alive Signals over interval:",
		// "Nb of steps:" };
		// int[] values = { aliveSignals, currentStep - 400 };
		// MessageProfiler.getMessageProfiler().printInOnce(
		// toPrint, true, items, values);
		// System.exit(0);
		// }
		// }
		// }
		// }

		if (currentStep >= 1200) {
			String[] items = { "Alive Signals over interval:", "Nb of steps:" };
			int[] values = { aliveSignals, -9999999 };
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true,
					items, values);
			System.exit(0);
		}

		currentStep++;
	}

	int failTime = (int) (Math.random() * (double) 200);
	utilities.NodeID failedMaster;

	private void scenario10step() {
		// checking response time
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 301) {
			aliveSignals = MessageProfiler.getMessageProfiler().getAmountFor(
					"AliveSignal");
		}

		if (currentStep == 400) {
			aliveSignals = MessageProfiler.getMessageProfiler().getAmountFor(
					"AliveSignal")
					- aliveSignals;
		}

		// if (currentStep == 400 + this.failTime) {
		// failedMaster = this.failingNodes[0].getContext().getPersonalOrg()
		// .getMasterID();
		// this.failNode(this.failingNodes[0].getContext().getPersonalOrg()
		// .getMasterID().getId());
		// MessageProfiler.getMessageProfiler().reset();
		// }
		//
		// if (currentStep >= 400 + this.failTime) {
		// if (this.failingNodes[0].getContext().getPersonalOrg() != null) {
		// int masterID = this.failingNodes[0].getContext()
		// .getPersonalOrg().getMasterID().getId();
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg() != null) {
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents().size() == 3
		// && !this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents()
		// .contains(failedMaster)) {
		// String[] items = { "Alive Signals over interval:",
		// "Nb of steps:" };
		// int[] values = { aliveSignals, currentStep - 400 };
		// MessageProfiler.getMessageProfiler().printInOnce(
		// toPrint, true, items, values);
		// System.exit(0);
		// }
		// }
		// }
		// }

		if (currentStep >= 1200) {
			String[] items = { "Alive Signals over interval:", "Nb of steps:" };
			int[] values = { aliveSignals, -9999999 };
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true,
					items, values);
			System.exit(0);
		}
		currentStep++;
	}

	private void scenario11step() {
		// checking response time
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 301) {
			aliveSignals = MessageProfiler.getMessageProfiler().getAmountFor(
					"AliveSignal");
		}

		if (currentStep == 400) {
			aliveSignals = MessageProfiler.getMessageProfiler().getAmountFor(
					"AliveSignal")
					- aliveSignals;
		}

		if (currentStep == 400 + this.failTime) {
			this.failNode(2);
			MessageProfiler.getMessageProfiler().reset();
		}

		// if (currentStep >= 400 + this.failTime) {
		// if (this.failingNodes[0].getContext().getPersonalOrg() != null) {
		// int masterID = this.failingNodes[0].getContext()
		// .getPersonalOrg().getMasterID().getId();
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg() != null) {
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents().size() == 3
		// && !this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents()
		// .contains(new utilities.NodeID(2))) {
		// String[] items = { "Alive Signals over interval:",
		// "Nb of steps:" };
		// int[] values = { aliveSignals, currentStep - 400 };
		// MessageProfiler.getMessageProfiler().printInOnce(
		// toPrint, true, items, values);
		// System.exit(0);
		// }
		// }
		// }
		// }

		if (currentStep >= 1200) {
			String[] items = { "Alive Signals over interval:", "Nb of steps:" };
			int[] values = { aliveSignals, -9999999 };
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true,
					items, values);
			System.exit(0);
		}
		currentStep++;
	}

	private void scenario12step() {
		// checking response time
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 301) {
			aliveSignals = MessageProfiler.getMessageProfiler().getAmountFor(
					"AliveSignal");
		}

		if (currentStep == 400) {
			aliveSignals = MessageProfiler.getMessageProfiler().getAmountFor(
					"AliveSignal")
					- aliveSignals;
		}

		// if (currentStep == 400 + this.failTime) {
		// failedMaster = this.failingNodes[0].getContext().getPersonalOrg()
		// .getMasterID();
		// this.failNode(this.failingNodes[0].getContext().getPersonalOrg()
		// .getMasterID().getId());
		// MessageProfiler.getMessageProfiler().reset();
		// }
		//
		// if (currentStep >= 400 + this.failTime) {
		// if (this.failingNodes[0].getContext().getPersonalOrg() != null) {
		// int masterID = this.failingNodes[0].getContext()
		// .getPersonalOrg().getMasterID().getId();
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg() != null) {
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents().size() == 5
		// && !this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents()
		// .contains(failedMaster)) {
		// String[] items = { "Alive Signals over interval:",
		// "Nb of steps:" };
		// int[] values = { aliveSignals, currentStep - 400 };
		// MessageProfiler.getMessageProfiler().printInOnce(
		// toPrint, true, items, values);
		// System.exit(0);
		// }
		// }
		// }
		// }

		if (currentStep >= 1200) {
			String[] items = { "Alive Signals over interval:", "Nb of steps:" };
			int[] values = { aliveSignals, -9999999 };
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true,
					items, values);
			System.exit(0);
		}
		currentStep++;
	}

	private void scenario13step() {
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 300) {
			MessageProfiler.getMessageProfiler().printNodeFailure("Start Test");
		}

		if (currentStep == 400) {
			this.failNode(16);
			// this.failNode(this.failingNodes[0].getContext().getPersonalOrg().getMasterID().getId());
		}

		if (currentStep == 800) {
			// MessageProfiler.getMessageProfiler().printInOnce(toPrint, true);
			MessageProfiler.getMessageProfiler().flush();
			System.exit(0);
		}

		currentStep++;
	}

	// slave failure
	private void scenario14step() {
		// checking response time
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		if (currentStep == 400 + this.failTime) {
			this.failNode(15);
			MessageProfiler.getMessageProfiler().reset();
		}

		// if (currentStep >= 400 + this.failTime) {
		// if (this.failingNodes[0].getContext().getPersonalOrg() != null) {
		// int masterID = this.failingNodes[0].getContext()
		// .getPersonalOrg().getMasterID().getId();
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg() != null) {
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents().size() == 19
		// && !this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents()
		// .contains(new utilities.NodeID(15))) {
		// MessageProfiler.getMessageProfiler().printInOnce(
		// toPrint, true);
		// System.exit(0);
		// }
		// }
		// }
		// }

		if (currentStep >= 1200) {
			String[] items = { "Alive Signals over interval:", "Nb of steps:" };
			int[] values = { aliveSignals, -9999999 };
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true,
					items, values);
			System.exit(0);
		}
		currentStep++;
	}

	// master failure
	private void scenario15step() {
		// checking response time
		if (currentStep == 0) {
			this.updateRate(400);
			this.switchBottleNeck(1);
		}

		// if (currentStep == 400 + this.failTime) {
		// failedMaster = this.failingNodes[0].getContext().getPersonalOrg()
		// .getMasterID();
		// this.failNode(failedMaster.getId());
		// MessageProfiler.getMessageProfiler().reset();
		// }
		//
		// if (currentStep >= 400 + this.failTime) {
		// if (this.failingNodes[0].getContext().getPersonalOrg() != null) {
		// int masterID = this.failingNodes[0].getContext()
		// .getPersonalOrg().getMasterID().getId();
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg() != null) {
		// if (this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents().size() == 19
		// && !this.failingNodes[masterID - 1].getContext()
		// .getPersonalOrg().getAgents()
		// .contains(failedMaster)) {
		// MessageProfiler.getMessageProfiler().printInOnce(
		// toPrint, true);
		// System.exit(0);
		// }
		// }
		// }
		// }

		if (currentStep >= 1200) {
			String[] items = { "Alive Signals over interval:", "Nb of steps:" };
			int[] values = { aliveSignals, -9999999 };
			MessageProfiler.getMessageProfiler().printInOnce(toPrint, true,
					items, values);
			System.exit(0);
		}
		currentStep++;
	}
}
