package me.flyray.bsin.server.document.parser;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @author leonard
 * @description
 * @createDate 2024/02/2024/2/4 /00/27
 */
public class PdfDocumentParser implements DocumentParser {

  private final Charset charset;

  public PdfDocumentParser() {
    this(UTF_8);
  }

  public PdfDocumentParser(Charset charset) {
    this.charset = ensureNotNull(charset, "charset");
  }

  @Override
  public Document parse(InputStream inputStream) {
    PDDocument document = null;
    try {
      document = PDDocument.load(inputStream);
      PDFTextStripper textStripper = new PDFTextStripper();
      String content = textStripper.getText(document);
      return Document.from(content);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
