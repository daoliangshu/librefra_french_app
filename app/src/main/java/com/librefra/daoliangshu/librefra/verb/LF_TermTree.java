package com.librefra.daoliangshu.librefra.verb;

/**
 * Created by daoliangshu on 2016/11/20.
 */

import java.util.ArrayList;
import java.util.HashMap;

public class LF_TermTree<T> {
    private LF_TermTree parent;
    private String pattern = "#";
    private ArrayList<LF_TermTree> children;
    private ArrayList<String> possibilities;


    /*----------------------------------------------*/
    /*-----------CONSTRUCTOR------------------*/
    /*----------------------------------------------*/
    public LF_TermTree(LF_TermTree par, String pattern) {
        this.parent = par;
        this.pattern = pattern;
        children = new ArrayList<>();
        possibilities = new ArrayList<>();
    }


    /*----------------------------------------------*/
    /*---------------GETTERS------------------*/
	/*----------------------------------------------*/
    public String[] getPossibility(int index) {
        if (index > 0 && index < possibilities.size()) {
            return possibilities.get(index).split(",");
        }
        return null;
    }

    public ArrayList<String[]> getCurrentPossibilities() {
        ArrayList<String[]> res = new ArrayList<String[]>();
        for (int j = 0; j < this.possibilities.size(); j++) {
            res.add(this.possibilities.get(j).split(","));
        }
        return res;
    }

    public LF_TermTree getChild(int index) {
        if (index > 0 && index < children.size()) {
            return children.get(index);
        }
        return null;
    }

    public ArrayList<LF_TermTree> getChildren() {
        return children;
    }

    public String getPattern() {
        return this.pattern;
    }

    public LF_TermTree getParent() {
        return this.parent;
    }

    /**
     * Returns possible initUnit [tense,person,termIndex] which could lead to this conjugated form
     *
     * @param word a arbitrary (usually conjugate) word for which to search for a infinitive
     * @return str[0] is tense, str[1] is person, str[2] is termIndex
     */
    public ArrayList<String[]> getAllPossibilities(String word) {
        ArrayList<String[]> res = new ArrayList<String[]>();
        for (int i = 0; i < this.children.size(); i++) {

            if (word.length() > this.children.get(i).getPattern().length() &&
                    word.endsWith(this.children.get(i).getPattern())) {
                System.out.println(this.children.get(i).getPattern() + "  " + word);
                ArrayList<String[]> tmp = this.children.get(i).getAllPossibilities(word);
                if (tmp != null) {
                    res.addAll(tmp);
                }
            }
        }
        if (word.length() > this.pattern.length() && this.pattern != null && word.endsWith(this.pattern)) {
            res.addAll(getCurrentPossibilities());
        }
        return res;
    }

    public ArrayList<String> getRadicals(String word) {
        ArrayList<String> res = new ArrayList<>();
        boolean isLast = true;
        for (int i = 0; i < this.children.size(); i++) {
            if (word.length() > this.children.get(i).getPattern().length() &&
                    word.endsWith(this.children.get(i).getPattern())) {
                isLast = false;
                System.out.println(this.children.get(i).getPattern() + "  " + word);
                ArrayList<String> tmp = this.children.get(i).getRadicals(word);
                if (getPattern().length() >= 1) {
                    tmp.add(getPattern());
                }
                if (tmp != null) {
                    res.addAll(tmp);
                }
            }
        }
        System.out.println(this.children.size());
        if (isLast) {
            res.add(getPattern());
        }
        if (this.parent == null) {
            HashMap<String, Integer> tmpCount = new HashMap<>();
            ArrayList<String> radicals = new ArrayList<>();
            for (int i = 0; i < res.size(); i++) {
                if (!tmpCount.containsKey(res.get(i))) {
                    tmpCount.put(res.get(i), 1);
                    System.out.println("word: " + word + "  pat:" + res.get(i) + "   " +
                            "new : " +
                            word.substring(0, word.length() - res.get(i).length()));
                    String tmp = word.substring(0, word.length() - res.get(i).length());
                    if (tmp.endsWith("e")) {
                        radicals.add(tmp.substring(0, tmp.length() - 1));
                    }
                    if (tmp.endsWith("ç")) {
                        radicals.add(tmp.substring(0, tmp.length() - 1) + "c");
                    }
                    radicals.add(word.substring(0, word.length() - res.get(i).length()));

                }
            }
            return radicals;
        }
        return res;
    }


    /*----------------------------------------------*/
	/*---------SETTERS/ADDERS-----------------*/
	/*----------------------------------------------*/
    public void addPossibility(String tense, String person, String termIndex) {
        possibilities.add(tense + "," + person + "," + termIndex);
    }

    /**
     * Add to the corresponding leaf information about the possible conditions to
     * reach this termination info = {tense,person, termType}
     *
     * @param pattern
     * @param info
     */
    public void addToLeaf(String pattern, String[] info) {
        if (info[1].equals("6")) {
            info[1] = "5";
        }
        if (pattern.equals(this.pattern)) {
            this.addPossibility(info[0], info[1], info[2]);
        } else {
            if (pattern.length() < this.pattern.length() ||
                    pattern.endsWith(this.pattern)) {
                String nextPat = pattern.
                        charAt(pattern.length() - this.pattern.length() - 1) +
                        this.pattern;
                int idx = hasPattern(nextPat);
                if (idx != -1) {
                    this.children.get(idx).addToLeaf(pattern, info);
                } else {
                    this.addChild(nextPat);
                    this.children.get(this.children.size() - 1).
                            addToLeaf(pattern, info);
                }
            }
        }
    }

    public LF_TermTree addChild(char letter) {
        children.add(new LF_TermTree(this, pattern + letter));
        return children.get(children.size() - 1);
    }

    public LF_TermTree addChild(String pat) {
        children.add(new LF_TermTree(this, pat));
        return children.get(children.size() - 1);
    }

    private int hasPattern(String pat) {
        for (int i = 0; i < this.children.size(); i++) {
            if (this.children.get(i).getPattern().equals(pat)) {
                return i;
            }
        }
        return -1;
    }

    public void print() {
        System.out.println("/----" + this.pattern + "----/");
        for (int i = 0; i < this.children.size(); i++) {
            this.children.get(i).print();
        }
        System.out.println("/----/");
    }


}
