package cc.aznc.dictforfun;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends Activity {
	
	AutoCompleteTextView dictSearch;
	Dictionary dict;
	Button queryBtn;
	EditText resultTxt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		dictSearch = (AutoCompleteTextView) findViewById(R.id.dictSearch);
		queryBtn = (Button) findViewById(R.id.queryButton);
		resultTxt = (EditText) findViewById(R.id.resultText);
		
		// slow at app first run, maybe use thread do it
		dict = new Dictionary(this);
		
		bindEvents();
	}
	
	private void bindEvents()
	{
		queryBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				onQueryClick();
			}
		});
		initAutoComplete();
	}
	
	private void initAutoComplete() {
        final int[] to = new int[]{android.R.id.text1};
        final String[] from = new String[]{"words"};
		SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_dropdown_item_1line,
                null,
                from,
                to,
                0);

        adapter.setStringConversionColumn(0);
        adapter.setFilterQueryProvider(new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence description) {
                String queryWord = dictSearch.getText().toString();
                Cursor managedCursor = dict.getLike(queryWord);
                return managedCursor;
            }
        });

        dictSearch.setAdapter(adapter);
        dictSearch.setThreshold(1);
    }
	
	private void onQueryClick()
	{
		try {
			String word = dictSearch.getText().toString();
			Log.v("Click", "query for: " + word);
			String data = dict.query(word);
			if (null == data) {
				// FIXME: notify 查無資料
				return;
			}
			JSONObject jdata = new JSONObject(data);
			JSONArray attrs = jdata.names();
			if (null == attrs) {
				return;
			}
			
			String outTxt = "";
			for (int i = 0; i < attrs.length(); i++) {
				String symbol = attrs.getString(i);
				//Log.v("TAG", symbol);
				outTxt += "[" + symbol + "]\n";
				JSONArray describes = jdata.getJSONArray(symbol);
				for (int j = 0; j < describes.length(); j++) {
					String desc = describes.getString(j);
					outTxt += desc + "\n";
				}
				outTxt += "\n";
			}
			resultTxt.setText(outTxt);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
