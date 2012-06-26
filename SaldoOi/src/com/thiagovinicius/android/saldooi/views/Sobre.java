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

import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.text.Html;
import android.widget.TextView;

import com.thiagovinicius.android.saldooi.BuildConfig;
import com.thiagovinicius.android.saldooi.R;

/**
 * @author thiago
 *
 */
public class Sobre extends PreferenceActivity {

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

	public static class Autores extends Activity {
		@Override
		protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.view_autores);
			TextView tv = (TextView) findViewById(R.id.sobre_autores_textview);
			tv.setText(Html.fromHtml(getResources().getString(
					R.string.view_sobre_autores_texto)));
			setTitle(R.string.view_sobre_autores_titulo);
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.sobre);

		Preference prefVersao = findPreference("versao");
		String prefixoVersao = getResources().getString(
				R.string.view_sobre_versao_titulo);
		String nomeVersao = getResources().getString(R.string.nome_versao);
		prefVersao.setTitle(String.format("%s: %s", prefixoVersao, nomeVersao));
		removeInfoScmRelease(prefVersao);

	}

	/*
	 * O código desse método gera um warning, porque BuildConfig.DEBUG é uma
	 * variável final. Entretanto, essa a classe BuildConfig é recriada durante
	 * a construção do projeto.
	 */
	@SuppressWarnings("unused")
	private void removeInfoScmRelease (Preference pref) {
		if (BuildConfig.DEBUG == false) {
			pref.setSummary("");
		}
	}

}
