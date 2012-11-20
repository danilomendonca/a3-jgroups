package a3jg.human;

import A3JGroups.A3JGNode;

public class HumanNode extends A3JGNode {

	private boolean awake = false;
	private int atime;
	private long start;
	
	public HumanNode(String ID) {
		super(ID);
	}

	public boolean isAwake() {
		return awake;
	}

	public void setAwake(boolean awake) {
		this.awake = awake;
	}

	public int getAtime() {
		return atime;
	}

	public void setAtime(int atime) {
		this.atime = atime;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}
	
}
