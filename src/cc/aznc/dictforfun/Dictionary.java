package cc.aznc.dictforfun;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Dictionary {
	private Context context;
	private static SQLiteDatabase db;
	
	public Dictionary(Context ctx)
	{
		context = ctx;
		initDBFile();
	}
	
	private void initDBFile()
	{
		String dbFilePath = context.getFilesDir() + "/dict.db";
		Log.v("initDB", "dbFilePath = " + dbFilePath);
		File file = new File(dbFilePath);
		if(!file.exists()) {
			copyDBFileTo(dbFilePath);
		}
		
		db = SQLiteDatabase.openDatabase(dbFilePath, null, 0);
	}
	
	private void copyDBFileTo(String dst)
	{
		OutputStream out = null;
		ZipInputStream zin = null;
		try {
			InputStream in = context.getResources().openRawResource(R.raw.dict);
			zin = new ZipInputStream(new BufferedInputStream(in));
			out = new BufferedOutputStream(new FileOutputStream(dst));
			byte data[] = new byte[1024000];
			int read;
			// have only one file in zip
			while ((read = zin.read(data)) > 0) {
	            out.write(data, 0, read);
	        }
		} catch (IOException e) {
			// there should be no exception
		} finally {
			close(zin);
			close(out);
		}
	}
	
	private void close(Closeable c)
	{
		if (null == c) return;
		try {
			c.close();
		} catch (IOException e) {
			// ignore exception
		}
	}
	
	public String query(String word)
	{
		String words[] = {word};
		Cursor cursor = db.rawQuery("SELECT * FROM DICT WHERE words = ?", words);
		if (cursor.moveToFirst()) {
			String v = cursor.getString(1);
			return v;
		}
		return null;
	}

	public Cursor getLike(String queryWord) {
		String words[] = {queryWord + "%"};
		Cursor cursor = db.rawQuery("SELECT words, rowid AS _id FROM DICT WHERE words like ?", words);
		cursor.moveToFirst();
		return cursor;
	}
}
