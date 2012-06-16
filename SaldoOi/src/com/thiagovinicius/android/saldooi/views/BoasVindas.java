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
import android.net.Uri;
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
	private Button mBotaoPositivo;
	private Button mBotaoNegativo;
	private SharedPreferences mPrefs;

	private int mAtrasoDado = 0;

	private Handler mManipulador = new Handler();
	private Runnable mHabilitarBotaoEntendi = new Runnable() {

		@Override
		public void run() {
			int diferenca = ATRASO_CONFIRMACAO - mAtrasoDado;
			String original = getResources().getString(
					R.string.view_boas_vindas_aceito);
			if (diferenca >= 0) {

				mBotaoPositivo.setText(String.format("%s (%d)", original,
						diferenca / 1000));
				mBotaoPositivo.setEnabled(false);
				mAtrasoDado += 1000;
				mManipulador.postDelayed(this, 1000);
			} else {
				mBotaoPositivo.setText(original);
				mBotaoPositivo.setEnabled(true);
			}
		}
	};

	@Override
	protected void onCreate(Bundle estadoSalvo) {
		super.onCreate(estadoSalvo);
		mPrefs = getPreferences(MODE_PRIVATE);

		if (mPrefs.contains(CHAVE_CONFIRMADO)) {
			desviaParaTelaPrincipal();
		}

		Resources res = getResources();
		setContentView(R.layout.boas_vindas);
		mTexto = (TextView) findViewById(R.id.boas_vindas_textview);
		mBotaoPositivo = (Button) findViewById(R.id.boas_vindas_botao_positivo);
		mBotaoNegativo = (Button) findViewById(R.id.boas_vindas_botao_negativo);
		mTexto.setText(Html.fromHtml(res
				.getString(R.string.view_boas_vindas_descricao)));

		if (estadoSalvo != null) {
			mAtrasoDado = estadoSalvo.getInt("mAtrasoDado", 0);
		} else {
			mAtrasoDado = 0;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBotaoPositivo.setOnClickListener(this);
		mBotaoNegativo.setOnClickListener(this);
		mManipulador.post(mHabilitarBotaoEntendi);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mBotaoPositivo.setOnClickListener(null);
		mBotaoNegativo.setOnClickListener(null);
		mManipulador.removeCallbacks(mHabilitarBotaoEntendi);
	}

	@Override
	protected void onSaveInstanceState(Bundle salvandoEstado) {
		super.onSaveInstanceState(salvandoEstado);
		salvandoEstado.putInt("mAtrasoDado", mAtrasoDado);
	}

	@Override
	public void onClick(View v) {
		if (v == mBotaoPositivo) {
			SharedPreferences.Editor ed = mPrefs.edit();
			ed.putInt(CHAVE_CONFIRMADO,
					getResources().getInteger(R.integer.numero_versao));
			ed.commit();
			desviaParaTelaPrincipal();
		}
		if (v == mBotaoNegativo) {
			desinstalaApp();
		}
	}

	private void desviaParaTelaPrincipal() {
		Intent i = new Intent(this, Principal.class);
		startActivity(i);
		finish();
	}

	private void desinstalaApp() {
		Intent i = new Intent(Intent.ACTION_DELETE,
				Uri.parse("package:com.thiagovinicius.android.saldooi"));
		startActivity(i);
		mAtrasoDado = 0;
	}

}
