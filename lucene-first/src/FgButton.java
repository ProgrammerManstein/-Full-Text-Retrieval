import javax.swing.*;

class FgButton extends JButton{
    public FgButton(){
        super();
    }
    public FgButton(Icon icon){
        super(icon);
    }
    public FgButton(Icon icon, String strToolTipText){
        super(icon);
        setToolTipText(strToolTipText);
    }
    public FgButton(String text){
        super(text);
    }
    public FgButton(String text, Icon icon, String strToolTipText){
        super(text, icon);
        setToolTipText(strToolTipText);
    }
}
