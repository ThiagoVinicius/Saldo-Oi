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

package com.thiagovinicius.android.saldooi.agendado;

import static com.thiagovinicius.android.saldooi.agendado.RenovaPlanoDados.ACTION_RENOVAR_PLANO_DADOS;
import static com.thiagovinicius.android.saldooi.agendado.RenovaPlanoDados.EXTRA_AGENDADO;

import java.sql.SQLException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.thiagovinicius.android.saldooi.db.AuxiliarOrm;
import com.thiagovinicius.android.saldooi.db.PacoteDados;
import com.thiagovinicius.android.saldooi.views.PlanoDados;

public class ProgramaAlarmes {

	private static final Logger logger = LoggerFactory
			.getLogger(ProgramaAlarmes.class.getSimpleName());

	public static void atualizaAlarme(Context ctx) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		alteraAlarme(ctx, prefs.getBoolean(PlanoDados.CHAVE_HABILITADO, false)
				&& prefs.contains(PlanoDados.CHAVE_PLANO));
	}

	public static void alteraAlarme(Context ctx, boolean habilitar) {
		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);

		if (habilitar) {
			habilitaRenovacao(ctx, prefs, am);
		} else {
			desabilitaRenovacao(ctx, prefs, am);
		}
	}

	private static Calendar dataValidade(Context ctx) throws SQLException {
		Calendar dataAlvo = null;
		AuxiliarOrm db = OpenHelperManager.getHelper(ctx, AuxiliarOrm.class);

		try {
			PacoteDados pacote = PacoteDados.encontraMaiorValidade(db
					.pacoteDados());
			if (pacote != null) {
				dataAlvo = Calendar.getInstance();
				dataAlvo.setTime(pacote.validade);
			}
			return dataAlvo;
		} finally {
			OpenHelperManager.releaseHelper();
		}

	}

	private static Calendar calculaHoraRenovacao(Calendar validade) {

		Calendar horaAlvo;
		Calendar hoje = Calendar.getInstance();

		if (validade.before(hoje)) {
			horaAlvo = hoje; // O plano esta vencido.
		} else {
			horaAlvo = validade;
		}
		// Nesse momento, a data está correta, resta apenas ajustar o horário

		horaAlvo.set(Calendar.HOUR_OF_DAY, 0);
		horaAlvo.set(Calendar.MINUTE, 10);
		horaAlvo.set(Calendar.SECOND, 0);
		horaAlvo.set(Calendar.MILLISECOND, 0);

		if (horaAlvo.before(Calendar.getInstance())) {
			// Temos que renovar hoje, porém já passamos da hora.
			// Se não fizermos nada aqui, o alarme será programado no passado,
			// e portanto, executado imediatamente.
		}

		return horaAlvo;
	}

	private static void habilitaRenovacao(Context ctx, SharedPreferences prefs,
			AlarmManager am) {
		logger.info("Programando renovação de saldo.");
		Calendar validade = null;

		try {
			validade = dataValidade(ctx);
		} catch (SQLException ex) {
			logger.warn("", ex);
		}

		if (validade == null) {
			logger.warn("habilitaRenovacao(): validade == null.");
			validade = Calendar.getInstance();
		}

		Calendar horaAlvo = calculaHoraRenovacao(validade);
		logger.info("Renovação de saldo programada para {}", horaAlvo);

		am.set(AlarmManager.RTC, horaAlvo.getTimeInMillis(),
				getIntentRenovacao(ctx));

		if (horaAlvo.after(Calendar.getInstance())) {
			SharedPreferences.Editor ed = prefs.edit();
			ed.putLong(PlanoDados.CHAVE_AGENDADO, horaAlvo.getTimeInMillis());
			ed.commit();
		}

	}

	private static void desabilitaRenovacao(Context ctx,
			SharedPreferences prefs, AlarmManager am) {
		logger.info("Desprogramando renovação de saldo.");
		am.cancel(getIntentRenovacao(ctx));

		SharedPreferences.Editor ed = prefs.edit();
		ed.remove(PlanoDados.CHAVE_AGENDADO);
		ed.commit();
	}

	private static PendingIntent getIntentRenovacao(Context ctx) {
		Intent i = new Intent();
		i.setAction(ACTION_RENOVAR_PLANO_DADOS);
		i.putExtra(EXTRA_AGENDADO, true);
		return PendingIntent.getBroadcast(ctx, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

}
