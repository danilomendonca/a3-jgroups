package A3JGroups.autonomic;

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
import org.jgroups.Message;

import A3JGroups.A3JGMessage;
import A3JGroups.A3JGSupervisorRole;

public abstract class AutonomicJGSupervisorRole extends A3JGSupervisorRole {

	private StatefulKnowledgeSession statefulSession;
	private MAPEManager mapeManager;

	public AutonomicJGSupervisorRole(int resourceCost, String rulesFilename) {
		super(resourceCost);
		mapeManager = new MAPEManager(this, 2000);
		loadKB(rulesFilename);
		executeRules();
	}

	@Override
	public void run() {

		// new Thread(new MAPEManager(this, 2000)).start();

	}

	public MAPEManager getMapeManager() {
		return mapeManager;
	}

	public void setMapeManager(MAPEManager mapeManager) {
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
