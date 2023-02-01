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
    static JMenuBar mb = new JMenuBar();//�˵���
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
    static FgButton[] btn={new FgButton(new ImageIcon(ImageScaling.zoom("lucene-first\\image\\open.png",0.1)), "���ļ�"),new FgButton(new ImageIcon (ImageScaling.zoom("lucene-first\\image\\new.png",0.1)), "�ؽ���Χ") };
    FgButton btn1=new FgButton("��������");
    public static JTextField t=new JTextField(20);
    //������ر���
    static String[] listtitle2={"�ļ�Ŀ¼��"};
    static String[] listtitle1={"�ļ����ƣ�"};
    static String[] a={"txt","pdf","docx","xlsx","pptx"};
    static int[] ifa={1,1,1,1,1};
    static String[] mypath=new String[100];
    static int pathnum=0;
    static Set check=new HashSet();
    static ArrayList<File> destinationfile=new ArrayList<File>();

    Window(String sTitle){
        super(sTitle);
//�ۣ����ô��ڴ�С
        setSize(800, 600);
//�󲿷�

        panel1.setLayout(new GridLayout(2,1,5,5));
//���ϲ��ֱ���
        JPanel panel2=new JPanel();
        panel2.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel2.add(new JLabel("�ĵ�����"));
//������ʾ����
        JPanel panel3=new JPanel();
        panel3.setLayout(new GridLayout(5,1,5,5));

        for(int i=0;i<5;i++){
            panel3.add(ctls[i]);
            ctls[i].setSelected(true);
        }
//���±��ⲿ��
        JPanel panel7=new JPanel();
        panel7.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel7.add(new JLabel("������Χ"));
//������ʾ����
        JScrollPane panel8=new JScrollPane(list2);
//���ϲ���
        JPanel panel4=new JPanel();
        panel4.setLayout(new BorderLayout());
        panel4.add(panel2,"North");
        panel4.add(panel3,"Center");
//���²���
        JPanel panel6=new JPanel();
        panel6.setLayout(new BorderLayout());
        panel6.add(panel7,"North");
        //���ù�����
        panel6.add(BorderLayout.SOUTH, mtb);
        mtb.setLayout(new FlowLayout(FlowLayout.LEFT));
        for(int i=0;i<2;i++){
            btn[i].setBorder(BorderFactory.createEmptyBorder());
            mtb.add(btn[i]);
        }
//���ò��ɸ���
        mtb.setFloatable(false);
        panel6.add(panel8,"Center");

        panel1.add(panel4);
        panel1.add(panel6);
//���ϲ���
        JPanel panel9=new JPanel();
        panel9.setLayout(new BorderLayout());
//������������
        JPanel panel11=new JPanel();
        panel11.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel11.add(new JLabel("������"));

        panel11.add(t);
        panel11.add(btn1);
//������ʾ����
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

    //���²���
        JPanel panel10=new JPanel();
        panel10.setLayout(new BorderLayout());
        JScrollPane spanel2=new JScrollPane(ta);
        panel10.add(spanel2,"Center");
//�Ҳ���
        panel12.setLayout(new GridLayout(2,1,5,5));
        panel12.setSize(300,600);
        panel12.add(panel9);
        panel12.add(panel10);

        mainpanel.setDividerSize(5);
        mainpanel.setDividerLocation(0.3);
//��Ӽ�����
        ctls[0].addActionListener(this);
        ctls[1].addActionListener(this);
        ctls[2].addActionListener(this);
        ctls[3].addActionListener(this);
        ctls[4].addActionListener(this);
        btn[0].addActionListener(this);
        btn[1].addActionListener(new Rebuild("��ѡ��Χ"));
        setContentPane(mainpanel);
        btn1.addActionListener(new Searchdoc());
//ʹ��������ʾ��������ʾ
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
//�����ʾ�����洰�ڵĴ�С
        Toolkit tk=getToolkit();
        Dimension dm=tk.getScreenSize();
//�ô��ھ�����ʾ
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
        ta.setText("��ӭʹ�� docsearcher ��\n������������Χ ��������������");
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
        //�ļ����˲���

    }
    @Override
    public void actionPerformed(ActionEvent e){
        //ѡ����˺�׺
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
        //ѡ����˷�Χ
        if(e.getSource()==btn[0]){
            JFileChooser fc=new JFileChooser();
            fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);//ֻ��ѡ��Ŀ¼
            File f=null;
            try{
                fc.showOpenDialog(null);
            }
            catch(HeadlessException head){
                System.out.println("Open File Dialog ERROR!");
            }
            f=fc.getSelectedFile();//��ȡѡ�񵽵��ļ���Ϣ

            if(!check.contains(f.getAbsolutePath()))
            {
                pathnum++;
                mypath[pathnum]=f.getAbsolutePath();
                check.add(f.getAbsolutePath());
                list2.setListData(mypath);
            }
        }
        //��ʼ����
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
