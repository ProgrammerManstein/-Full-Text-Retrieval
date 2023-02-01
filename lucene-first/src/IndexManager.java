import org.apache.commons.io.FileUtils;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StoredField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;

public class IndexManager {
    private static IndexWriter indexWriter;
    public static void init()throws Exception{
        indexWriter=new IndexWriter(FSDirectory.open(new File("C:\\temp\\index").toPath()),new IndexWriterConfig(new IKAnalyzer()));
    }
    public static void addDocument(File f)throws Exception{
        Directory directory= FSDirectory.open(new File("C:\\temp\\index").toPath());
        IndexWriterConfig config=new IndexWriterConfig(new IKAnalyzer());
        config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter indexWriter=new IndexWriter(directory,config);
        Document document =new Document();
        String filename=f.getName();
        String filepath=f.getPath();
        String fileContent = new TikaExtractor().extractTextOfDocument(f);
        long filesize=FileUtils.sizeOf(f);
        Field fieldName=new TextField("name",filename, Field.Store.YES);
        Field fieldPath=new TextField("path",filepath,Field.Store.YES);
        Field fieldContent=new TextField("content",fileContent,Field.Store.YES);
        Field fieldsize=new TextField("size",filesize+"",Field.Store.YES);
        document.add(fieldName);
        document.add(fieldPath);
        document.add(fieldContent);
        document.add(fieldsize);
        indexWriter.addDocument(document);
        indexWriter.close();
    }
    public static void deleteAllDocument()throws  Exception{
        init();
        indexWriter.deleteAll();
        indexWriter.close();
    }
}
