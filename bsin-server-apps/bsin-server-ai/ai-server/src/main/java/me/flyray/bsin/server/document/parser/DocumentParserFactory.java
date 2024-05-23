package me.flyray.bsin.server.document.parser;

import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import lombok.AllArgsConstructor;
import me.flyray.bsin.domain.enums.FileType;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;

@AllArgsConstructor
@Component
public class DocumentParserFactory {

  public DocumentParser getDocumentParserByFileType(String fileType) {
    if (FileType.isTextFile(fileType)) {
      return new TextDocumentParser();
    } else if (FileType.isWord(fileType)) {
      return new WordDocumentParser();
    } else if (FileType.isPdf(fileType)) {
      return new PdfDocumentParser();
    } else if (FileType.isMdFile(fileType)) {
      return new MarkDownDocumentParser();
    } else if (FileType.isCodeFile(fileType)) {
      return new CodeDocumentParser();
    }
    //    else if (FileType.isCodeFile(fileType)) {
    //      //      return new WordDocumentParser();
    //    } else {
    //      //      return new WordDocumentParser();
    //    }
    return null;
  }

  public DocumentParser getDocumentParserByFileType(String fileType, Charset charset) {
    if (FileType.isTextFile(fileType)) {
      return new TextDocumentParser(charset);
    } else if (FileType.isWord(fileType)) {
      return new WordDocumentParser(charset);
    } else if (FileType.isPdf(fileType)) {
      return new PdfDocumentParser(charset);
    } else if (FileType.isMdFile(fileType)) {
      return new MarkDownDocumentParser(charset);
    } else if (FileType.isCodeFile(fileType)) {
      return new CodeDocumentParser(charset);
    }
    //
    //    else if (FileType.isCodeFile(fileType)) {
    //      //      return new WordDocumentParser(charset);
    //    } else {
    //      //      return new WordDocumentParser(charset);
    //    }
    return null;
  }
}
