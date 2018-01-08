import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

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

		System.out.println("User count: " + data.userCount());
		System.out.println("Item count: " + data.itemCount());
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
		if (vector1.size() == 1) return 0;
		
		for (Entry<String, Double> entry : users.entrySet()) {
			Map<String, Double> vector2 = data.getItemsRatedByUser(entry.getKey());
			similarity.put(entry.getKey(), vector2.size() == 1 ? 0 : distFunc.calculate(vector1, vector2));
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
	
	private List<String> recommend(String user) {
		Map<String, Double> items = data.getItemsRatedByUser(user);
		Set<String> recommendedItems = new TreeSet<>((arg1, arg2) -> Double.parseDouble(arg1) > Double.parseDouble(arg2) ? 1 : -1);

		int nThread = 8;
		Thread[] thread = new Thread[nThread];
		int step = data.itemCount() / nThread;
		for (int i = 0; i < nThread; i++) {
			final int index = i;
			thread[i] = new Thread(() -> {
				for (int j = index * step; j < (index + 1) * step; j++) {
					String item = listItems.get(j);
					if (items.containsKey(item)) continue;
					double rating = pred(user, item);
					if (rating > 0) {
						recommendedItems.add(item);
					}
				}
			});
		}
		
		for (Thread t : thread) {
			t.start();
		}
		
		for (Thread t : thread) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		/*for (String item : listItems) {
			if (items.containsKey(item)) continue;
			double rating = pred(user, item);
			if (rating > 0) {
				recommendedItems.add(item);
			}
		}*/
		
		return new ArrayList<>(recommendedItems);
	}
	
	public void printRecommendation() {
		System.out.println("Recommendation: ");
		int nThread = 8;
		Thread[] thread = new Thread[nThread];
		int step = data.userCount() / nThread;
		for (int i = 0; i < nThread; i++) {
			final int index = i;
			thread[i] = new Thread(() -> {
				for (int j = index * step; j < (index + 1) * step; j++) {
					String user = listUsers.get(j);
					List<String> recommendedItems = recommend(user);
					if (uuCF) {
						System.out.println("\tRecommend item(s): " + recommendedItems + " to user " + user);
					} else {
						System.out.println("\tRecommend item " + user + " to user(s) " + recommendedItems);
					}
				}
			});
		}
		
		for (Thread t : thread) {
			t.start();
		}
		
		for (Thread t : thread) {
			try {
				t.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		/*for (String user : data.getListUsers()) {
			List<String> recommendedItems = recommend(user);
			if (uuCF) {
				System.out.println("\tRecommend item(s): " + recommendedItems + " to user " + user);
			} else {
				System.out.println("\tRecommend item " + user + " to user(s) " + recommendedItems);
			}
		}*/
	}
	
	public void printRecommendation(String user) {
		System.out.println("Recommendation: ");
		List<String> recommendedItems = recommend(user);
		if (uuCF) {
			System.out.println("\tRecommend item(s): " + recommendedItems + " to user " + user);
		} else {
			System.out.println("\tRecommend item " + user + " to user(s) " + recommendedItems);
		}
	}

}
