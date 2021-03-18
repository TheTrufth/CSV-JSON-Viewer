import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/* File name: "/Users/student/Documents/Java/COMP004/CW2/src/patients100.csv" */
public class Model {
    private DataLoader myDataLoader;
    private JSONReader jsonReader;
    private DataFrame myDF;
    private HashMap<Character, String> optionMapper;
    private String fullOptionString;

    public Model(String fileName, String type){
        if (type.equals("CSV")){
            this.myDataLoader = new DataLoader(fileName);
            this.myDF = myDataLoader.getMyDF();
        }
        if (type.equals("JSON")){
            this.jsonReader = new JSONReader(fileName);
            this.myDF = jsonReader.getMyDF();
        }

        this.optionMapper = new HashMap<>();
        String[] colNames = getColNames();
        char[] alphabet = "abcdefghijklmnopqrstuvwxyz0123456789!@£$%^&*(),./;'\\[]<>?:|{}`~".toCharArray();
        int i = 0;
        StringBuilder originalOptionString = new StringBuilder();
        for (String colName: colNames) {
            optionMapper.put(alphabet[i], colName);
            originalOptionString.append(alphabet[i]);
            i++;
        }
        this.fullOptionString = originalOptionString.toString();
    }
    
    public String[] getColNames(){
        ArrayList<String> originalColName = myDF.getColumnNames();
        String[] colNames = new String[(originalColName.size())];
        int i = 0;
        for (String s: originalColName){
            colNames[i] = s;
            i++;
        }
        return colNames;
    }

    public HashMap<Character, String> getOptionMapper(){
        return this.optionMapper;
    }
    public String getFullOptionString(){
        return fullOptionString;
    }
    public String[] getTableHeadings(String choices){
        List<String> headingsList = new ArrayList<String>();
        for(int i=0, n=choices.length(); i <n; i++){
            char c = choices.charAt(i);
            if (c != '-'){
                headingsList.add(optionMapper.get(c));
            }
        }
        String[] headings = new String[headingsList.size()];
        headings = headingsList.toArray(headings);
        return headings;
    }

    public String[][] getData(String choices){
        String[] colNames = getTableHeadings(choices);
        int numOfRows = myDF.getRowCount(myDF.getColByColName(colNames[0]));
        String[][] myData = new String[numOfRows][colNames.length];
        int z = 0;
        for (String s: colNames){
            for (int i=0; i < numOfRows; i++) {
                myData[i][z] = myDF.getValue(s, i);
            }
            z++;
        }

       return myData;
    }

    public String[] getAges(){
        LocalDate now;
        Column x = myDF.getColByColName("BIRTHDATE");
        Column y = myDF.getColByColName("DEATHDATE");
        int numberOfRows = x.getSize();
        String[] ages = new String[numberOfRows];
        String[] t;
        for (int i=0; i<numberOfRows; i++){
            t = x.getRowValue(i).split("-");
            LocalDate birthDate = LocalDate.of(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
            if (y.getRowValue(i).equals("")){
                now = LocalDate.now();
            }
            else{
                t = y.getRowValue(i).split("-");
                now = LocalDate.of(Integer.parseInt(t[0]), Integer.parseInt(t[1]), Integer.parseInt(t[2]));
            }
            long years = ChronoUnit.YEARS.between(birthDate, now);
            ages[i] = String.valueOf(years);
        }
        return ages;
    }

    public void writeToFile(String filename, String optionString, String[][] currentTableData){
        JSONWriter s = new JSONWriter(this, optionString, currentTableData);
        s.writeToFile(filename);
    }

    public static void main(String[] args) {
        Model myModel = new Model("/Users/student/Documents/Java/COMP004/CW2/src/CSVFiles/patients100.csv", "CSV");
        myModel.getAges();
    }
}
