package utilities;

public class ThreevaluedLogic {

	private boolean unknown;
	private boolean twovaluedLogic;
	
	public ThreevaluedLogic(){
		unknown = true;
	}
	
	public void set(boolean b){
		unknown = false;
		twovaluedLogic = b;
	}
	
	public void setTrue(){
		unknown = false;
		twovaluedLogic = true;
	}
	
	public void setFalse(){
		unknown = false;
		twovaluedLogic = false;
	}
	
	public void setUnknown(){
		unknown = true;
	}
	
	public boolean isTrue(){
		if(unknown)
			return false;
		if(twovaluedLogic)
			return true;
		return false;
	}
	
	public boolean isFalse(){
		if(unknown)
			return false;
		if(!twovaluedLogic)
			return true;
		return false;
	}
	
	public boolean unknown(){
		if(unknown)
			return true;
		else return false;
	}
	
	public String toString(){
		if(isTrue())
			return "true";
		if(isFalse())
			return "false";
		return "unknown";
	}
}
