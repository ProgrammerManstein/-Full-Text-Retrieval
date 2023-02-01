import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.io.*;
import java.util.*;
public class Window extends JFrame implements ActionListener{
    public static HashMap<String,String> path_to_content=new HashMap<>();
    public static HashMap<String,String> name_to_path=new HashMap<>();
    static JMenuBar mb = new JMenuBar();//菜单栏
    static JToolBar mtb=new JToolBar();
    static JPanel panel1=new JPanel();
    static JPanel panel12=new JPanel();
    static JSplitPane mainpanel=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,panel1,panel12);
    static JTextPane ta=new JTextPane();
    public static JList list1=new JList();
    public static JList list2=new JList();
    JCheckBox []ctls={
            new JCheckBox("txt"),
            new JCheckBox("PDF"),
            new JCheckBox("Word"),
            new JCheckBox("Excel"),
            new JCheckBox("PowerPoint"),
    };
    static FgButton[] btn={new FgButton(new ImageIcon(ImageScaling.zoom("lucene-first\\image\\open.png",0.1)), "打开文件"),new FgButton(new ImageIcon (ImageScaling.zoom("lucene-first\\image\\new.png",0.1)), "重建范围") };
    FgButton btn1=new FgButton("搜索内容");
    public static JTextField t=new JTextField(20);
    //过滤相关变量
    static String[] listtitle2={"文件目录："};
    static String[] listtitle1={"文件名称："};
    static String[] a={"txt","pdf","docx","xlsx","pptx"};
    static int[] ifa={1,1,1,1,1};
    static String[] mypath=new String[100];
    static int pathnum=0;
    static Set check=new HashSet();
    static ArrayList<File> destinationfile=new ArrayList<File>();

    Window(String sTitle){
        super(sTitle);
//③：设置窗口大小
        setSize(800, 600);
//左部分

        panel1.setLayout(new GridLayout(2,1,5,5));
//左上部分标题
        JPanel panel2=new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel2.add(new JLabel("文档类型"));
//左上显示部分
        JPanel panel3=new JPanel();
        panel3.setLayout(new GridLayout(5,1,5,5));

        for(int i=0;i<5;i++){
            panel3.add(ctls[i]);
            ctls[i].setSelected(true);
        }
//左下标题部分
        JPanel panel7=new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel7.add(new JLabel("搜索范围"));
//左下显示部分
        JScrollPane panel8=new JScrollPane(list2);
//左上部分
        JPanel panel4=new JPanel();
        panel4.setLayout(new BorderLayout());
        panel4.add(panel2,"North");
        panel4.add(panel3,"Center");
//左下部分
        JPanel panel6=new JPanel();
        panel6.setLayout(new BorderLayout());
        panel6.add(panel7,"North");
        //设置工具条
        panel6.add(BorderLayout.SOUTH, mtb);
        mtb.setLayout(new FlowLayout(FlowLayout.LEFT));
        for(int i=0;i<2;i++){
            btn[i].setBorder(BorderFactory.createEmptyBorder());
            mtb.add(btn[i]);
        }
//设置不可浮动
        mtb.setFloatable(false);
        panel6.add(panel8,"Center");

        panel1.add(panel4);
        panel1.add(panel6);
//右上部分
        JPanel panel9=new JPanel();
        panel9.setLayout(new BorderLayout());
//右上搜索部分
        JPanel panel11=new JPanel();
        panel11.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel11.add(new JLabel("搜索："));

        panel11.add(t);
        panel11.add(btn1);
//右上显示部分
        JScrollPane scrollPane=new JScrollPane(list1);
        panel9.add(panel11,"North");
        panel9.add(scrollPane,"Center");
        list1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (list1.getSelectedIndex()!=-1){
                    if (e.getClickCount()==1) oneClick(list1.getSelectedValue());
                    if (e.getClickCount()==2) twoClick(list1.getSelectedValue());
                }
            }
        });

    //右下部分
        JPanel panel10=new JPanel();
        panel10.setLayout(new BorderLayout());
        JScrollPane spanel2=new JScrollPane(ta);
        panel10.add(spanel2,"Center");
//右部分
        panel12.setLayout(new GridLayout(2,1,5,5));
        panel12.setSize(300,600);
        panel12.add(panel9);
        panel12.add(panel10);

        mainpanel.setDividerSize(5);
        mainpanel.setDividerLocation(0.3);
//添加监视器
        ctls[0].addActionListener(this);
        ctls[1].addActionListener(this);
        ctls[2].addActionListener(this);
        ctls[3].addActionListener(this);
        ctls[4].addActionListener(this);
        btn[0].addActionListener(this);
        btn[1].addActionListener(new Rebuild("重选范围"));
        setContentPane(mainpanel);
        btn1.addActionListener(new Searchdoc());
//使窗口在显示屏居中显示
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        centerWindow();
    }
    protected void oneClick(Object value){
        ta.setContentType("text/html");
        ta.setText(path_to_content.get(list1.getSelectedValue()));
    }
    protected void twoClick(Object value){
        final Runtime runtime = Runtime.getRuntime();
        Process process = null;//
        final String cmd = "rundll32 url.dll FileProtocolHandler file://"+name_to_path.get(list1.getSelectedValue());
        System.out.println(cmd);
        try {
            process = runtime.exec(cmd);
        } catch (final Exception e) {
            System.out.println("Error exec!");
        }
    }
    public void centerWindow(){
//获得显示屏桌面窗口的大小
        Toolkit tk=getToolkit();
        Dimension dm=tk.getScreenSize();
//让窗口居中显示
        setLocation((int)(dm.getWidth()-getWidth())/2,(int)(dm.getHeight()-getHeight())/2);
    }
    public static void createIndex()throws Exception{
        Directory directory= FSDirectory.open(new File("C:\\temp\\index").toPath());
        IndexWriterConfig config=new IndexWriterConfig(new IKAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter indexWriter=new IndexWriter(directory,config);
        indexWriter.close();
    }
    public static void main(String args[]) throws Exception {
        list1.setListData(listtitle2);
        list2.setListData(listtitle1);
        createIndex();
        Window searcher=new Window("docsearcher");
        ta.setText("欢迎使用 docsearcher ！\n请设置搜索范围 并输入搜索内容");
        searcher.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                try {
                    IndexManager.deleteAllDocument();
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
        searcher.setVisible(true);
        mainpanel.setDividerLocation(0.3);
        //文件过滤部分

    }
    @Override
    public void actionPerformed(ActionEvent e){
        //选择过滤后缀
        destinationfile.clear();
        try {
            IndexManager.deleteAllDocument();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        for(int i=0;i<5;i++){
            if(ctls[i].isSelected()){
                ifa[i]=1;
            }
            else
                ifa[i]=0;
        }
        //选择过滤范围
        if(e.getSource()==btn[0]){
            JFileChooser fc=new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//只能选择目录
            File f=null;
            try{
                fc.showOpenDialog(null);
            }
            catch(HeadlessException head){
                System.out.println("Open File Dialog ERROR!");
            }
            f=fc.getSelectedFile();//获取选择到的文件信息

            if(!check.contains(f.getAbsolutePath()))
            {
                pathnum++;
                mypath[pathnum]=f.getAbsolutePath();
                check.add(f.getAbsolutePath());
                list2.setListData(mypath);
            }
        }
        //开始过滤
        for(int i=1;i<=pathnum;i++){
            File path = new File(mypath[i]);

            String[] listcontent;
            listcontent = path.list(new DirFilter(""));
            File[] xx=path.listFiles(new DirFilter(""));
            for(int j=0;j<xx.length;j++){
                destinationfile.add(xx[j]);
                try {
                    IndexManager.addDocument(xx[j]);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            Arrays.sort(listcontent, new AlphabeticComparator());
        }
        for(int j=0;j<destinationfile.size();j++)
        {
            File temp= (File) destinationfile.get(j);
            System.out.println(temp.getAbsolutePath());
        }
    }
}
