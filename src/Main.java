import java.util.Map;
import java.util.Map.Entry;

public class Main {

	public static void main(String[] args) {
		Matrix data = new Matrix();
		String filePath = "amazon-meta.txt";
		ReadFile readFile = new ReadFile(filePath, data);
		readFile.readFile();
		System.out.println(readFile.totalReview);

		
//		data.add("0", "0", 5);
//		data.add("0", "1", 4);
//		data.add("0", "3", 2);
//		data.add("0", "4", 2);
//		data.add("1", "0", 5);
//		data.add("1", "2", 4);
//		data.add("1", "3", 2);
//		data.add("1", "4", 0);
//		data.add("2", "0", 2);
//		data.add("2", "2", 1);
//		data.add("2", "3", 3);
//		data.add("2", "4", 4);
//		data.add("3", "0", 0);
//		data.add("3", "1", 0);
//		data.add("3", "3", 4);
//		data.add("4", "0", 1);
//		data.add("4", "3", 4);
//		data.add("5", "1", 2);
//		data.add("5", "2", 1);
//		data.add("6", "2", 1);
//		data.add("6", "3", 4);
//		data.add("6", "4", 5);
		CF cf = new CF(data, 2, (vector1, vector2) -> {
			double dotProduct = 0.0;
			double norm1 = 0.0;
			double norm2 = 0.0;
			Map<String, Double> v1, v2;
			if (vector1.size() < vector2.size()) {
				v1 = vector1;
				v2 = vector2;
			} else {
				v1 = vector2;
				v2 = vector1;
			}
			for (Entry<String, Double> entryv1 : v1.entrySet()) {
				if (v2.containsKey(entryv1.getKey()))
					dotProduct += entryv1.getValue() * v2.get(entryv1.getKey());
				
				norm1 += Math.pow(entryv1.getValue(), 2);
			}
			
			if (dotProduct == 0 || Double.isNaN(dotProduct) || norm1 == 0 || Double.isNaN(norm1))
				return 0;
			
			for (Entry<String, Double> entryv2 : v2.entrySet()) {
				norm2 += Math.pow(entryv2.getValue(), 2);
			}
			return dotProduct / (Math.sqrt(norm1 * norm2));
		});
		cf.normalize();

		long time = System.currentTimeMillis();
		//cf.printRecommendation("A1LO05OQR2UAG2");
		cf.printRecommendation();
		System.out.print(System.currentTimeMillis() - time);

	}

}
