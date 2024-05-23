package me.flyray.bsin.server.document.parser;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

/**
 * @author leonard
 * @description
 * @createDate 2024/02/2024/2/4 /00/27
 */
public class JsonDocumentParser implements DocumentParser {

  private final Charset charset;

  public JsonDocumentParser() {
    this(UTF_8);
  }

  public JsonDocumentParser(Charset charset) {
    this.charset = ensureNotNull(charset, "charset");
  }

  @Override
  public Document parse(InputStream inputStream) {
    return null;
  }
}
