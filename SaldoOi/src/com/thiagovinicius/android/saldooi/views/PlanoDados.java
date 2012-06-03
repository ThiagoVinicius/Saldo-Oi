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

import java.util.Date;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.text.format.DateFormat;

import com.thiagovinicius.android.saldooi.R;
import com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes;

public class PlanoDados extends PreferenceActivity implements
		OnSharedPreferenceChangeListener {

	public static final String CHAVE_AGENDADO = "renova_dados_agendado";
	public static final String CHAVE_HABILITADO = "renova_dados_habilitado";
	public static final String CHAVE_PLANO = "renova_dados_tipo";
	public static final String CHAVE_VALIDADE = "renova_dados_validade";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.plano_dados);
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
		if (CHAVE_PLANO.equals(chave) || CHAVE_HABILITADO.equals(chave)) {
			ProgramaAlarmes.atualizaAlarme(this);
		}
		if (CHAVE_PLANO.equals(chave)) {
			atualizaTipoRenovacao(prefs);
		}
		if (CHAVE_AGENDADO.equals(chave)) {
			atualizaProximaRenovacao(prefs);
		}
		if (CHAVE_VALIDADE.equals(chave)) {
			atualizaValidade(prefs);
		}
	}

	private void atualizaTipoRenovacao(SharedPreferences prefs) {
		ListPreference seletorTipo = (ListPreference) findPreference(CHAVE_PLANO);

		String valores[] = getResources().getStringArray(
				R.array.renova_dados_valores_descricao);

		if (prefs.contains(CHAVE_PLANO)) {
			seletorTipo.setSummary(valores[new Integer(prefs.getString(
					CHAVE_PLANO, "0"))]);
		} else {
			seletorTipo
					.setSummary(R.string.preferencias_descricao_renova_dados_valor);
		}

	}

	private void atualizaProximaRenovacao(SharedPreferences prefs) {
		EditTextPreference campoRenovacao = (EditTextPreference) findPreference(CHAVE_AGENDADO);

		String valor = null;
		if (prefs.contains(CHAVE_AGENDADO)) {
			Date dataHora = new Date(prefs.getLong(CHAVE_AGENDADO, 0L));
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
		EditTextPreference campoValidade = (EditTextPreference) findPreference(CHAVE_VALIDADE);

		String valor = null;
		if (prefs.contains(CHAVE_VALIDADE)) {
			Date dataHora = new Date(prefs.getLong(CHAVE_VALIDADE, 0L));
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
