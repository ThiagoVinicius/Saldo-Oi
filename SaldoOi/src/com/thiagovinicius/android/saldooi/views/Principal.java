/**
 *  This file is part of Saldo Oi.
 *
 *  Saldo Oi is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  Saldo Oi is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with Saldo Oi.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.thiagovinicius.android.saldooi.views;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.thiagovinicius.android.saldooi.R;

public class Principal extends Activity implements OnItemClickListener {

	public static class SubAtividades extends ListView {

		public SubAtividades(Context ctx, AttributeSet atr) {
			super(ctx, atr);

			Map<String, Object> entries = new HashMap<String, Object>();
			entries.put("intent", new Intent(ctx, PlanoDados.class));
			entries.put("titulo",
					getResources().getString(R.string.view_plano_dados));

			List<Map<String, Object>> entriesList = new ArrayList<Map<String, Object>>();
			entriesList.add(entries);

			setAdapter(new SimpleAdapter(ctx, entriesList,
					android.R.layout.simple_list_item_1,
					new String[] { "titulo" }, new int[] { android.R.id.text1 }));

		}

	}

	protected ListView mListaAtividades;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.view_principal);
		mListaAtividades = (ListView) findViewById(R.id.principal_subatividades);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mListaAtividades.setOnItemClickListener(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mListaAtividades.setOnItemClickListener(null);
	}

	@Override
	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
		@SuppressWarnings("rawtypes")
		Map map = (Map) l.getItemAtPosition(position);

		Intent intent = (Intent) map.get("intent");
		startActivity(intent);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.principal, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.menu_principal_sobre) {
			startActivity(new Intent(this, Sobre.class));
			return true;
		}
		return false;
	}

}
