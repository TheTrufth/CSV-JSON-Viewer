import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class JSONReader {
    private DataFrame myDF;

    public JSONReader(String fileName){
        myDF = new DataFrame();
        String[] firstSplit;
        ArrayList<String> colNames = new ArrayList<>();
        try {
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            StringBuilder jsonStringBuilder = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                String l = line.split(":")[0].strip().replaceAll("\"", "");
                if (!(colNames.contains(l))) {
                    colNames.add(l);
                }
                try {
                    jsonStringBuilder.append(line.split(":")[1]);
                } catch (Exception ignored) {
                }
                line = br.readLine();
            }


            colNames.removeAll(Collections.singleton("["));
            colNames.removeAll(Collections.singleton("{"));
            colNames.removeAll(Collections.singleton("},"));
            colNames.removeAll(Collections.singleton("]"));
            colNames.removeAll(Collections.singleton("}"));
            colNames.removeAll(Collections.singleton(""));
            firstSplit = jsonStringBuilder.toString().split(",");

            String[] d = new String[colNames.size()];
            ArrayList<String[]> data = new ArrayList<>();
            int index = 0;
            for (String s : firstSplit) {
                if (index == colNames.size() - 1) {
                    int t = s.indexOf("\"\"");
                    if (t != -1) {
                        d[index] = s.substring(1, t+1).replaceAll("\"", "");
                        data.add(d);
                        d = new String[colNames.size()];
                        index = 0;
                        d[index] = s.substring(t + 1).replaceAll("\"", "");
                    } else {
                        d[index] = s.replaceAll("\"", "");
                        data.add(d);
                    }
                } else {
                    d[index] = s.replaceAll("\"", "");
                }
                index++;
            }

            // Parse the arrays
            int k = 0;
            for (String colName : colNames) {
                // Col name (ID, BIRTHDATE, DEATHDATE, ...)
                // System.out.println(x[i]);
                Column col = new Column(colName);
                for (int j = 0; j < data.size(); j++) {
                    try {
                        //System.out.println(data.get(j)[k]);
                        col.addRowValue(data.get(j)[k]);
                    } catch (ArrayIndexOutOfBoundsException e) {
                        col.addRowValue("");
                    }
                }
                k++;
                myDF.addColumn(col);
            }
        }
        catch (FileNotFoundException e){
            System.out.println("JSON file not found.");
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        JSONReader x = new JSONReader("/Users/student/Documents/Java/COMP004/CW2/src/JSONFiles/p.json");
    }

    public DataFrame getMyDF(){
        return this.myDF;
    }

}
