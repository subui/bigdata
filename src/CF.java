import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class CF {

	private final boolean uuCF; // user-user (true) or item-item (false) CF
	private final Matrix data;
	private final int k; // number of neighbor points
	private final DistanceFunction distFunc;
	private Map<String, Byte> mu;

	public CF(Matrix data, int k, DistanceFunction distFunc, boolean uuCF) {
		this.data = data;
		this.k = k;
		this.distFunc = distFunc;
		this.uuCF = uuCF;
	}

	public CF(Matrix data, int k, DistanceFunction distFunc) {
		this(data, k, distFunc, true);
	}

	public void normalize() {
		System.out.println("Normalizing...");
		List<String> users = data.getListUsers();
		//mu = data.getListUsers().stream().collect(Collectors.toMap(x -> x, x -> (byte) 0));

		int i = 0;
		for (String user : users) {
			Map<String, Double> items = data.getItemsRatedByUser(user);

			double average = items.values().stream().mapToDouble(x -> x).average().getAsDouble();

			// normalize
			items.entrySet().forEach(x -> data.updateRating(user, x.getKey(), x.getValue() - average));
			System.out.println("" + (++i) + "/" + users.size());
		}
	}

	private double _pred(String user, String item, boolean normalized) {
		Map<String, Double> users = data.getUsersRatedItem(item);
		// similarity between user and others
		// key: user
		// value: similarity
		Map<String, Double> similarity = new HashMap<>();
		double[] vector1 = data.getVectorRatingByUser(user);
		
		for (String u : data.getListUsers()) {
			double[] vector2 = data.getVectorRatingByUser(u);
			similarity.put(u, distFunc.calculate(vector1, vector2));
		}
		
		Map<String, Double> sortedSimilarity = new TreeMap<String, Double>(new ValueComparator(similarity));
		for (Map.Entry<String, Double> entry : similarity.entrySet()) {
			if (users.containsKey(entry.getKey())) {
				sortedSimilarity.put(entry.getKey(), entry.getValue());
			}
		}
		
		double sum1 = 0;
		double sum2 = 1e-8;
		int i = 0;
		for (Map.Entry<String, Double> entry : sortedSimilarity.entrySet()) {
			if (i++ >= k) break;
			sum1 += entry.getValue() * users.get(entry.getKey());
			sum2 += Math.abs(entry.getValue());
		}

		return sum1 / sum2 + (normalized ? 0 : mu.get(user));
	}
	
	private double pred(String user, String item, boolean normalized) {
		return uuCF
				? _pred(user, item, normalized)
				: _pred(item, user, normalized);
	}
	
	private List<String> recommend(String user, boolean normalized) {
		Map<String, Double> items = data.getItemsRatedByUser(user);
		List<String> recommendedItems = new ArrayList<>();
		for (String item : data.getListItems()) {
			if (items.containsKey(item)) continue;
			double rating = pred(user, item, true);
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

}
