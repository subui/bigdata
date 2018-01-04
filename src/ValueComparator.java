import java.util.Comparator;
import java.util.Map;

public class ValueComparator implements Comparator<Integer> {
	
	Map<Integer, Double> base;
	
	public ValueComparator(Map<Integer, Double> base) {
		this.base = base;
	}

	@Override
	public int compare(Integer o1, Integer o2) {
		if (base.get(o1) <= base.get(o2)) {
			return 1;
		} else {
			return -1;
		}
	}

}
