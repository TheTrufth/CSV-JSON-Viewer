import java.util.ArrayList;

public class DataFrame {
    private ArrayList<Column> columns = new ArrayList<>();

    public void addColumn(Column col){
        columns.add(col);
    }

    public ArrayList<String> getColumnNames(){
        ArrayList<String> colNames = new ArrayList<>();
        for (Column col:columns){
            colNames.add(col.getName());
        }
        return colNames;
    }
    public Column getColByColName(String colName){
        for (Column column:columns){
            if (column.getName().equals(colName)){
                return column;
            }
        }
        return null;
    }
    public int getRowCount(Column col){
        if (col == null){
            return 0;
        }
        return col.getSize();
    }


    public int getColCount(){
        return columns.size();
    }

    public String getValue(String colName, int row){
        Column col = getColByColName(colName);
        return col.getRowValue(row);
    }

    public void putValue(String colName, int row, String val){
        Column col = getColByColName(colName);
        col.setRowValue(row, val);
    }

    public void addValue(String colName, String val){
        Column col = getColByColName(colName);
        col.addRowValue(val);
    }
}
