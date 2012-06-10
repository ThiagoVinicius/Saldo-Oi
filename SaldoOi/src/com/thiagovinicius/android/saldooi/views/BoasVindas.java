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

import com.thiagovinicius.android.saldooi.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

/**
 * @author thiago
 *
 */
public class BoasVindas extends Activity implements OnClickListener {

	private static final String CHAVE_CONFIRMADO = "confirmado";
	private static final int ATRASO_CONFIRMACAO = 10000; // 10 segundos

	private TextView mTexto;
	private Button mBotaoEntendi;
	private SharedPreferences mPrefs;

	private int mAtrasoDado = 0;

	private Handler mManipulador = new Handler();
	private Runnable mHabilitarBotaoEntendi = new Runnable() {

		@Override
		public void run() {
			int diferenca = ATRASO_CONFIRMACAO - mAtrasoDado;
			String original = getResources().getString(R.string.rotulo_entendi);
			if (diferenca >= 0) {

				mBotaoEntendi.setText(String.format("%s (%d)", original,
						diferenca / 1000));
				mBotaoEntendi.setEnabled(false);
				mAtrasoDado += 1000;
				mManipulador.postDelayed(this, 1000);
			} else {
				mBotaoEntendi.setText(original);
				mBotaoEntendi.setEnabled(true);
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPrefs = getPreferences(MODE_PRIVATE);

		if (mPrefs.contains(CHAVE_CONFIRMADO)) {
			desviaParaTelaPrincipal();
		}

		Resources res = getResources();
		setContentView(R.layout.boas_vindas);
		mTexto = (TextView) findViewById(R.id.boas_vindas_textview);
		mBotaoEntendi = (Button) findViewById(R.id.boas_vindas_botao);
		mTexto.setText(Html.fromHtml(res
				.getString(R.string.view_boas_vindas_descricao)));
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBotaoEntendi.setOnClickListener(this);
		mAtrasoDado = 0;
		mManipulador.post(mHabilitarBotaoEntendi);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mBotaoEntendi.setOnClickListener(null);
		mManipulador.removeCallbacks(mHabilitarBotaoEntendi);
	}

	@Override
	public void onClick(View v) {
		if (v == mBotaoEntendi) {
			SharedPreferences.Editor ed = mPrefs.edit();
			ed.putInt(CHAVE_CONFIRMADO,
					getResources().getInteger(R.integer.numero_versao));
			ed.commit();
			desviaParaTelaPrincipal();
		}
	}

	private void desviaParaTelaPrincipal() {
		Intent i = new Intent(this, Principal.class);
		startActivity(i);
		finish();
	}

}
