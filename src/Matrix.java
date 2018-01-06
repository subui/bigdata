import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class Matrix implements Cloneable {

	private final List<Model> data;
	private Set<Integer> users;
	private Set<Integer> items;

	public Matrix() {
		data = new ArrayList<>();
		users = new HashSet<>();
		items = new HashSet<>();
	}

	public int userCount() {
		return users.size();
	}

	public List<Integer> getListUsers() {
		return new ArrayList<>(users);
	}

	public int itemCount() {
		return items.size();
	}

	public List<Integer> getListItems() {
		return new ArrayList<>(items);
	}
	
	public double[] getVectorRatingByUser(int user) {
		double[] vector = new double[items.size()];
		List<Pair<Integer, Double>> items = getItemsRatedByUser(user);
		for (int i = 0; i < this.items.size(); i++) {
			final int index = i;
			Pair<Integer, Double> pair = items.stream()
					.filter(x -> x.getKey() == getListItems().get(index))
					.findFirst().orElse(null);
			vector[i] = pair != null ? pair.getValue() : 0;
		}
		return vector;
	}

	public List<Pair<Integer, Double>> getItemsRatedByUser(int user) {
		return data.stream()
				.filter(x -> x.getUser() == user)
				.map(x -> new Pair<Integer, Double>(x.getItem(), x.getRating()))
				.collect(Collectors.toList());
	}

	public List<Pair<Integer, Double>> getUsersRatedItem(int item) {
		return data.stream()
				.filter(x -> x.getItem() == item)
				.map(x -> new Pair<Integer, Double>(x.getUser(), x.getRating()))
				.collect(Collectors.toList());
	}

	public void add(int user, int item, double rating) {
		data.add(new Model(user, item, rating));
		users.add(user);
		items.add(item);
	}
	
	public void updateRating(int user, int item, double rating) {
		try {
			data.stream()
				.filter(x -> x.getUser() == user && x.getItem() == item)
				.findFirst().orElse(null)
				.setRating(rating);
		} catch (NullPointerException e) {
			//
		}
	}

	protected Matrix clone() throws CloneNotSupportedException {
		return (Matrix) super.clone();
	}

}
