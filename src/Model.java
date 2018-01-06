
public final class Model {
	
	private String user;
	private String item;
	private double rating;
	
	public Model(String user, String item, double rating) {
		this.user = user;
		this.item = item;
		this.rating = rating;
	}
	
	public String getUser() {
		return user;
	}
	
	public String getItem() {
		return item;
	}
	
	public double getRating() {
		return rating;
	}
	
	protected void setRating(double rating) {
		this.rating = rating;
	}
	
}
