import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

public class ReadFile {

	private String strFilePath;
	private Matrix data;
	public int totalReview = 0;
	private String user = "";
	private String item = "";
	private double rating = 0;
	
	public ReadFile(String strFilePath, Matrix data){
		this.strFilePath = strFilePath;
		this.data = data;
	}
	
	// read data
	public void readFile(){
		Stream inputStream = null;

		try {
			inputStream = Files.lines(Paths.get(strFilePath), StandardCharsets.UTF_8);
            inputStream.filter(	x ->	x.toString().contains("Id:") 
            						||	x.toString().contains("cutomer:")
            			)
            			.forEach(x -> getData(x.toString()));
        }catch(IOException e){
			e.printStackTrace();
		}
		inputStream.close();
	}
	
	private void getData(String str){
		if(str.contains("Id:")){
			item = str.split(" +")[1];
		}
		else if(str.contains("cutomer:")){
			user = str.split(" +")[3];
			rating = Double.parseDouble(str.split(" +")[5]);
			System.out.print("User: " + user + " Item: " + item + " Rating: " + rating + "\n");
			// gioi han du lieu
			if(totalReview <= 50)
				data.add(user, item, rating);
			totalReview++;
		}
	}
}
