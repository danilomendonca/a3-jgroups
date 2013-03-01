package node.agentMiddleware.communication.middleware;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;

import node.organizationMiddleware.contextManager.contextDirectories.Context;
import node.organizationMiddleware.contextManager.contextDirectories.Organization;
import node.organizationMiddleware.contextManager.contextDirectories.RolePosition;


public class CopyOfMessageProfiler {

	private int flush = 500;
	private int whileLoops = 10; 
	
	private int NbOfSendMessages;
	private int NbOfReceivedMessages;
	private int aliveRequest;
	private int aliveSignal;

	
	private long lastPrintoutTime;
	
	private BufferedWriter output;
	private Hashtable<String,Integer> register;
	private Hashtable<String,Integer> cache;
	
	
	private static CopyOfMessageProfiler instance;
	private static String outputFile;
	
	public static CopyOfMessageProfiler getInstance(){
		if(instance == null)
			instance = new CopyOfMessageProfiler();
		return instance;
	}
	
	private CopyOfMessageProfiler(){
		try {
			this.register = new Hashtable<String, Integer>();
			this.cache = new Hashtable<String, Integer>();
			this.lastPrintoutTime = System.currentTimeMillis();
			String opf = "output.txt";
			if(outputFile != null) opf = outputFile;
			this.output = new BufferedWriter(new FileWriter(opf, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void setOutputFile(String opf){
		outputFile = opf;
	}
	
	
	public void register(String send){
		try{
			int currentValue = register.get(send);
			currentValue++;
			register.put(send,currentValue);
		} catch (NullPointerException e){
			//send is not yet registered
			register.put(send,1);
		}
	}
	
	private void print(){
		Enumeration<String> keys = register.keys();
		int totalMessagesSent = 0;
		
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			int currentValue = register.get(key);
			int previouslyPrintedValue = 0;
			
			try{
				previouslyPrintedValue = cache.get(key);
			}catch (Exception e){}
			
			try {
				this.output.write(key + "\t" + (currentValue - previouslyPrintedValue) + "\t");
			} catch (IOException e) {}
			
			totalMessagesSent += currentValue - previouslyPrintedValue;
			cache.put(key,currentValue);
		}

		try {
			this.output.write("total number of msg sent in interval" + "\t" + totalMessagesSent+ "\t");
		} catch (IOException e) {}
	}
	
	public void printOutMessageInfo(){
//		whileLoops--;
//    	if(whileLoops == 0){
//    		long intervalLength = System.currentTimeMillis() - lastPrintoutTime;
//    		try {
//    			this.output.write(Long.toString(System.currentTimeMillis()) + "\t");
//				this.output.write(Long.toString(intervalLength) + "\t");
//				print();
//				this.output.newLine();
//			} catch (IOException e) {}
//    		whileLoops = 10;
//    		lastPrintoutTime = System.currentTimeMillis();
//      	}
//    	
//    	flush--;
//    	if(flush == 0){
//    		try {
//				this.output.flush();
//			} catch (IOException e) {}
//			flush = 500;
//    	}
	}
	
	public void resetCounters(){
		this.NbOfReceivedMessages = 0;
		this.NbOfSendMessages = 0;
	}
	
	public void countSendMessage(){
		NbOfSendMessages++;
	}
	
	public void countReceivedMessage(){
		NbOfReceivedMessages++;
	}
	
	public int getNbOfReceivedMessages(){
		return this.NbOfReceivedMessages;
	}
	
	public int getNbOfSendMessages(){
		return this.NbOfSendMessages;
	}
	
	public void printTerminationMsg(String receiver){
		try {
			this.output.write(System.currentTimeMillis() + "\t" +receiver + " received a termination message.");
			this.output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printStartAsSlave(Organization organization, String nodeIdentifier){
		try {
			this.output.write(System.currentTimeMillis() + "\t" +nodeIdentifier + " received a start as slave message for organization " +
					" "+organization.getId()+" with master "+organization.getMasterID());
			this.output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printNodeFailure(String id){
		try {
			this.output.write(System.currentTimeMillis() + "\t" +id + " FAILED");
			this.output.newLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void countStartAsSlave(Organization organization, String nodeIdentifier) {
		//printStartAsSlave(organization,nodeIdentifier);
		register("StartAsSlave");
	}


	public void countTerminationMessage(String receiver) {
		//printTerminationMsg(receiver);
		register("TerminationMsg");
	}


	public void countAliveRequest() {
		this.aliveRequest++;
	}


	public void countAliveSignal() {
		this.aliveSignal++;
		
	}

	public void reset(){
		System.out.println("--- Profiler reset");
		try {
			this.register = new Hashtable<String, Integer>();
			this.cache = new Hashtable<String, Integer>();
			this.lastPrintoutTime = System.currentTimeMillis();
			String opf = "output.txt";
			if(outputFile != null) opf = outputFile;
			this.output = new BufferedWriter(new FileWriter(opf, true));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void print(String[] whatToPrint, boolean printTotal){
		int totalMessagesSent = 0;
		
		Enumeration<String> keys = register.keys();
		
		while(keys.hasMoreElements()){
			String key = keys.nextElement();
			int currentValue = register.get(key);
			int previouslyPrintedValue = 0;
			
			try{
				previouslyPrintedValue = cache.get(key);
			}catch (Exception e){}
			
			if(this.needsToPrint(key, whatToPrint)){
				try {
					this.output.write(key + "\t" + (currentValue - previouslyPrintedValue) + "\t");
				} catch (IOException e) {}
			}
			
			totalMessagesSent += currentValue - previouslyPrintedValue;
			cache.put(key,currentValue);
		}
		
		if(printTotal)
			try {
				this.output.write("total number of msg sent in interval" + "\t" + totalMessagesSent+ "\t");
			} catch (IOException e) {}
	}
	
	private boolean needsToPrint(String item, String[] toPrint){
		for(int i = 0; i<toPrint.length; i++){
			if(toPrint[i].equals(item))
				return true;
		}
		return false;
	}
	
	public void printOutMessageInfo(String[] whatToPrint, boolean printTotal){
		whileLoops--;
    	if(whileLoops == 0){
    		long intervalLength = System.currentTimeMillis() - lastPrintoutTime;
    		try {
    			this.output.write(Long.toString(System.currentTimeMillis()) + "\t");
				this.output.write(Long.toString(intervalLength) + "\t");
				print(whatToPrint, printTotal);
				this.output.newLine();
			} catch (IOException e) {}
    		whileLoops = 10;
    		lastPrintoutTime = System.currentTimeMillis();
      	}
    	
    	flush--;
    	if(flush == 0){
    		try {
				this.output.flush();
			} catch (IOException e) {}
			flush = 500;
    	}
	}
	
	public synchronized void startNewExperiment(int nbNodes) {
		try {
			this.output.write("**********************************************************");this.output.newLine();
			this.output.write("START  for "+nbNodes+ " nodes");this.output.newLine();
		
			this.cache = new Hashtable<String, Integer>();
			this.register = new Hashtable<String, Integer>();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public synchronized void stopExperiment(Context ctx1, Context ctx2) {
		try{	
			Enumeration<String> keys = register.keys();
			int totalMessagesSent = 0;
			
			while(keys.hasMoreElements()){
				String key = keys.nextElement();
				int currentValue = register.get(key);
				
				this.output.write(key +"\t"+ currentValue+ "\t");
				
				totalMessagesSent += currentValue;
			}

			try {
				this.output.write("total number of msg" + "\t" + totalMessagesSent+ "\t");
				this.output.newLine();
				printOrganizationInfo(ctx1);
				printOrganizationInfo(ctx2);
				this.output.write("STOP");this.output.newLine();
				this.output.write("**********************************************************");this.output.newLine();
				this.output.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}catch(IOException e){
			e.printStackTrace();
		}
		
	}


	private void printOrganizationInfo(Context ctx1) throws IOException {
		this.output.write("Org id : "+ctx1.getPersonalOrg().getId() + " , Master : "+ctx1.getPersonalOrg().getMasterID());
		this.output.newLine();
		for(RolePosition rp : ctx1.getPersonalOrg().getFilledRolePositions()){
			this.output.write(rp.getAgentId() + "\t");
		}
		this.output.newLine();
		
	}
	
	public void printInOnce(String[] whatToPrint, boolean printTotal){
		long intervalLength = System.currentTimeMillis() - lastPrintoutTime;
		try {
			this.output.write(Long.toString(System.currentTimeMillis()) + "\t");
			this.output.write(Long.toString(intervalLength) + "\t");
			print(whatToPrint, printTotal);
			this.output.newLine();
		} catch (IOException e) {}
	}
	
	public void flush(){
		try {
			this.output.flush();
		} catch (IOException e) {}
	}
	
	
	
}
