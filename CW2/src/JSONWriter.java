import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class JSONWriter {
    private final Model myModel;
    private String[] colHeadings;
    private String[][] data;
    private StringBuilder jsonString;

    public JSONWriter(Model m, String optionString, String[][] currentTableData){
        this.myModel = m;
        this.colHeadings = myModel.getTableHeadings(optionString);
        this.data = currentTableData;
        this.jsonString = new StringBuilder(("["));
        test();
    }

    public StringBuilder getJsonString() {
        return jsonString;
    }

    public void writeToFile(String filename){
        try {
            FileWriter fw = new FileWriter(filename);
            BufferedWriter bw = new BufferedWriter(fw, 1000000);
            bw.write(jsonString.toString());
            bw.close();
        } catch (IOException ignored) {

        }
    }
    // data[row][colName]
    private void test() {
        for (int i = 0; i < data.length; i++){
            jsonString.append("\n {");
            for (int j = 0; j < colHeadings.length; j++){
                if (j != colHeadings.length - 1){
                    jsonString.append("\n  \"").append(colHeadings[j]).append("\":\"").append(data[i][j]).append("\",");
                }
                else {
                    jsonString.append("\n  \"").append(colHeadings[j]).append("\":\"").append(data[i][j]).append("\"");
                }
            }
            if (i < data.length - 1){
                jsonString.append("\n },");
            }
            else {
                jsonString.append("\n }");
            }
        }
        jsonString.append("\n]");
    }
}
