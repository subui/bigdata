import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javafx.util.Pair;

public class CF {

	private final boolean uuCF; // user-user (true) or item-item (false) CF
	private final Matrix data;
	private Matrix dataCopy;
	private final int k; // number of neighbor points
	private final DistanceFunction distFunc;
	private int nUsers;
	private int nItems;
	private Map<Integer, Byte> mu;

	public CF(Matrix data, int k, DistanceFunction distFunc, boolean uuCF) {
		this.data = data;
		this.k = k;
		this.distFunc = distFunc;
		this.nUsers = data.userCount();
		this.nItems = data.itemCount();
		this.uuCF = uuCF;
	}

	public CF(Matrix data, int k, DistanceFunction distFunc) {
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
			List<Pair<Integer, Integer>> items = data.getItemsRatedByUser(user);

			double average = items.stream().map(x -> x.getValue()).collect(Collectors.toList()).stream()
					.mapToInt(x -> x).average().getAsDouble();

			// normalize
			List<Pair<Integer, Integer>> itemsCopy = dataCopy.getItemsRatedByUser(user);
			itemsCopy.forEach(x -> itemsCopy.add(new Pair<Integer, Integer>(x.getKey(), x.getValue() - mu.get(user))));
		}
	}

	private void refresh() {
		normalize();
	}

	private double _pred(int user, int item, boolean normalized) {
		List<Pair<Integer, Integer>> users = data.getUsersRatedItem(item);
		List<Pair<Integer, Integer>> items = data.getItemsRatedByUser(user);
		// similarity between user and others
		// key: user
		// value: similarity
		Map<Integer, Double> similarity = new HashMap<Integer, Double>();
		// vector rating by user
		double[] vector1 = data.getListUsers()
				.stream()
				.mapToDouble(x -> 
					items.stream().anyMatch(y -> x == y.getKey())
						? items.stream().filter(y -> y.getKey() == x).findFirst().get().getValue()
						: 0)
				.toArray();
		
		for (int u : data.getListUsers()) {
			List<Pair<Integer, Integer>> items2 = data.getItemsRatedByUser(u);
			// vector rating by u
			double[] vector2 = data.getListUsers()
					.stream()
					.mapToDouble(x -> 
						items2.stream().anyMatch(y -> x == y.getKey())
							? items2.stream().filter(y -> y.getKey() == x).findFirst().get().getValue()
							: 0)
					.toArray();
			similarity.put(u, distFunc.calculate(vector1, vector2));
		}
		
		Map<Integer, Double> sortedSimilarity = new TreeMap<Integer, Double>(new ValueComparator(similarity));
		for (Map.Entry<Integer, Double> entry : similarity.entrySet()) {
			if (users.stream().anyMatch(x -> x.getKey() == entry.getKey())) {
				sortedSimilarity.put(entry.getKey(), entry.getValue());
			}
		}
		
		double sum1 = 0;
		double sum2 = 1e-8;
		int i = 0;
		for (Map.Entry<Integer, Double> entry : sortedSimilarity.entrySet()) {
			if (i++ >= k) break;
			sum1 += entry.getValue() * users.stream().filter(x -> x.getKey() == entry.getKey()).findFirst().get().getValue();
			sum2 += Math.abs(entry.getValue());
		}

		return sum1 / sum2 + (normalized ? 0 : mu.get(user));
	}
	
	private double pred(int user, int item, boolean normalized) {
		return uuCF
				? _pred(user, item, normalized)
				: _pred(item, user, normalized);
	}
	
	private List<Integer> recommend(int user, boolean normalized) {
		List<Pair<Integer, Integer>> items = data.getItemsRatedByUser(user);
		List<Integer> recommendedItems = new ArrayList<Integer>();
		for (int item : data.getListItems()) {
			if (items.stream().anyMatch(x -> x.getKey() == item)) continue;
			double rating = _pred(user, item, true);
			if (rating > 0) {
				recommendedItems.add(item);
			}
		}
		
		return recommendedItems;
	}
	
	public void printRecommendation() {
		System.out.println("Recommendation: ");
		for (int user : data.getListUsers()) {
			List<Integer> recommendedItems = recommend(user, true);
			if (uuCF) {
				System.out.println("\tRecommend item(s): " + recommendedItems + " to user " + user);
			} else {
				System.out.println("\tRecommend item " + user + " to user(s) " + recommendedItems);
			}
		}
	}

}
