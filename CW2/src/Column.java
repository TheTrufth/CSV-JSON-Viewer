import java.util.ArrayList;

public class Column {
    private String colName;
    private ArrayList<String> colRows = new ArrayList<>();

    public Column(String colName){
        this.colName = colName;
    }

    public String getName(){
        return colName;
    }

    public int getSize(){
        return colRows.size();
    }

    public String getRowValue(int rowNumber){
        return colRows.get(rowNumber);
    }

    public void setRowValue(int rowNumber, String value){
        colRows.set(rowNumber, value);
    }

    public void addRowValue(String rowValue){
        colRows.add(rowValue);
    }

}

