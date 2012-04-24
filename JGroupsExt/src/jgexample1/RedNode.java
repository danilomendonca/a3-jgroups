package jgexample1;

import A3JGroups.A3JGMiddleware;
import A3JGroups.A3JGNode;

public class RedNode extends A3JGNode{

	public RedNode(String name, A3JGMiddleware middleware) {
		super(name, middleware);
	}
	
	public RedNode(String name){
		super(name);
	}
}
