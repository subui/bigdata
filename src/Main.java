
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
		CF cf = new CF(data, 2, new DistanceFunction() {
			
			@Override
			public double calculate(double[] vector1, double[] vector2) {
				double dotProduct = 0.0;
			    double normA = 0.0;
			    double normB = 0.0;
			    for (int i = 0; i < vector1.length; i++) {
			        dotProduct += vector1[i] * vector2[i];
			        normA += Math.pow(vector1[i], 2);
			        normB += Math.pow(vector2[i], 2);
			    }   
			    return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
			}
		});
		
		cf.printRecommendation();

	}

}
