import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class Rebuild extends JFrame implements ActionListener {

    Rebuild(String title){
        super(title);
    }
    @Override
    public void actionPerformed(ActionEvent e) {
            Window.mypath=new String[100];
            Window.pathnum = 0;
            Window.check.clear();
            Window.destinationfile.clear();
        try {
            IndexManager.deleteAllDocument();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        Window.list2.clearSelection();
            Window.list1.clearSelection();
            Window.list1.setListData(Window.listtitle1);
            Window.list2.setListData(Window.listtitle2);
            Window.ta.setText("ª∂”≠ π”√ docsearcher £°\n«Î…Ë÷√À—À˜∑∂Œß ≤¢ ‰»ÎÀ—À˜ƒ⁄»›");
    }
}
