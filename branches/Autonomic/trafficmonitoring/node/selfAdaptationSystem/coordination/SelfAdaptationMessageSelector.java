package node.selfAdaptationSystem.coordination;


public abstract class SelfAdaptationMessageSelector {
	
	/**
	 * Checks whether or not the computation employing this Message Selector is interested
	 *  in retrieving the given self-adaptation message
	 */
	public abstract boolean select(SelfAdaptationMessage message);

}
