import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class CF {

	private final boolean uuCF; // user-user (true) or item-item (false) CF
	private final Graph data;
	private Graph dataCopy;
	private final int k; // number of neighbor points
	private final DistanceFunction distFunc;
	private int nUsers;
	private int nItems;
	private Map<Integer, Byte> mu;

	public CF(Graph data, int k, DistanceFunction distFunc, boolean uuCF) {
		this.data = data;
		this.k = k;
		this.distFunc = distFunc;
		this.nUsers = data.userCount();
		this.nItems = data.itemCount();
		this.uuCF = uuCF;
	}

	public CF(Graph data, int k, DistanceFunction distFunc) {
		this(data, k, distFunc, true);
	}

	private void normalize() {
		List<Integer> users = data.getListUsers();
		try {
			dataCopy = data.clone();
		} catch (CloneNotSupportedException e) {
			dataCopy = data;
			e.printStackTrace();
		}
		mu = dataCopy.getListUsers().stream().collect(Collectors.toMap(x -> x, x -> (byte) 0));

		for (int i = 0; i < nUsers; i++) {
			int user = users.get(i);
			List<Pair<Integer, Byte>> items = data.getItemsRatedByUser(user);

			double average = items.stream().map(x -> x.getValue()).collect(Collectors.toList()).stream()
					.mapToInt(x -> x).average().getAsDouble();

			// normalize
			List<Pair<Integer, Byte>> itemsCopy = dataCopy.getItemsRatedByUser(user);
			itemsCopy.forEach(
					x -> itemsCopy.add(new Pair<Integer, Byte>(x.getKey(), (byte) (x.getValue() - mu.get(user)))));
		}
	}

	private void refresh() {
		normalize();
	}

	private float _pred(int user, int item, int normalized) {
		List<Integer> usersRatedItem = data.getUsersRatedItem(item);
		//	user   , cosine similarity
		Map<Integer, Float> similarity = new HashMap<Integer, Float>();
		List<Pair<Integer, Byte>> items = data.getItemsRatedByUser(user);
		int[] vector1 = data.getListItems().stream()
			.mapToInt(x -> 
				items.stream().anyMatch(y -> x == y.getKey())
					? items.stream().filter(y -> y.getKey() == x).findFirst().get().getValue()
					: 0)
			.toArray();
		for (int userRatedItem : usersRatedItem) {
			List<Pair<Integer, Byte>> items2 = data.getItemsRatedByUser(userRatedItem);
			int[] vector2 = data.getListItems().stream()
				.mapToInt(x -> 
					items2.stream().anyMatch(y -> x == y.getKey())
						? items2.stream().filter(y -> y.getKey() == x).findFirst().get().getValue()
						: 0)
				.toArray();
			similarity.put(userRatedItem, distFunc.calculate(vector1, vector2));
		}
		
		Map<Integer, Float> sortedSimilarity = new TreeMap<Integer, Float>(new ValueComparator(similarity));
		sortedSimilarity.putAll(similarity);
		Map<Integer, Float> nearest = new HashMap<Integer, Float>();
		int i = 0;
		for (Map.Entry<Integer, Float> entry : sortedSimilarity.entrySet()) {
			if (i++ >= k) break;
			nearest.put(entry.getKey(), entry.getValue());
		}

		return 0;
	}

}
