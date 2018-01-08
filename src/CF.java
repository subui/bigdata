import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class CF {

	private final boolean uuCF; // user-user (true) or item-item (false) CF
	private final Matrix data;
	private final List<String> listUsers;
	private final List<String> listItems;
	private final int k; // number of neighbor points
	private final DistanceFunction distFunc;

	public CF(Matrix data, int k, DistanceFunction distFunc, boolean uuCF) {
		this.data = data;
		this.listUsers = data.getListUsers();
		this.listItems = data.getListItems();
		this.k = k;
		this.distFunc = distFunc;
		this.uuCF = uuCF;
	}

	public CF(Matrix data, int k, DistanceFunction distFunc) {
		this(data, k, distFunc, true);
	}

	public void normalize() {
		System.out.println("Normalizing...");

		for (String user : listUsers) {
			Map<String, Double> items = data.getItemsRatedByUser(user);

			double average = items.values().stream().mapToDouble(x -> x).average().getAsDouble();

			// normalize
			items.entrySet().forEach(x -> data.updateRating(user, x.getKey(), x.getValue() - average));
		}
	}

	private double _pred(String user, String item) {
		Map<String, Double> users = data.getUsersRatedItem(item);
		// similarity between user and others
		// key: user
		// value: similarity
		Map<String, Double> similarity = new HashMap<>();
		Map<String, Double> vector1 = data.getItemsRatedByUser(user);
		
		for (Entry<String, Double> entry : users.entrySet()) {
			Map<String, Double> vector2 = data.getItemsRatedByUser(entry.getKey());
			similarity.put(entry.getKey(), distFunc.calculate(vector1, vector2));
		}

		Map<String, Double> sortedSimilarity = new TreeMap<String, Double>(new ValueComparator(similarity));
		/*for (Entry<String, Double> entry : users.entrySet()) {
			sortedSimilarity.put(entry.getKey(), similarity.get(entry.getKey()));
		}*/
		/*for (Map.Entry<String, Double> entry : similarity.entrySet()) {
			sortedSimilarity.put(entry.getKey(), entry.getValue());
		}*/
		sortedSimilarity.putAll(similarity);
		
		double sum1 = 0;
		double sum2 = 1e-8;
		int i = 0;
		for (Map.Entry<String, Double> entry : sortedSimilarity.entrySet()) {
			if (i++ >= k) break;
			sum1 += entry.getValue() * users.get(entry.getKey());
			sum2 += Math.abs(entry.getValue());
		}

		return sum1 / sum2;
	}
	
	private double pred(String user, String item) {
		return uuCF
				? _pred(user, item)
				: _pred(item, user);
	}
	
	private List<String> recommend(String user, boolean normalized) {
		Map<String, Double> items = data.getItemsRatedByUser(user);
		List<String> recommendedItems = new ArrayList<>();
		int i = 0;
		for (String item : listItems) {
			System.out.println((++i) + "/" + data.itemCount());
			if (items.containsKey(item)) continue;
			double rating = pred(user, item);
			if (rating > 0) {
				recommendedItems.add(item);
			}
		}
		
		return recommendedItems;
	}
	
	public void printRecommendation() {
		System.out.println("Recommendation: ");
		for (String user : data.getListUsers()) {
			List<String> recommendedItems = recommend(user, true);
			if (uuCF) {
				System.out.println("\tRecommend item(s): " + recommendedItems + " to user " + user);
			} else {
				System.out.println("\tRecommend item " + user + " to user(s) " + recommendedItems);
			}
		}
	}
	
	public void printRecommendation(String user) {
		System.out.println("Recommendation: ");
		List<String> recommendedItems = recommend(user, true);
		if (uuCF) {
			System.out.println("\tRecommend item(s): " + recommendedItems + " to user " + user);
		} else {
			System.out.println("\tRecommend item " + user + " to user(s) " + recommendedItems);
		}
	}

}
