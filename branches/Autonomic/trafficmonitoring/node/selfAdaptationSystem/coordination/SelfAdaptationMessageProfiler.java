package node.selfAdaptationSystem.coordination;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;


public class SelfAdaptationMessageProfiler {
	
	private SelfAdaptationMessageProfiler(){}
	
	public static SelfAdaptationMessageProfiler getProfiler(){
		if(SelfAdaptationMessageProfiler.profiler == null){
			SelfAdaptationMessageProfiler.profiler = new SelfAdaptationMessageProfiler();
		}
		
		return SelfAdaptationMessageProfiler.profiler;
	}
	
	private static SelfAdaptationMessageProfiler profiler;
	
	public void setOutputFile(File outputFile){
		this.outputFile = outputFile;
	}
	
	private File outputFile;
	
	public void setExecutionCyclePrintInterval(int interval){
		this.executionCyclePrintInterval = interval;
	}
	
	private int executionCyclePrintInterval = 1;
	
	/**
	 * Configure whether to count only the self-adaptation messages that are sent
	 * by computations that are actually busy adapting the traffic monitoring system, or
	 * all self-adaptation messages (e.g. also the initial ping/echo monitor computation messages)
	 */
	public void setAdaptationProfilingOnly(boolean adaptationOnly){
		this.adaptationProfilingOnly = adaptationOnly;
	}
	
	private boolean adaptationProfilingOnly = true;
	
	
	/**************************	 
	 * 
	 *	Messages
	 *
	 **************************/
	
	public void registerIntraLoopMessage(SelfAdaptationMessage message){
		this.register(this.intraLoopMessageRegistry, message);
	}
	
	public void registerInterLoopMessage(SelfAdaptationMessage message){
		this.register(this.interLoopMessageRegistry, message);
	}
	
	private synchronized void register(HashMap<String, Integer> registry, SelfAdaptationMessage message){
		if(this.adaptationProfilingOnly && !message.sentDuringAdaptation()){
			//Message not sent during actual adaptation; discard.
			return;
		}		
		
		String scenarioType = message.getScenario().getScenarioType();
		
		//Self-Monitoring Scenarios
		if(scenarioType.equals("CameraSelfMonitoring")){
			this.countMessage(registry, "SelfMonitoring");
		}
		
		//Self-Healing Scenarios
		if(scenarioType.equals("MasterWithSlavesNodeFailure")
				|| scenarioType.equals("NeighborNodeFailure")
				|| scenarioType.equals("SlaveNodeFailure")){
			this.countMessage(registry, "SelfHealing");
		}
		
		//Self-Configuration Scenarios
		if(scenarioType.equals("CameraIntroduction")){
			this.countMessage(registry, "SelfConfiguration");
		}		
	}
	
	private void countMessage(HashMap<String, Integer> registry, String identifier){
		//Increment by one
		registry.put(identifier, this.getCurrentMessageCount(registry, identifier)+1);
	}
	
	private int getCurrentMessageCount(HashMap<String, Integer> registry, String identifier){
		Integer currentValue = registry.get(identifier);
		
		if(currentValue == null){
			return 0;
		}
		else{
			return currentValue;
		}
	}
	
	private HashMap<String, Integer> intraLoopMessageRegistry = new HashMap<String, Integer>();
	private HashMap<String, Integer> interLoopMessageRegistry = new HashMap<String, Integer>();
	
	
	/**************************	 
	 * 
	 *	PRINT
	 *
	 **************************/
	
	public synchronized void nextExecutionCycle(){
		if(this.executionCycle == 0){
			//Print headers for each column
			this.print = "ExecutionCycle " + 
			"SelfMonitoringINTRA SelfMonitoringINTER " +
			"SelfHealingINTRA SelfHealingINTER " +
			"SelfConfigurationINTRA SelfConfigurationINTER ";
			
			//Newline
			this.print += System.getProperty("line.separator");
		}
		else{
			//Check to see if printing is needed
			if(this.executionCyclesToNextPrint == 0){
				this.print += this.executionCycle + " ";

				//Self-Monitoring stats
				this.print += this.getCurrentMessageCount(intraLoopMessageRegistry, "SelfMonitoring") + " " +
				this.getCurrentMessageCount(interLoopMessageRegistry, "SelfMonitoring") + " ";

				//Self-Healing stats
				this.print += this.getCurrentMessageCount(intraLoopMessageRegistry, "SelfHealing") + " " +
				this.getCurrentMessageCount(interLoopMessageRegistry, "SelfHealing") + " ";

				//Self-Configuration stats
				this.print += this.getCurrentMessageCount(intraLoopMessageRegistry, "SelfConfiguration") + " " +
				this.getCurrentMessageCount(interLoopMessageRegistry, "SelfConfiguration") + " ";
				
				//Newline
				this.print += System.getProperty("line.separator");
				
				//Reset message registry's
				this.interLoopMessageRegistry.clear();
				this.intraLoopMessageRegistry.clear();
				
				//Reset execution cycle counter
				this.executionCyclesToNextPrint = this.executionCyclePrintInterval;
			}
			else{
				//just decrease print cycle counter
				this.executionCyclesToNextPrint--;
			}		
		}	

		//Increase execution cycle count
		this.executionCycle++;
	}
	
	private int executionCyclesToNextPrint = 0;
	private int executionCycle = 0;
	private String print;
	
	public synchronized void print(){
		try{
			FileWriter fstream = new FileWriter(this.outputFile);
			BufferedWriter out = new BufferedWriter(fstream);
					
			out.write(this.print);
			
			//Close the output stream
			out.close();
			
			this.print = null;
		}catch (Exception e){//Catch exception if any
			System.err.println("SelfAdaptationMessageProfiler Error: " + e.getMessage());
		}
	}

}
