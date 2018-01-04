import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class Matrix implements Cloneable {

	private final List<List<Integer>> data;
	private Set<Integer> users;
	private Set<Integer> items;

	public Matrix() {
		data = new ArrayList<List<Integer>>();
		users = new HashSet<Integer>();
		items = new HashSet<Integer>();
	}

	public int userCount() {
		return users.size();
	}

	public List<Integer> getListUsers() {
		return new ArrayList<Integer>(users);
	}

	public int itemCount() {
		return items.size();
	}

	public List<Integer> getListItems() {
		return new ArrayList<Integer>(items);
	}

	public List<Pair<Integer, Integer>> getItemsRatedByUser(int user) {
		return data.stream()
				.filter(x -> x.get(0) == user)
				.map(x -> new Pair<Integer, Integer>(x.get(1), x.get(2)))
				.collect(Collectors.toList());
	}

	public List<Pair<Integer, Integer>> getUsersRatedItem(int item) {
		return data.stream()
				.filter(x -> x.get(1) == item)
				.map(x -> new Pair<Integer, Integer>(x.get(0), x.get(2)))
				.collect(Collectors.toList());
	}

	public void add(int user, int item, int rating) {
		data.add(List.of(user, item, rating));
		users.add(user);
		items.add(item);
	}

	protected Matrix clone() throws CloneNotSupportedException {
		return (Matrix) super.clone();
	}

}
