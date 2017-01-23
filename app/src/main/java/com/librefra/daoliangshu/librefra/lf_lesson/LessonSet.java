package com.librefra.daoliangshu.librefra.lf_lesson;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by daoliangshu on 2016/9/18.
 * <p>
 * This class receives the path for an xml file containing lesson(s),
 * It extracts them as an arrayList of class LessonUnit
 */
public class LessonSet {
    public ArrayList<LessonUnit> lessons;
    private Context context;
    private String fn;

    public LessonSet(InputStream is, String filename, Context context) {
        this.context = context;
        this.fn = filename;
        this.set(null, is);
    }

    public void setFilename(String filename) {
        this.fn = filename;
    }

    public String getFilename() {
        return this.fn;
    }

    public void set(String filepath, InputStream is) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException pe) {
            pe.printStackTrace();
        }

        Document document = null;
        if (builder != null) {
            try {
                if (filepath != null) {
                    document = builder.parse(new File(filepath));
                    if (filepath.contains("/"))
                        fn = filepath.substring(filepath.indexOf('/') + 1, -1);
                    else fn = filepath;
                } else if (is != null) {
                    document = builder.parse(is);
                }


            } catch (SAXException sae) {
                sae.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (document != null) {
                Element root = document.getDocumentElement();
                Log.i("XML_ROOT_NODE", root.getNodeName());
                NodeList elements = document.getElementsByTagName("unit");
                this.lessons = new ArrayList();
                for (int i = 0; i < elements.getLength(); i++) {
                    Element element = (Element) elements.item(i);
                    this.lessons.add(new LessonUnit(element, context));
                    this.lessons.get(i).setFilename(this.fn);
                }

            }
        }


    }

    public ArrayList<String> getTitles() {
        if (this.lessons == null) return null;
        ArrayList<String> titles = new ArrayList<>();
        for (LessonUnit u : this.lessons) {
            titles.add(u.getTitle());
        }
        return titles;
    }

    public ArrayList<String> getLessonChooserFormatedData() {
        if (this.lessons == null) return null;
        ArrayList<String> datas = new ArrayList<>();
        for (LessonUnit u : this.lessons) {
            datas.add(u.getTitle() + "," + u.getTitleZh() + "," + u.getFilename() + "," + u.getLevel() +
                    "," + u.getThematic());
        }
        return datas;
    }


    public ArrayList<HashMap<String, String>> getActivitiesInformation() {
        if (this.lessons != null) {
            ArrayList<HashMap<String, String>> res = new ArrayList<>();
            for (int i = 0; i < this.lessons.size(); i++) {
                res.addAll(this.lessons.get(i).getActivitiesInformation());
            }
            return res;
        }
        return null;
    }

    public int getUnitCount() {
        if (this.lessons == null) return 0;
        else return this.lessons.size();
    }


}
