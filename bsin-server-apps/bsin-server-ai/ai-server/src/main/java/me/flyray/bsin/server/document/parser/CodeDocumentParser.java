package me.flyray.bsin.server.document.parser;

import static dev.langchain4j.internal.ValidationUtils.ensureNotNull;
import static java.nio.charset.StandardCharsets.UTF_8;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

/**
 * @author leonard
 * @description
 * @createDate 2024/02/2024/2/4 /00/27
 */
public class CodeDocumentParser implements DocumentParser {

  private final Charset charset;

  public CodeDocumentParser() {
    this(UTF_8);
  }

  public CodeDocumentParser(Charset charset) {
    this.charset = ensureNotNull(charset, "charset");
  }

  @Override
  public Document parse(InputStream inputStream) {

    StringBuffer stringBuffer = new StringBuffer();
    try (InputStreamReader reader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(reader)) {
      String line;
      while ((line = bufferedReader.readLine()) != null) {
        stringBuffer.append(line).append("\n");
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return Document.from(stringBuffer.toString());
  }
}
