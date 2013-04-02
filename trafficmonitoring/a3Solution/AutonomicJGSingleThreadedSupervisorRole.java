package a3Solution;

import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.KnowledgeBaseFactory;
import org.drools.builder.KnowledgeBuilder;
import org.drools.builder.KnowledgeBuilderError;
import org.drools.builder.KnowledgeBuilderErrors;
import org.drools.builder.KnowledgeBuilderFactory;
import org.drools.builder.ResourceType;
import org.drools.io.ResourceFactory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;
import org.jgroups.Address;
import org.jgroups.Message;

import utilities.threading.ThreadManager;
import A3JGroups.A3JGMessage;

public abstract class AutonomicJGSingleThreadedSupervisorRole extends
		A3JGSingleThreadedSupervisorRole {

	private StatefulKnowledgeSession statefulSession;
	private MAPEManagerSingleThreaded mapeManager;
	private ThreadManager tm;

	public AutonomicJGSingleThreadedSupervisorRole(int resourceCost,
			String rulesFilename, ThreadManager tm) {
		super(resourceCost, tm);
		mapeManager = new MAPEManagerSingleThreaded(this, 2000);
		this.tm = tm;
		loadKB(rulesFilename);
		executeRules();
	}

	@Override
	public void run() {

		// new Thread(new MAPEManager(this, 2000)).start();

	}

	public MAPEManagerSingleThreaded getMapeManager() {
		return mapeManager;
	}

	public void setMapeManager(MAPEManagerSingleThreaded mapeManager) {
		this.mapeManager = mapeManager;
	}

	@Override
	public void receive(Message msg) {
		A3JGMessage mex = (A3JGMessage) msg.getObject();
		if (mex.getValueID().equals("A3MapeManagerMessage")) {
			Object fact = mex.getContent();
			insertOrUpdateKB(fact);
			executeRules();
		} else
			super.receive(msg);
	}

	@Override
	public void messageFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateFromFollower(A3JGMessage msg) {
		// TODO Auto-generated method stub

	}

	@Override
	public int fitnessFunc() {
		// TODO Auto-generated method stub
		return 0;
	}

	public abstract boolean Monitor();

	public abstract boolean Analyse();

	public abstract boolean Plan();

	public abstract boolean Execute();

	public boolean sendMAPEMessageToFollowers(A3JGMessage mex,
			List<Address> dest) {

		mex.setValueID("MAPEMessage" + mex.getValueID());

		return sendMessageToFollower(mex, dest);

	}

	public void insertOrUpdateKB(Object fact) {

		// TODO considerare che ci sono due modi diversi per valutare se un fact
		// è già nella KB

		if (fact == null) {
			return;
		}
		FactHandle factHandle = statefulSession.getFactHandle(fact);
		if (factHandle == null) {
			statefulSession.insert(fact);
		} else {
			statefulSession.update(factHandle, fact);
		}

	}

	public void executeRules() {

		/* int fired = */statefulSession.fireAllRules();
		// System.out.println("Fire di " + fired + " rules");

	}

	private void loadKB(String rulesFilename) {
		try {
			// load up the knowledge base
			KnowledgeBase kbase = readKnowledgeBase(rulesFilename);
			statefulSession = kbase.newStatefulKnowledgeSession();
			statefulSession.setGlobal("mapeManager", mapeManager);
			// Logging
			// KnowledgeRuntimeLogger logger =
			// KnowledgeRuntimeLoggerFactory.newFileLogger(ksession, "test");

			// session usage
			// ksession.insert(message);
			// ksession.fireAllRules();
			// logger.close();

		} catch (Throwable t) {
			t.printStackTrace();
		}
	}

	private static KnowledgeBase readKnowledgeBase(String rulesFilename)
			throws Exception {
		KnowledgeBuilder kbuilder = KnowledgeBuilderFactory
				.newKnowledgeBuilder();
		kbuilder.add(ResourceFactory.newClassPathResource(rulesFilename),
				ResourceType.DRL);
		KnowledgeBuilderErrors errors = kbuilder.getErrors();
		if (errors.size() > 0) {
			for (KnowledgeBuilderError error : errors) {
				System.err.println(error);
			}
			throw new IllegalArgumentException("Could not parse knowledge.");
		}
		KnowledgeBase kbase = KnowledgeBaseFactory.newKnowledgeBase();
		kbase.addKnowledgePackages(kbuilder.getKnowledgePackages());
		return kbase;
	}

}
