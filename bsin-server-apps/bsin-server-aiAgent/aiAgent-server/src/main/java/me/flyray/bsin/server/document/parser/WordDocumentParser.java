package me.flyray.bsin.server.document.parser;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @author leonard
 * @description
 * @createDate 2024/02/2024/2/4 /00/27
 */
public class WordDocumentParser implements DocumentParser {

  private final Charset charset;

  public WordDocumentParser() {
    this(UTF_8);
  }

  public WordDocumentParser(Charset charset) {
    this.charset = ensureNotNull(charset, "charset");
  }

  @Override
  public Document parse(InputStream inputStream) {
    XWPFDocument document = null;
    try {
      document = new XWPFDocument(inputStream);
      XWPFWordExtractor extractor = new XWPFWordExtractor(document);
      String content = extractor.getText();
      return Document.from(content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
