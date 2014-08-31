package il.ac.huji.roommate;

public class BalanceModel {

	private String name;
	private double debt;
	private double credit;
	
	public BalanceModel(String name, double debt, double credit){
		this.name = name;
		this.debt = debt;
		this.credit = credit;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getDebt() {
		return debt;
	}
	public void setDebt(double debt) {
		this.debt = debt;
	}
	public double getCredit() {
		return credit;
	}
	public void setCredit(double credit) {
		this.credit = credit;
	}

}
