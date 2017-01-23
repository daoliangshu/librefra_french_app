package com.librefra.daoliangshu.librefra.sentences;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by daoliangshu on 1/23/17.
 * Container on which to put example sentences
 */
public class SentenceContainer {
    ArrayList<String[]> sentences;
    int curIndex = 0;
    String word;
    Random rand = new Random();

    public SentenceContainer(String word, ArrayList<String[]> sentences) {
        this.word = word;
        this.sentences = sentences;
        if (sentences == null || sentences.size() <= 0) curIndex = -1;
        else curIndex = Math.abs(rand.nextInt() % sentences.size());
    }

    public void next() {
        if (sentences != null && sentences.size() > 0)
            curIndex = (curIndex + 1) % sentences.size();
        else curIndex = -1;
    }

    public String getSentenceFr() {
        if (curIndex < 0 || curIndex >= sentences.size()) return null;
        return sentences.get(curIndex)[0];
    }

    public String getSentenceZh() {
        if (curIndex < 0 || curIndex >= sentences.size()) return null;
        return sentences.get(curIndex)[1];
    }

    public String getWord() {
        return this.word;
    }
}
