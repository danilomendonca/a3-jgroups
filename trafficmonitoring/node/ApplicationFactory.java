package node;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.logging.LogManager;

import javax.swing.JFrame;

import logging.MultiOutputStream;
import node.agentMiddleware.communication.middleware.MessageProfiler;
import node.developer.sensors.Camera;
import node.hostInfrastructure.SimulatedCommunicationNetwork;
import simulator.TrafficSimulator;
import utilities.threading.ThreadManager;
import GUI.ControlPanel;
import GUI.SimulatorWindow;
import a3Solution.A3CameraNode;

public class ApplicationFactory implements Runnable {
	// public Vector<CameraNode> cameraNodes;
	private ArrayList<A3CameraNode> cameraNodes;
	public boolean running = false;
	public ThreadManager tm;

	public TrafficSimulator trafficSimulator;
	public SimulatorWindow simulatorWindow;
	public boolean GUI_ON = true;

	public ApplicationFactory(int scenario, int test, boolean threading,
			boolean selfhealing, int healInterval, int windowXSize,
			int windowYSize, int cameraRange) {

		// create simulator and GUI
		createSimAndGui(scenario, test, windowXSize, windowYSize, cameraRange);

		// create camera nodes
		createCameraNodes(threading, selfhealing, healInterval);

		// set failing nodes
		A3CameraNode[] failingNodes = this.cameraNodes
				.toArray(new A3CameraNode[0]);
		int[] failTimes = { 250 };
		trafficSimulator.setNodeFailureScenario(failingNodes, failTimes);
	}

	// @Pieter
	public ApplicationFactory(int scenario, int test, boolean threading,
			boolean selfhealing, int healInterval) {
		// Default values
		// int windowXSize = 400;
		// int windowYSize = 100;
		// int cameraRange = 30;
		this(scenario, test, threading, selfhealing, healInterval, 400, 100, 30);
	}

	/******************
	 * * INITIALIZATION * *
	 ******************/

	private void createSimAndGui(int scenario, int test, int windowXSize,
			int windowYSize, int cameraRange) {
		trafficSimulator = new TrafficSimulator(scenario, test);
		if (GUI_ON) {
			JFrame frame = new JFrame("Control panel");
			frame.setContentPane(new ControlPanel(trafficSimulator));
			frame.pack();
			// @Pieter
			// frame.setVisible(true);
			// @Pieter
			trafficSimulator.init(cameraRange);
			// @Pieter
			simulatorWindow = new SimulatorWindow(
					this.trafficSimulator.getNetwork(), GUI_ON, windowXSize,
					windowYSize);
			simulatorWindow.setLocation(frame.getX(),
					frame.getY() + frame.getHeight());
			// @Pieter
			// simulatorWindow.start();
		} else {
			new ControlPanel(trafficSimulator);
			trafficSimulator.init(cameraRange);
		}
		// @Pieter
		// trafficSimulator.start();
	}

	private void createCameraNodes(boolean threading, boolean selfhealing,
			int healInterval) {
		// cameraNodes = new Vector<CameraNode>();
		cameraNodes = new ArrayList<A3CameraNode>();

		if (threading) {
			System.out.println("System started in multi threaded mode");
			// @Pieter
			// SimulatedCommunicationNetwork.getInstance().start();
			for (Enumeration<Camera> e = trafficSimulator.getNetwork()
					.getCameras().elements(); e.hasMoreElements();) {
				Camera camera = e.nextElement();
				// @Pieter
				// cameraNodes.add(new CameraNode(selfhealing,
				// camera.getAgentID()
				// .toString(), camera, (float) 0.8, trafficSimulator
				// .getNetwork(), healInterval));
			}
			// for (Enumeration<CameraNode> e = this.cameraNodes.elements(); e
			// .hasMoreElements();) {
			// // @Pieter
			// // e.nextElement().start();
			// }
		} else {
			System.out.println("System started in single threaded mode");
			this.tm = new ThreadManager();
			for (Enumeration<Camera> e = trafficSimulator.getNetwork()
					.getCameras().elements(); e.hasMoreElements();) {
				Camera camera = e.nextElement();
				// Pieter
				// cameraNodes.add(new CameraNode(selfhealing,
				// camera.getAgentID()
				// .toString(), camera, (float) 0.8, tm, trafficSimulator
				// .getNetwork(), healInterval));
				cameraNodes.add(new A3CameraNode(
						camera.getAgentID().toString(), tm, camera));
			}
			// @Pieter
			// this.start();
		}
	}

	/***************
	 * * MAIN METHOD * *
	 ***************/

	// int scenario, int test, boolean threading, boolean selfhealing, int
	// healInterval, String outputfile

	public static void main(String[] args) {

		LogManager.getLogManager().reset();

		File file = new File("C:\\Users\\user\\Desktop\\log.txt");
		PrintStream printStream;
		try {
			MultiOutputStream multiOut = new MultiOutputStream(System.out,
					new FileOutputStream(file));
			printStream = new PrintStream(multiOut);
			System.setOut(printStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		if (args.length > 5)
			MessageProfiler.setOutputFile(args[5]);

		// @Pieter
		// ApplicationFactory mwf = new
		// ApplicationFactory(Integer.parseInt(args[0]),
		// Integer.parseInt(args[1]),
		// Boolean.parseBoolean(args[2]),
		// Boolean.parseBoolean(args[3]),
		// Integer.parseInt(args[4]));

		// Other arguments (for scenarios or tests) are not supported ...
		ApplicationFactory mwf = new ApplicationFactory(4, 1, false, false, 0);

		// @Pieter
		boolean multithreading = false;
		// Start threads
		if (mwf.GUI_ON)
			mwf.simulatorWindow.start();
		mwf.trafficSimulator.start();
		if (multithreading) {
			SimulatedCommunicationNetwork.getInstance().start();
			// for (CameraNode cameraNode : mwf.cameraNodes)
			// cameraNode.start();
		} else
			mwf.start();

	}

	/*************
	 * * THREADING * *
	 *************/

	public void run() {
		while (running) {
			// @Pieter
			this.step();
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
			}
		}
	}

	// @Pieter
	public void step() {
		tm.step();
		SimulatedCommunicationNetwork.getInstance().step();
	}

	public void start() {
		if (!running) {
			running = true;
			new Thread(this, "ApplicationFactoryThread").start();
		}
	}

	public void stop() {
		running = false;
	}
}
