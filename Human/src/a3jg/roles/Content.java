package a3jg.roles;

import java.io.Serializable;

import org.jgroups.Address;

public class Content implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Address ad;
	private String s;
	
	public Address getAd() {
		return ad;
	}
	
	public void setAd(Address ad) {
		this.ad = ad;
	}
	
	public String getS() {
		return s;
	}
	
	public void setS(String s) {
		this.s = s;
	}
	
}
