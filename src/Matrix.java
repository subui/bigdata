import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Matrix {

	private final Map<String, Map<String, Double>> dataUser;
	private final Map<String, Map<String, Double>> dataItem;

	public Matrix() {
		dataUser = new HashMap<>();
		dataItem = new HashMap<>();
	}

	public int userCount() {
		return dataUser.size();
	}

	public List<String> getListUsers() {
		return new ArrayList<>(dataUser.keySet());
	}

	public int itemCount() {
		return dataItem.size();
	}

	public List<String> getListItems() {
		return new ArrayList<>(dataItem.keySet());
	}
	
	public double[] getVectorRatingByUser(String user) {
		Map<String, Double> items = getItemsRatedByUser(user);
		return getListItems().stream().mapToDouble(x -> items.containsKey(x) ? items.get(x) : 0).toArray();
	}

	public Map<String, Double> getItemsRatedByUser(String user) {
		return dataUser.get(user);
	}

	public Map<String, Double> getUsersRatedItem(String item) {
		return dataItem.get(item);
	}

	public void add(String user, String item, double rating) {
		if (!dataUser.containsKey(user)) {
			dataUser.put(user, new HashMap<>());
		}
		dataUser.get(user).put(item, rating);

		if (!dataItem.containsKey(item)) {
			dataItem.put(item, new HashMap<>());
		}
		dataItem.get(item).put(user, rating);
	}
	
	public void updateRating(String user, String item, double rating) {
		dataUser.get(user).put(item, rating);
		dataItem.get(item).put(user, rating);
	}

}
