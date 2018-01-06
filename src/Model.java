
public final class Model {
	
	private int user;
	private int item;
	private double rating;
	
	public Model(int user, int item, double rating) {
		this.user = user;
		this.item = item;
		this.rating = rating;
	}
	
	public int getUser() {
		return user;
	}
	
	public int getItem() {
		return item;
	}
	
	public double getRating() {
		return rating;
	}
	
	protected void setRating(double rating) {
		this.rating = rating;
	}
	
}
