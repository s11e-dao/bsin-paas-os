package me.flyray.bsin.server.document.split;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class TokenTextSplitter implements TextSplitter {
  @Override
  public List<String> split(String content) {
    return null;
  }
}
