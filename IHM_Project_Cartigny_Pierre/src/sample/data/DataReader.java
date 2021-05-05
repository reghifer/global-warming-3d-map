package sample.data;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


public class DataReader {
	public static Model getModel(String csvLink) {
		Model returnedModel = null;
		try {
			System.out.println("[Start]loading data");
			FileReader file = new FileReader(csvLink);
			BufferedReader bufRead = new BufferedReader(file);
			String line = bufRead.readLine();
			line = line.replaceAll("\"","");
			String[] array = line.split(",");
			if(!array[0].equals("lat") || !array[1].contentEquals("lon")) {
				System.err.println("wrong csv");
				bufRead.close();
				file.close();
				return null;
			}
			returnedModel = new Model(Integer.parseInt(array[2]),Integer.parseInt(array[array.length - 1]));
			line = bufRead.readLine();
		while ( line != null) {
				line = line.replaceAll("\"","");
			   	array = line.split(",");
			    int currentYear = 1880;
			   	for(int y = 2 ; y < array.length;y++) {
			   		if(!array[y].equals("NA")) {
			   			returnedModel.getYearData(currentYear).addData(Float.parseFloat(array[0]), Float.parseFloat(array[1]), Float.parseFloat(array[y]));
			   		}else {
			   			returnedModel.getYearData(currentYear).addData(Float.parseFloat(array[0]), Float.parseFloat(array[1]), Float.NaN);
			   		}
			   		currentYear++;
			   	}
			    line = bufRead.readLine();
			}

			bufRead.close();
			file.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("[End]loading data");
		return returnedModel;

	}
}
