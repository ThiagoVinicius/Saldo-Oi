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

import static com.thiagovinicius.android.saldooi.util.Utils.getInfoPacote;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.thiagovinicius.android.saldooi.R;

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

	private long mAtrasoHora;
	private long mAtrasoDado = 0;
	private boolean mZerarAtraso = false;

	private Handler mManipulador = new Handler();
	private Runnable mHabilitarBotaoPositivo = new Runnable() {

		@Override
		public void run() {
			habilitaBotaoPositivo();
		}
	};

	@Override
	protected void onCreate(Bundle estadoSalvo) {
		super.onCreate(estadoSalvo);

		// Sempre inicialize este antes de qualquer outra coisa.
		mPrefs = getPreferences(MODE_PRIVATE);

		if (jaConfirmou()) {
			desviaParaTelaPrincipal();
		}

		criaInterfaceGrafica();
	}

	@Override
	protected void onResume() {
		super.onResume();
		mBotaoPositivo.setOnClickListener(this);
		mBotaoNegativo.setOnClickListener(this);

		if (mZerarAtraso) {
			mAtrasoDado = 0L;
			mZerarAtraso = false;
		}

		mAtrasoHora = SystemClock.uptimeMillis();
		long atrasoRestante = ATRASO_CONFIRMACAO - mAtrasoDado;
		if (atrasoRestante > 0L) {
			desabilitaBotaoPositivo();
			mManipulador.postDelayed(mHabilitarBotaoPositivo, atrasoRestante);
		} else {
			habilitaBotaoPositivo();
		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		mBotaoPositivo.setOnClickListener(null);
		mBotaoNegativo.setOnClickListener(null);
		mAtrasoDado += SystemClock.uptimeMillis() - mAtrasoHora;
		mManipulador.removeCallbacks(mHabilitarBotaoPositivo);
	}

	@Override
	protected void onRestoreInstanceState(Bundle estadoSalvo) {
		super.onRestoreInstanceState(estadoSalvo);
		mAtrasoDado = estadoSalvo.getLong("mAtrasoDado", 0L);
		mZerarAtraso = estadoSalvo.getBoolean("mZerarAtraso", false);
	}

	@Override
	protected void onSaveInstanceState(Bundle salvandoEstado) {
		super.onSaveInstanceState(salvandoEstado);
		salvandoEstado.putLong("mAtrasoDado", mAtrasoDado);
		salvandoEstado.putBoolean("mZerarAtraso", mZerarAtraso);
	}

	@Override
	public void onClick(View v) {
		if (v == mBotaoPositivo) {
			salvaConfirmacao();
			desviaParaTelaPrincipal();
		}
		if (v == mBotaoNegativo) {
			desinstalaApp();
		}
	}

	private void criaInterfaceGrafica() {
		setContentView(R.layout.boas_vindas);
		mTexto = (TextView) findViewById(R.id.boas_vindas_textview);
		mBotaoPositivo = (Button) findViewById(R.id.boas_vindas_botao_positivo);
		mBotaoNegativo = (Button) findViewById(R.id.boas_vindas_botao_negativo);
		mTexto.setText(Html.fromHtml(getResources().getString(
				R.string.view_boas_vindas_descricao)));
	}

	private boolean jaConfirmou() {
		return mPrefs.contains(CHAVE_CONFIRMADO);
	}

	private void salvaConfirmacao() {
		SharedPreferences.Editor ed = mPrefs.edit();
		PackageInfo info = getInfoPacote(this);
		ed.putInt(CHAVE_CONFIRMADO, info == null ? 0 : info.versionCode);
		ed.commit();
	}

	private void desabilitaBotaoPositivo() {
		mBotaoPositivo.setEnabled(false);
		mBotaoPositivo.setText(R.string.view_boas_vindas_aceito_desabilitado);
	}

	private void habilitaBotaoPositivo() {
		mBotaoPositivo.setEnabled(true);
		mBotaoPositivo.setText(R.string.view_boas_vindas_aceito);
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
		mZerarAtraso = true;
	}

}
