package me.flyray.bsin.server.spi.store.embedding.inmemory;

import me.flyray.bsin.server.memory.store.InMemoryEmbeddingStoreJsonCodec;

public interface InMemoryEmbeddingStoreJsonCodecFactory {

  InMemoryEmbeddingStoreJsonCodec create();
}
