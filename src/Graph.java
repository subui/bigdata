import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.util.Pair;

public class Graph implements Cloneable {

	//				  user	 ,				   item	  , rating
	private final Map<Integer, LinkedList<Pair<Integer, Byte>>> adjacencyList;
	private Set<Integer> users;
	private Set<Integer> items;
	
	public Graph() {
		adjacencyList = new HashMap<Integer, LinkedList<Pair<Integer, Byte>>>();
		users = new HashSet<Integer>();
		items = new HashSet<Integer>();
	}
	
	public int userCount() {
		return users.size();
	}
	
	public List<Integer> getListUsers() {
		return new ArrayList<Integer>(users);
	}
	
	public int itemCount() {
		return items.size();
	}
	
	public List<Integer> getListItems() {
		return new ArrayList<Integer>(items);
	}
	
	public LinkedList<Pair<Integer, Byte>> getItemsRatedByUser(int user) {
		return adjacencyList.get(user);
	}
	
	public void addEdge(int user, int item, byte rating) {
		if (!adjacencyList.containsKey(user)) {
			adjacencyList.put(user, new LinkedList<Pair<Integer, Byte>>());
		}
		
		LinkedList<Pair<Integer, Byte>> neighbor = adjacencyList.get(user);
		neighbor.add(new Pair<Integer, Byte>(item, rating));
		
		users.add(user);
		items.add(item);
	}
	
	protected Graph clone() throws CloneNotSupportedException {
		return (Graph) super.clone();
	}
	
}
