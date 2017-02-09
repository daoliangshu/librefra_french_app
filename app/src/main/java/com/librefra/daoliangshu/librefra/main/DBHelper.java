package com.librefra.daoliangshu.librefra.main;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.util.SparseArray;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by gigitintin on 05/04/16.
 * Main Class for database manipulation,
 * provide method for extracting dictionary entries.
 */
public class DBHelper extends SQLiteOpenHelper {

    public static final String TRANS_STR_PREFIX = "trans";
    public static final String TRANS_STR_1 = "trans1";
    public static final String TRANS_STR_2 = "trans2";
    public static final String TRANS_STR_3 = "trans3";
    public static final String TB_TABLE_STR = "db_table";

    public static final String TRANS_ID_PREFIX = "trans_id";
    public static final String TRANS_ID_1 = "trans_id1";
    public static final String TRANS_ID_2 = "trans_id2";
    public static final String TRANS_ID_3 = "trans_id3";

    public static final String SENTENCE_FR = "sentence_fr";
    public static final String SENTENCE_ZH = "sentence_zh";

    public static final String WORD = "word";
    public static final String GENRE = "genre";
    public static final String PHONETIC = "phonetic";
    public static final String INFO = "info";
    public static final String OTHER_TYPE = "type";
    public static final String EXTRA = "extra";

    public static final int SUBST = 101;
    public static final int VERB = 102;
    public static final int OTHER = 105;
    public static final int SENTENCE = 120;

    public static final int RESULT_EXACT = 0;
    public static final int RESULT_POST = 1;
    private static final int RESULT_PRE = 2;
    private static final int RESTULT_BOTH_SIDE = 3;

    private static final SparseArray<String> table_map = new SparseArray<String>() {{
        put(SUBST, "subst_fr");
        put(VERB, "verb_fr");
        put(OTHER, "adj_extended_fr");
        put(SENTENCE, "sentences_fr_zh");
    }};


    private static DBHelper appDbHelper;
    private static String DB_PATH;
    private static final String DB_NAME = "dic_librefra.db";

    private SQLiteDatabase myDB;
    private Context myContext;

    private DBHelper(Context context) throws SQLException {
        super(context, DB_NAME, null, 3);
        myContext = context;
        DB_PATH = myContext.getFilesDir().getPath();
        openDB();
    }

    public static synchronized DBHelper getInstance(Context context) {
        if (DBHelper.appDbHelper == null) {
            try {
                appDbHelper = new DBHelper(context);
            } catch (SQLException sqle) {
                sqle.printStackTrace();
            }
        }
        return appDbHelper;
    }

    public ArrayList<HashMap<String, String>> getSentences_by_keywords(String keyWordsComma) {
        if (keyWordsComma == null) return null;
        String keywords[] = keyWordsComma.split(",");
        String query = "SELECT sentence_fr, sentence_zh FROM " + table_map.get(SENTENCE) +
                " WHERE ";
        int count = 0;
        for (String key : keywords) {
            if (count > 0) query += "OR ";
            query += "keywords_fr LIKE '%" + key + "%' ";
            ++count;
        }
        Cursor c = myDB.rawQuery(query, null);
        ArrayList<HashMap<String, String>> resList = new ArrayList<>();
        while (c.moveToNext()) {
            HashMap<String, String> res = new HashMap<>();
            res.put(SENTENCE_FR, c.getString(0));
            res.put(SENTENCE_ZH, c.getString(1));
            resList.add(res);
        }
        c.close();
        return resList;
    }


    public HashMap<String, String> getEntryById(int id, int DB_TABLE) {
        String tableName = table_map.get(DB_TABLE, null);
        if (tableName == null || myDB == null) return null;
        Cursor c = myDB.rawQuery("SELECT " +
                DBHelper.WORD +
                ", " + DBHelper.TRANS_ID_1 +
                ", " + DBHelper.TRANS_ID_2 +
                ", " + DBHelper.TRANS_ID_3 +
                " FROM " + tableName +
                " WHERE _id=" + id + ";", null);
        if (c == null) return null;
        if (!c.moveToFirst()) return null;
        HashMap<String, String> res = new HashMap<>();
        res.put(DBHelper.WORD, c.getString(0));
        res.put(DBHelper.TRANS_ID_1, c.getString(1));
        res.put(DBHelper.TRANS_ID_2, c.getString(2));
        res.put(DBHelper.TRANS_ID_3, c.getString(3));
        return res;
    }


    /***
     * Retrieve WORD, and ITS CHINESE Translations given the french word _ID
     * MAP = WORD, TRANS_X
     *
     * @param wordId:   id of the word in the table
     * @param DB_TABLE: table code to get the word from
     * @return
     */
    public HashMap<String, String> getWordTransById(int wordId,
                                                    int DB_TABLE) {
        return getWordTrans(
                String.format(Locale.ENGLISH,
                        "%d", wordId), DB_TABLE, "_id");
    }


    public HashMap<String, String> getWordTrans(String word, int DB_TABLE,
                                                String word_col_name) {
        String column = "word";
        if (word_col_name != null) column = word_col_name;
        String tableName = table_map.get(DB_TABLE, null);
        if (tableName == null) return null;
        if (myDB == null) Log.e("NO_DB", "NO_DB");
        Cursor c;
        try {
            String sufix = (column.equals("_id")) ? word : "'" + word + "'";
            c = myDB.rawQuery("SELECT trans_id1, trans_id2, trans_id3, genre, word, phonetic FROM " +
                            tableName + " WHERE " + column + "=" + sufix + ";",
                    null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        if (c == null) return null;
        HashMap<String, String> res = new HashMap<>();
        c.moveToFirst();

        if (c.getCount() <= 0) {
            c.close();
            return null;
        }
        res.put("word", c.getString(4));
        res.put(PHONETIC, c.getString(5));
        for (int i = 0; i < 3; i++) {
            String trans_ids_list[] = null;
            String transword = " ";
            try {
                if (c.getString(i) == null) {
                    continue;
                }
                trans_ids_list = c.getString(i).split(",");
            } catch (NumberFormatException nfe) {
                nfe.printStackTrace();
            }

            if (trans_ids_list != null && trans_ids_list.length > 0) {
                transword = "";
                for (int j = 0; j < trans_ids_list.length; j++) {
                    String temp = getTransById(Integer.parseInt(trans_ids_list[j]));
                    if (j != 0) transword += ",";
                    transword += temp;
                }
            } else transword = "";
            res.put(DBHelper.TRANS_STR_PREFIX + (i + 1), transword);
        }
        res.put("genre", c.getString(3));
        c.close();
        return res;
    }

    public ArrayList<HashMap<String, String>> getTransList_byPattern(String startWithString,
                                                                     boolean tbSubst,
                                                                     boolean tbVerb,
                                                                     boolean tbOther,
                                                                     int resFlag) {

        if (appDbHelper == null || !(tbSubst || tbVerb || tbOther)) return null;
        Cursor c;
        String preSearch = "";
        String postSearch = "";
        switch (resFlag) {
            case DBHelper.RESULT_EXACT:
                break;
            case DBHelper.RESULT_POST:
                postSearch = "%";
                break;
            case DBHelper.RESULT_PRE:
                preSearch = "%";
                break;
            case DBHelper.RESTULT_BOTH_SIDE:
                preSearch = "%";
                postSearch = "%";
                break;
            default:
        }

        String subpref = "";
        int tbCount = 0;
        if (tbSubst) {
            subpref += "SELECT * FROM ( SELECT * FROM  " + table_map.get(DBHelper.SUBST) +
                    " WHERE word LIKE '" + preSearch + startWithString + postSearch + "' LIMIT 20 )";
            tbCount++;
        }
        if (tbVerb) {
            if (tbCount > 0) subpref += " UNION ";
            subpref += "SELECT * FROM ( SELECT * FROM " + table_map.get(DBHelper.VERB) +
                    " WHERE word LIKE '" + preSearch + startWithString + postSearch + "' LIMIT 20 )";
            tbCount++;
        }
        if (tbOther) {
            if (tbCount > 0) subpref += " UNION ";
            subpref += "SELECT * FROM ( SELECT * FROM " + table_map.get(DBHelper.OTHER) +
                    " WHERE word LIKE \"" + preSearch + startWithString + postSearch + "\" LIMIT 20 )";
        }
        if (subpref.equals("")) return null;
        String prefix2 = "SELECT * FROM ( " + subpref +
                " ) ORDER BY word ASC;";
        c = myDB.rawQuery(prefix2,
                null);
        if (c == null) return null;

        ArrayList<HashMap<String, String>> resList = new ArrayList<>();
        int count = 0;
        if (!c.moveToFirst()) return null;

        do {
            HashMap<String, String> res = new HashMap<>();
            for (int i = 0; i < c.getColumnCount(); i++) {
                if (c.getColumnName(i).equals("extra")) {
                    switch (c.getString(i).charAt(0)) {
                        case 'a':
                            res.put(TB_TABLE_STR, String.valueOf(DBHelper.OTHER));
                            break;
                        case 's':
                            res.put(TB_TABLE_STR, String.valueOf(DBHelper.SUBST));
                            break;
                        case 'v':
                            res.put(TB_TABLE_STR, String.valueOf(DBHelper.VERB));
                            break;
                    }
                } else if (c.getColumnName(i).equals(DBHelper.TRANS_ID_1) ||
                        c.getColumnName(i).equals(DBHelper.TRANS_ID_2) ||
                        c.getColumnName(i).equals(DBHelper.TRANS_ID_3)) {
                    String trans_ids_list[];
                    String translationWord = " ";
                    if (c.getString(i) == null || c.getString(i).equals("")) {
                        continue;
                    }
                    trans_ids_list = c.getString(i).split(",");
                    if (trans_ids_list.length > 0) {
                        translationWord = "";
                        for (int j = 0; j < trans_ids_list.length; j++) {
                            String temp = getTransById(Integer.parseInt(trans_ids_list[j]));
                            if (j != 0) translationWord += ",";
                            translationWord += temp;
                        }
                    } else translationWord = "";
                    res.put(c.getColumnName(i), translationWord);
                } else {
                    res.put(c.getColumnName(i), c.getString(i));
                }
            }
            resList.add(res);
            count += 1;
        } while (c.moveToNext());
        c.close();
        return resList;
    }


    /***
     * Retrieves a CHINESE translation, given its _ID
     * Note: Null is return if fails
     *
     * @param id
     * @return
     */
    public String getTransById(int id) {
        Cursor c = myDB.rawQuery("SELECT word FROM table_zh WHERE _id=" + id + ";", null);
        if (c == null) return null;
        if (!c.moveToFirst()) return null;
        String word = c.getString(0);
        c.close();
        return word;
    }

    public String getWordById(int id, int DB_TYPE) {
        Cursor c = myDB.rawQuery("SELECT word FROM " +
                        table_map.get(DB_TYPE) +
                        " WHERE _id=" + id + ";",
                null);
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            String res = c.getString(0);
            c.close();
            return res;
        }
        return null;
    }


    /***
     * Retrieve an ArrayList<Integer> of _ID of french WORD in a given DB_TABLE,
     * that start by the given pattern
     *
     * @param pattern:  word starts with string
     * @param DB_TABLE: code of the table to git ids from
     * @return
     */
    public ArrayList<Integer> getWordIds(String pattern, int DB_TABLE) {
        String tableName = DBHelper.table_map.get(DB_TABLE, null);
        if (tableName == null) return null;
        Cursor c;
        String query = "SELECT _id FROM " + tableName + " WHERE word LIKE '" + pattern + "%';";
        c = myDB.rawQuery(query, null);
        if (c == null || !c.moveToFirst()) return null;
        ArrayList<Integer> res = new ArrayList<>();
        do {
            res.add(Integer.parseInt(c.getString(0)));
        } while (c.moveToNext());
        c.close();
        return res;
    }

    public ArrayList<String[]> getSentences(String frenchWord) {
        Cursor c;
        String query = "SELECT " + SENTENCE_FR + ", " + SENTENCE_ZH +
                " FROM " + table_map.get(SENTENCE) + " WHERE " + SENTENCE_FR +
                " LIKE '%" + frenchWord + "%';";
        c = myDB.rawQuery(query, null);
        if (c == null || !c.moveToFirst()) return null;
        ArrayList<String[]> res = new ArrayList<>();
        do {
            res.add(new String[]{c.getString(0), c.getString(1)});
        } while (c.moveToNext());
        c.close();
        return res;
    }


    public boolean checkDB() {
        File dbFile = myContext.getDatabasePath(DB_NAME);
        return dbFile.exists();
    }


    public void copyDB(File dbFile) throws IOException {
        //Open local db as input stream
        Log.e("Err0", "Could not open a stream");
        InputStream input = myContext.getAssets().open(DB_NAME);
        //Path to the new created empty db
        Log.e("Info", "Opening:" + dbFile.toString());


        OutputStream output = new FileOutputStream(dbFile);
        Log.e("Err", "Could not open a stream");

        //transfer bytes from inputfile to outputfile
        byte[] buffer = new byte[1024];
        Log.e("Err3", "Could not open a stream");
        while (input.read(buffer) > 0) {
            output.write(buffer);
            System.out.println(buffer.toString());
        }
        Log.i("CopyDB", "OK");
        //close
        output.flush();
        output.close();
        input.close();
    }


    public void openDB() throws SQLException {
        String path = DB_PATH + "/dic_librefra.db";
        File dbFile = new File(path);
        if (!dbFile.exists()) {
            try {
                copyDB(dbFile);
            } catch (IOException e) {
                throw new RuntimeException("Error creating source database", e);
            }
        }
        File file = new File(path);
        if (file.exists() && !file.isDirectory()) {
            myDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);

            Log.i("DB", "Opend succesfully !!");

        } else {
            Log.i("ERR", "File not found");
            System.exit(-1);
        }
    }

    @Override
    public synchronized void close() {
        if (myDB != null) myDB.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
