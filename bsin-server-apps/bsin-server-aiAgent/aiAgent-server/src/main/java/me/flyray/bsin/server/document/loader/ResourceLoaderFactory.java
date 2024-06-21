package me.flyray.bsin.server.document.loader;

import lombok.AllArgsConstructor;
import me.flyray.bsin.domain.enums.FileType;
import me.flyray.bsin.server.document.split.TokenTextSplitter;
import me.flyray.bsin.server.document.split.CharacterTextSplitter;
import me.flyray.bsin.server.document.split.CodeTextSplitter;
import me.flyray.bsin.server.document.split.MarkdownTextSplitter;
import org.springframework.stereotype.Component;

@AllArgsConstructor
@Component
public class ResourceLoaderFactory {
  private final CharacterTextSplitter characterTextSplitter;
  private final CodeTextSplitter codeTextSplitter;
  private final MarkdownTextSplitter markdownTextSplitter;
  private final TokenTextSplitter tokenTextSplitter;

  public ResourceLoader getLoaderByFileType(String fileType) {
    if (FileType.isTextFile(fileType)) {
      return new TextFileLoader(characterTextSplitter);
    } else if (FileType.isWord(fileType)) {
      return new WordLoader(characterTextSplitter);
    } else if (FileType.isPdf(fileType)) {
      return new PdfFileLoader(characterTextSplitter);
    } else if (FileType.isMdFile(fileType)) {
      return new MarkDownFileLoader(markdownTextSplitter);
    } else if (FileType.isCodeFile(fileType)) {
      return new CodeFileLoader(codeTextSplitter);
    } else {
      return new TextFileLoader(characterTextSplitter);
    }
  }
}
