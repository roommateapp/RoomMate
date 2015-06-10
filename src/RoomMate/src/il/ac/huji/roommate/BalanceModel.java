package il.ac.huji.roommate;

/*
* Class representing BalanceModel object, used by the BalanaceAdapter
*/
public class BalanceModel {

	private String name;
	private double debt;
	private double credit;
	
	public BalanceModel(String name, double debt, double credit){
		setName(name);
		setDebt(debt);
		setCredit(credit);
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
