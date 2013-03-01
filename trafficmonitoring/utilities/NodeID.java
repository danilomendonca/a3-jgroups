package utilities;

//@Pieter
public class NodeID implements Comparable<NodeID>{

	private int id;
	
	public NodeID(int id){
		this.id = id;
	}

	public int getId() {
		return id;
	}
	
	@Override
	public boolean equals(Object o){
		try{
			return ((NodeID) o).getId() == id;
		}catch(ClassCastException e){
			return false;
		}
	}
	
	@Override
	//@Pieter
	public int compareTo(NodeID o){		
		if(this.getId() < o.getId())
			return -1;
		else if(this.getId() == o.getId())
			return 0;
		else
			return 1;
	}
	
	@Override
	//@Pieter
	public int hashCode(){
		return (new Integer(this.id)).hashCode();
	}
	
	@Override
	public String toString(){
		return Integer.toString(id);
	}

	/**
	 * deep copy
	 * 
	 */
	public NodeID copy() {
		return new NodeID(getId());
	}
	
}
