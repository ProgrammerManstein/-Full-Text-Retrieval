import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

class DirFilter implements FilenameFilter
{
    private Pattern pattern[]=new Pattern[5];


    public DirFilter(String regex)
    {
        pattern[0] = Pattern.compile(Window.a[0]);
        pattern[1] = Pattern.compile(Window.a[1]);
        pattern[2] = Pattern.compile(Window.a[2]);
        pattern[3] = Pattern.compile(Window.a[3]);
        pattern[4] = Pattern.compile(Window.a[4]);
    }

    public boolean accept(File dir, String name)
    {
        boolean y=false;
        String nameString = new File(name).getName();
        String postfix = nameString.substring(nameString.lastIndexOf(".")+1);
        for(int i=0;i<5;i++){
            if(Window.ifa[i]==1)
                y=y||pattern[i].matcher(postfix).matches();
        }
        return y;
    }
} 