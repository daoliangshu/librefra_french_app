package com.librefra.daoliangshu.librefra.tools;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import com.librefra.daoliangshu.librefra.daoliangboom.DLB_Config;
import com.librefra.daoliangshu.librefra.lettrabulle.LB_Config;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by daoliangshu on 1/5/17.
 * For loading/saving user settings from conf file
 */

public class SettingsManager {
    public static void loadSettings(Context context) {
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
                document = builder.parse(context.openFileInput("conf_file"));
            } catch (SAXException sae) {
                sae.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (document != null) {
                Element root = document.getDocumentElement();
                Log.i("XML_ROOT_NODE", root.getNodeName());
                Element global_settings = (Element) document.
                        getElementsByTagName("global_settings").item(0);
                try {
                    DLB_Config.setSpeed(Integer.
                            parseInt(global_settings.getAttribute("qcm_speed")));
                    LB_Config.setSpeed(Integer.parseInt(global_settings.getAttribute("lb_speed")));
                } catch (Exception e) {
                    DLB_Config.setSpeed(1);
                    LB_Config.setSpeed(1);
                }

            } else {
                try {
                    FileOutputStream newConf =
                            context.openFileOutput("conf_file", Context.MODE_PRIVATE);
                    String data = "<root> <global_settings lb_speed=\"1\" qcm_speed=\"2\">" +
                            "</global_settings> </root>";
                    newConf.write(data.getBytes());
                    newConf.close();
                    Toast.makeText(context, "conf_file created", Toast.LENGTH_SHORT).show();
                    document = builder.parse(context.openFileInput("conf_file"));
                    if (document != null) {
                        Element root = document.getDocumentElement();
                        Log.i("XML_ROOT_NODE", root.getNodeName());
                        Element global_settings = (Element) document.
                                getElementsByTagName("global_settings").item(0);
                        try {
                            DLB_Config.setSpeed(Integer.
                                    parseInt(global_settings.getAttribute("qcm_speed")));
                            LB_Config.setSpeed(Integer.parseInt(global_settings.getAttribute("lb_speed")));
                        } catch (Exception e) {
                            DLB_Config.setSpeed(1);
                            LB_Config.setSpeed(1);
                        }

                    } else {
                        Log.e("CONF_FILE", "DIDN'T FINd CONF_FILE");
                    }
                } catch (SAXException sae) {
                    sae.printStackTrace();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
            }
        }
    }

    public static void saveSettings(Context context) {
        String lb_spd = String.format(Locale.ENGLISH, "%d", LB_Config.getSpeedCode());
        String qcm_spd = String.format(Locale.ENGLISH, "%d", DLB_Config.getSpeedCode());

        String data = "<root> <global_settings lb_speed=\"" + lb_spd + "\" " +
                "qcm_speed=\"" + qcm_spd + "\"> </global_settings> </root>";
        try {
            FileOutputStream newConf =
                    context.openFileOutput("conf_file", Context.MODE_PRIVATE);
            newConf.write(data.getBytes());
            newConf.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }


    }


    public static void copyAssetFileToData(Context context, String assetFolderName) {
        File mediaStorageDir = new File(context.getFilesDir(), assetFolderName);
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdir()) {
                Log.d("App", "failed to create directory");
                return;
            }
        } else {
            return;
        }
        AssetManager assetManager = context.getAssets();
        String[] files = null;
        try {
            files = assetManager.list(assetFolderName);
        } catch (IOException e) {
            Log.e("vocab", "Failed to get asset file list.", e);
        }
        if (files == null) return;
        for (String filename : files) {
            InputStream in = null;
            OutputStream out = null;
            try {
                in = assetManager.open(assetFolderName + "/" + filename);
                File file = new File(context.getFilesDir().toString(), "vocab");

                out = new FileOutputStream(new File(file, filename));

                //        context.openFileOutput(
                //        assetFolderName + "/" + filename, Context.MODE_PRIVATE);
                copyFile(in, out);
                in.close();
                in = null;
                out.flush();
                out.close();
                out = null;
            } catch (IOException e) {
                Log.e("tag", "Failed to copy asset file: " + filename, e);
            }
        }
    }

    private static void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while ((read = in.read(buffer)) != -1) {
            out.write(buffer, 0, read);
        }
    }
}

