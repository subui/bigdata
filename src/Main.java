
public class Main {

	public static void main(String[] args) {
		Matrix data = new Matrix();
		data.add(0, 0, 5);
		data.add(0, 1, 4);
		data.add(0, 3, 2);
		data.add(0, 4, 2);
		data.add(1, 0, 5);
		data.add(1, 2, 4);
		data.add(1, 3, 2);
		data.add(1, 4, 0);
		data.add(2, 0, 2);
		data.add(2, 2, 1);
		data.add(2, 3, 3);
		data.add(2, 4, 4);
		data.add(3, 0, 0);
		data.add(3, 1, 0);
		data.add(3, 3, 4);
		data.add(4, 0, 1);
		data.add(4, 3, 4);
		data.add(5, 1, 2);
		data.add(5, 2, 1);
		data.add(6, 2, 1);
		data.add(6, 3, 4);
		data.add(6, 4, 5);
		CF cf = new CF(data, 2, (vector1, vector2) -> {
			double dotProduct = 0.0;
			double norm1 = 0.0;
			double norm2 = 0.0;
			for (int i = 0; i < vector1.length; i++) {
				dotProduct += vector1[i] * vector2[i];
				norm1 += Math.pow(vector1[i], 2);
				norm2 += Math.pow(vector2[i], 2);
			}
			return dotProduct / (Math.sqrt(norm1) * Math.sqrt(norm2));
		});
		cf.normalize();

		cf.printRecommendation();

	}

}
