import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.ocr.TesseractOCRConfig;
import org.apache.tika.parser.pdf.PDFParserConfig;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class TikaExtractor{
public String extractTextOfDocument(File file) throws Exception {
	InputStream fileStream = new FileInputStream(file);
	Parser parser = new AutoDetectParser();
	Metadata metadata = new Metadata();
	BodyContentHandler handler = new BodyContentHandler(Integer.MAX_VALUE);

	TesseractOCRConfig config = new TesseractOCRConfig();
	PDFParserConfig pdfConfig = new PDFParserConfig();
	pdfConfig.setExtractInlineImages(true);

	// To parse images in files those lines are needed
	ParseContext parseContext = new ParseContext();
	parseContext.set(TesseractOCRConfig.class, config);
	parseContext.set(PDFParserConfig.class, pdfConfig);
	parseContext.set(Parser.class, parser); // need to add this to make sure
											// recursive parsing happens!
	try {
		parser.parse(fileStream, handler, metadata, parseContext);

		return handler.toString();
	} catch (IOException | SAXException | TikaException e) {
		e.printStackTrace();
	} finally {
		try {
			fileStream.close();
		} catch (IOException e) {
			throw new Exception(e);
		}
	}
	return null;
}
}