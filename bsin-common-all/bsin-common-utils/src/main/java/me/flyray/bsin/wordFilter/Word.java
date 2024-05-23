package me.flyray.bsin.wordFilter;


public class Word implements Comparable<Word> {
    public char c;
    public WordList next = null;

    public Word(char c) {
        this.c = c;
    }

    @Override
    public int compareTo(Word word) {
        return c - word.c;
    }

    public String toString() {
        return c + "(" + (next == null ? null : next.size()) + ")";
    }
}
