import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;

public class Searchdoc implements ActionListener {
    //public void delete
    private IKAnalyzer analyzer;

    private IKAnalyzer getAnalyzer(){
        if(analyzer == null){
            return new IKAnalyzer();
        }else{
            return analyzer;
        }
    }

    public void searchIndex() throws Exception {
        //Class.forName("org.apache.lucene.index.")
        Analyzer analyzer=getAnalyzer();
        Directory directory = FSDirectory.open(new File("C:\\temp\\index").toPath());
        IndexReader indexReader = DirectoryReader.open(directory);
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        Query query = new TermQuery(new Term("content", Window.t.getText()));
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<font color='red'>", "</font>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
        TopDocs topDocs = indexSearcher.search(query, 100);
        ScoreDoc[] scoreDocs=topDocs.scoreDocs;
        DefaultListModel dlm=new DefaultListModel();
        for (ScoreDoc doc:scoreDocs){
            int docid=doc.doc;
            Document document=indexSearcher.doc(docid);

            dlm.addElement(document.get("name"));
            TokenStream tokenStream = analyzer.tokenStream("content", new StringReader(document.get("content")));
            String content = highlighter.getBestFragment(tokenStream, document.get("content"));
            Window.name_to_path.put(document.get("name"),document.get("path"));
            Window.path_to_content.put(document.get("name"),content);
        }
        Window.list1.setModel(dlm);
        Window.list1.setVisible(true);
        //Window.list1.
    }

    @Override
    public void actionPerformed(ActionEvent e){
        Window.path_to_content.clear();
        Window.name_to_path.clear();
        Window.list1.clearSelection();
        try {
            searchIndex();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
