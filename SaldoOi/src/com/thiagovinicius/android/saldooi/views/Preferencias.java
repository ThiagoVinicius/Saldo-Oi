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
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
 */

package com.thiagovinicius.android.saldooi.views;

import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.ACTION_ALTERA_ALARME;
import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.EXTRA_HABILITAR;

import java.util.Date;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.text.format.DateFormat;

import com.thiagovinicius.android.saldooi.R;

public class Preferencias extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.preferencias);
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();

		prefs.registerOnSharedPreferenceChangeListener(this);
		atualizaTipoRenovacao(prefs);
		atualizaProximaRenovacao(prefs);
		atualizaValidade(prefs);
	}

	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences()
				.unregisterOnSharedPreferenceChangeListener(this);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String chave) {
		if ("renova_dados_tipo".equals(chave)
				|| "renova_dados_habilitado".equals(chave)) {

			Intent i = new Intent();
			i.setAction(ACTION_ALTERA_ALARME);
			i.putExtra(EXTRA_HABILITAR,
					prefs.getBoolean("renova_dados_habilitado", false));
			sendBroadcast(i);

		}
		if ("renova_dados_tipo".equals(chave)) {
			atualizaTipoRenovacao(prefs);
		}
		if ("renova_dados_agendado".equals(chave)) {
			atualizaProximaRenovacao(prefs);
		}
		if ("renova_dados_validade".equals(chave)) {
			atualizaValidade(prefs);
		}
	}

	private void atualizaTipoRenovacao(SharedPreferences prefs) {
		ListPreference seletorTipo = (ListPreference) findPreference("renova_dados_tipo");

		String valores[] = getResources().getStringArray(
				R.array.renova_dados_valores_descricao);

		if (prefs.contains("renova_dados_tipo")) {
			seletorTipo.setSummary(valores[new Integer(prefs.getString(
					"renova_dados_tipo", "0"))]);
		} else {
			seletorTipo
					.setSummary(R.string.preferencias_descricao_renova_dados_valor);
		}

	}

	private void atualizaProximaRenovacao(SharedPreferences prefs) {
		EditTextPreference campoRenovacao = (EditTextPreference) findPreference("renova_dados_agendado");

		String valor = null;
		if (prefs.contains("renova_dados_agendado")) {
			Date dataHora = new Date(prefs.getLong("renova_dados_agendado", 0L));
			valor = String.format("%s, %s", DateFormat.getTimeFormat(this)
					.format(dataHora),
					DateFormat.getDateFormat(this).format(dataHora));
		} else {
			valor = getResources().getString(
					R.string.preferencias_descricao_dados_proximo);
		}

		campoRenovacao.setSummary(valor);

	}

	private void atualizaValidade(SharedPreferences prefs) {
		EditTextPreference campoValidade = (EditTextPreference) findPreference("renova_dados_validade");

		String valor = null;
		if (prefs.contains("renova_dados_validade")) {
			Date dataHora = new Date(prefs.getLong("renova_dados_validade", 0L));
			valor = String.format("%s, %s", DateFormat.getTimeFormat(this)
					.format(dataHora),
					DateFormat.getDateFormat(this).format(dataHora));
		} else {
			valor = getResources().getString(
					R.string.preferencias_descricao_dados_validade);
		}

		campoValidade.setSummary(valor);
	}

}
