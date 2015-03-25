package com.supermario.dict;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;
public class Dictionary extends Activity implements OnClickListener, TextWatcher
{
	private final String DATABASE_PATH = android.os.Environment
			.getExternalStorageDirectory().getAbsolutePath()
			+ "/dictionary";
	private AutoCompleteTextView word;
	private final String DATABASE_FILENAME = "dictionary.db";
	private SQLiteDatabase database;
	private Button searchWord;
	private TextView showResult;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		database = openDatabase();
		searchWord = (Button) findViewById(R.id.searchWord);
		word = (AutoCompleteTextView) findViewById(R.id.word);
		searchWord.setOnClickListener(this);
		word.addTextChangedListener(this);
		showResult=(TextView)findViewById(R.id.result);
		Button add=(Button)findViewById(R.id.add);
		Button book=(Button)findViewById(R.id.book);
		add.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			 
		 	Toast toast=Toast.makeText(Dictionary.this,"添加成功",Toast.LENGTH_SHORT);
		 	toast.show();
				
			}
		});
		book.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				TextView getword=(TextView)findViewById(R.id.result);
				Intent intent=new Intent();
				intent.putExtra("getword", getword.getText().toString());
				intent.setClass(Dictionary.this, books.class);
				startActivity(intent);
			}
		});
	}
	public class DictionaryAdapter extends CursorAdapter
	{
		private LayoutInflater layoutInflater;
		@Override
		public CharSequence convertToString(Cursor cursor)
		{
			return cursor == null ? "" : cursor.getString(cursor
					.getColumnIndex("_id"));
		}
		private void setView(View view, Cursor cursor)
		{
			TextView tvWordItem = (TextView) view;
			tvWordItem.setText(cursor.getString(cursor.getColumnIndex("_id")));
		}
		@Override
		public void bindView(View view, Context context, Cursor cursor)
		{
			setView(view, cursor);
		}
		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent)
		{
			View view = layoutInflater.inflate(R.layout.word_list_item, null);
			setView(view, cursor);
			return view;
		}
		public DictionaryAdapter(Context context, Cursor c, boolean autoRequery)
		{
			super(context, c, autoRequery);
			layoutInflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		}
	}
	public void afterTextChanged(Editable s)
	{	 
		Cursor cursor = database.rawQuery(
				"select english as _id from t_words where english like ?",
				new String[]
				{ s.toString() + "%" });
		DictionaryAdapter dictionaryAdapter = new DictionaryAdapter(this,
				cursor, true);
		word.setAdapter(dictionaryAdapter);

	}
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after)
	{
		// TODO Auto-generated method stub

	}
	public void onTextChanged(CharSequence s, int start, int before, int count)
	{
		// TODO Auto-generated method stub

	}
	public void onClick(View view)
	{
		String sql = "select chinese from t_words where english=?";		
		Cursor cursor = database.rawQuery(sql, new String[]
		{word.getText().toString()});
			String result = "未找到该单词.";
			if (cursor.getCount() > 0)
			{
				cursor.moveToFirst();
				result = cursor.getString(cursor.getColumnIndex("chinese")).replace("&amp;", "&");
			}
			showResult.setText(word.getText()+"\n"+result.toString());
	}
	private SQLiteDatabase openDatabase()
	{
		try
		{
			String databaseFilename = DATABASE_PATH + "/" + DATABASE_FILENAME;
			File dir = new File(DATABASE_PATH);
			if (!dir.exists())
				dir.mkdir();
			if (!(new File(databaseFilename)).exists())
			{
				InputStream is = getResources().openRawResource(
						R.raw.dictionary);
				FileOutputStream fos = new FileOutputStream(databaseFilename);
				byte[] buffer = new byte[8192];
				int count = 0;
				while ((count = is.read(buffer)) > 0)
				{
					fos.write(buffer, 0, count);
				}
				fos.close();
				is.close();
			}
			SQLiteDatabase database = SQLiteDatabase.openOrCreateDatabase(
					databaseFilename, null);
			return database;
		}
		catch (Exception e)
		{
		}
		return null;
	}
}