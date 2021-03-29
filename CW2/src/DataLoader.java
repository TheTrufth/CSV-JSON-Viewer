import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;


public class DataLoader {
    private final DataFrame myDF;
    public DataLoader(String fileName){
       String line;
       String splitBy = ",";
       DataFrame dataFrame = new DataFrame();
       try {
           ArrayList<String[]> data = new ArrayList<>();
           BufferedReader br = new BufferedReader(new FileReader(fileName));
           while ((line = br.readLine()) != null){
               data.add(line.split(splitBy));
           }
           // Use Arrays.toString(String[] args) to print out array contents
           String[] x = data.get(0);

           // Parse the arrays
           int k = 0;
           for (String s : x) {
               // Col name (ID, BIRTHDATE, DEATHDATE, ...)
               Column col = new Column(s);
               for (int j = 1; j < data.size(); j++) {
                   try {
                       col.addRowValue(data.get(j)[k]);
                   } catch (ArrayIndexOutOfBoundsException e) {
                       col.addRowValue("");
                   }
               }
               k++;
               dataFrame.addColumn(col);
           }
       }
       catch (FileNotFoundException e){
           System.out.println("CSV file not found.");
       }
       catch (IOException e){
           e.printStackTrace();
       }
       this.myDF = dataFrame;
   }

   public DataFrame getMyDF(){
        return this.myDF;
   }

}
