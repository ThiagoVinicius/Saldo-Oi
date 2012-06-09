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
 *  along with Saldo Oi. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.thiagovinicius.android.saldooi.views;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.thiagovinicius.android.saldooi.R;

/**
 * @author thiago
 *
 */
public class Sobre extends ListActivity {

	public static class Licencas extends Activity {

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			Resources res = getResources();
			String texto;
			try {
				texto = textoLicencas(res);
			} catch (IOException e) {
				texto = "Erro ao carregar tela!";
			}
			setTitle(res.getString(R.string.view_sobre_licencas_titulo));
			setContentView(R.layout.view_licencas);
			TextView tv = (TextView) findViewById(R.id.sobre_licencas);
			tv.setText(texto);
		}

		private String textoLicencas(Resources res) throws IOException {
			byte dados[] = new byte[8096];
			int lido;

			InputStream is = res.openRawResource(R.raw.licencas);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			for (;;) {
				lido = is.read(dados);
				if (lido <= 0) {
					break;
				} else {
					bos.write(dados, 0, lido);
				}
			}

			try {
				return bos.toString("UTF-8");
			} catch (UnsupportedEncodingException e) {
				// every java runtime must support this one.
				throw e;
			}

		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setTitle(getResources().getString(R.string.view_sobre_titulo));

		Map<String, Object> entries = new HashMap<String, Object>();
		entries.put("intent", new Intent(this, Licencas.class));
		entries.put("titulo",
				getResources().getString(R.string.view_sobre_licencas));

		List<Map<String, Object>> entriesList = new ArrayList<Map<String, Object>>();
		entriesList.add(entries);

		setListAdapter(new SimpleAdapter(this, entriesList,
				android.R.layout.simple_list_item_1, new String[] { "titulo" },
				new int[] { android.R.id.text1 }));

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		@SuppressWarnings("rawtypes")
		Map map = (Map) l.getItemAtPosition(position);

		Intent intent = (Intent) map.get("intent");
		startActivity(intent);
	}

}
