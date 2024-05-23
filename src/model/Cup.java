package model;

public class Cup {
	private String cupId;
	private String cupName;
	private int cupPrice;
	private int quantity;
	private int total;
	public Cup(String cupId, String cupName, int cupPrice, int quantity, int total) {
		super();
		this.cupId = cupId;
		this.cupName = cupName;
		this.cupPrice = cupPrice;
		this.quantity = quantity;
		this.total = total;
	}
	public String getCupId() {
		return cupId;
	}
	public void setCupId(String cupId) {
		this.cupId = cupId;
	}
	public String getCupName() {
		return cupName;
	}
	public void setCupName(String cupName) {
		this.cupName = cupName;
	}
	public int getCupPrice() {
		return cupPrice;
	}
	public void setCupPrice(int cupPrice) {
		this.cupPrice = cupPrice;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	
	
}
