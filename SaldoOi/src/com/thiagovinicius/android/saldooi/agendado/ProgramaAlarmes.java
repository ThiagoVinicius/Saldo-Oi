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

import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProgramaAlarmes extends BroadcastReceiver {

	private static final Logger logger = LoggerFactory
			.getLogger(ProgramaAlarmes.class.getSimpleName());

	public static final String ACTION_ALTERA_ALARME = ProgramaAlarmes.class
			.getCanonicalName() + ".ACTION_ALTERA_ALARME";
	public static final String EXTRA_HABILITAR = ProgramaAlarmes.class
			.getCanonicalName() + ".EXTRA_HABILITAR";

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (ACTION_ALTERA_ALARME.equals(intent.getAction())) {
			if (intent.getBooleanExtra(EXTRA_HABILITAR, true)) {
				habilitaRenovacao(ctx);
			} else {
				desabilitaRenovacao(ctx);
			}
		}
	}

	private void habilitaRenovacao(Context ctx) {
		logger.info("Programando renovação de saldo.");
		Calendar horaAlvo = Calendar.getInstance();
		horaAlvo.set(Calendar.HOUR_OF_DAY, 0);
		horaAlvo.set(Calendar.MINUTE, 10);
		horaAlvo.set(Calendar.SECOND, 0);
		horaAlvo.set(Calendar.MILLISECOND, 0);
		while (horaAlvo.before(Calendar.getInstance())) { // Oops, já passou
			horaAlvo.roll(Calendar.DAY_OF_YEAR, true);
		}
		logger.info("Renovação de saldo programada para {}", horaAlvo);
		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC, horaAlvo.getTimeInMillis(),
				AlarmManager.INTERVAL_DAY, getIntentRenovacao(ctx));
	}

	private void desabilitaRenovacao(Context ctx) {
		logger.info("Desprogramando renovação de saldo.");
		AlarmManager am = (AlarmManager) ctx
				.getSystemService(Context.ALARM_SERVICE);
		am.cancel(getIntentRenovacao(ctx));
	}

	private PendingIntent getIntentRenovacao(Context ctx) {
		Intent i = new Intent();
		i.setAction(ACTION_RENOVAR_PLANO_DADOS);
		i.putExtra(EXTRA_AGENDADO, true);
		return PendingIntent.getBroadcast(ctx, 0, i,
				PendingIntent.FLAG_UPDATE_CURRENT);
	}

}
