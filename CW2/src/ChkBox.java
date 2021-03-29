import javax.swing.*;

/*
    This class contains the necessary details needed for distinguish each checkbox
    char c - represents the checkboxID which used to show/hide the fields in the JTable.
*/
public class ChkBox {
    private final int index;
    private final char c;
    private final JCheckBox chkbox;

    public ChkBox(char c, int index, JCheckBox chkbox){
        this.c = c;
        this.index = index;
        this.chkbox = chkbox;
    }

    public char getC() {
        return c;
    }

    public int getIndex() {
        return index;
    }

    public JCheckBox getChkbox() {
        return chkbox;
    }

}

