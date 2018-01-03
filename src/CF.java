import java.util.List;
import java.util.Map;
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
			itemsCopy.forEach(x -> itemsCopy.add(new Pair<Integer, Byte>(x.getKey(), (byte) (x.getValue() - mu.get(user)))));
		}
	}

}
