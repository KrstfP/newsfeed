package com.krstf.newsfeed.port.outbound.ai;

public interface SemanticVectorizer {
    float[] vectorizeText(String text);
}
