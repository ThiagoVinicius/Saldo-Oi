package com.thiagovinicius.android.saldooi.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.thiagovinicius.android.saldooi.R;

public class Principal extends ListActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Map<String, Object> entries = new HashMap<String, Object>();
		entries.put("intent", new Intent(this, Preferencias.class));
		entries.put("titulo", "Preferências");
		
		List<Map<String, Object>> entriesList = new ArrayList<Map<String, Object>>();
		entriesList.add(entries);
		
		
		setListAdapter(new SimpleAdapter(this, entriesList, R.layout.item_lista, 
				new String[] { "titulo" }, new int[] { R.id.layout_texto_1 }));
		
	}
	
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        @SuppressWarnings("rawtypes")
		Map map = (Map) l.getItemAtPosition(position);

        Intent intent = (Intent) map.get("intent");
        startActivity(intent);
    }
	
}
