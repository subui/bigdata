import java.util.Map;

public interface DistanceFunction {

	double calculate(Map<String, Double> vector1, Map<String, Double> vector2);
	
}
