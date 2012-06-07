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

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.text.format.DateFormat;

import com.thiagovinicius.android.saldooi.R;
import com.thiagovinicius.android.saldooi.agendado.RenovaPlanoDados;

public class PlanoDados extends PreferenceActivity implements
		OnSharedPreferenceChangeListener, OnPreferenceClickListener {

	public static final String CHAVE_AGENDADO = "renova_dados_agendado";
	public static final String CHAVE_HABILITADO = "renova_dados_habilitado";
	public static final String CHAVE_PLANO = "renova_dados_tipo";
	public static final String CHAVE_VALIDADE = "renova_dados_validade";
	public static final String CHAVE_RENOVAR = "renova_dados_agora";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.plano_dados);
	}

	@Override
	protected void onResume() {
		super.onResume();

		SharedPreferences prefs = getPreferenceScreen().getSharedPreferences();

		findPreference(CHAVE_RENOVAR).setOnPreferenceClickListener(this);
		findPreference(CHAVE_HABILITADO).setOnPreferenceClickListener(this);
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
		findPreference(CHAVE_RENOVAR).setOnPreferenceClickListener(null);
		findPreference(CHAVE_HABILITADO).setOnPreferenceClickListener(null);
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		if (CHAVE_RENOVAR.equals(preference.getKey())) {

			final Context self = this;

			AlertDialog.Builder confirmaRenovacao = new AlertDialog.Builder(
					this);
			confirmaRenovacao
					.setMessage(R.string.mensagem_confirmacao_renova_dados_agora);

			confirmaRenovacao.setPositiveButton(android.R.string.yes,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							RenovaPlanoDados.renovaImediatamente(self);
						}
					});

			confirmaRenovacao.setNegativeButton(android.R.string.no, null);

			confirmaRenovacao.setTitle(android.R.string.dialog_alert_title);
			confirmaRenovacao.setIcon(android.R.drawable.ic_dialog_alert);

			AlertDialog alerta = confirmaRenovacao.create();
			alerta.show();

		}

		return false;
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String chave) {

		if (CHAVE_PLANO.equals(chave) || CHAVE_HABILITADO.equals(chave)) {

			boolean podeAgendar = true;

			if (CHAVE_HABILITADO.equals(chave)) {

				long agora = Calendar.getInstance().getTimeInMillis();
				int mensagemErro = 0;

				if (prefs.getBoolean(CHAVE_HABILITADO, false) == false) {
					mensagemErro = 0;
				} else if (prefs.contains(CHAVE_VALIDADE) == false) {
					mensagemErro = R.string.mensagem_erro_renova_dados_sem_info;
				} else if (prefs.getLong(CHAVE_VALIDADE, 0L) < agora) {
					mensagemErro = R.string.mensagem_erro_renova_dados_vencido;
				}

				if (mensagemErro != 0) {

					podeAgendar = false;
					((CheckBoxPreference) findPreference(CHAVE_HABILITADO))
							.setChecked(false);

					AlertDialog.Builder erro = new AlertDialog.Builder(this);
					erro.setMessage(mensagemErro);
					erro.setPositiveButton(R.string.rotulo_entendi, null);
					erro.setTitle(android.R.string.dialog_alert_title);
					erro.setIcon(android.R.drawable.ic_dialog_alert);
					AlertDialog alertaErro = erro.create();
					alertaErro.show();

				}

			}

			if (podeAgendar) {
				RenovaPlanoDados.agendaRenovacao(this);
			}
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
		Preference seletorTipo = findPreference(CHAVE_PLANO);

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
		Preference campoRenovacao = findPreference(CHAVE_AGENDADO);

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
		Preference campoValidade = findPreference(CHAVE_VALIDADE);

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
