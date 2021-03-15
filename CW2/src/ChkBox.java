import javax.swing.*;

public class ChkBox {
    private int index;
    private char c;
    private String chkboxName;
    private JCheckBox chkbox;

    public ChkBox(String chkboxName, char c, int index, JCheckBox chkbox){
        this.chkboxName = chkboxName;
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

    public String getChkboxName() {
        return chkboxName;
    }
}

