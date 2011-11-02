package com.thiagovinicius.android.saldooi.agendado;

import static com.thiagovinicius.android.saldooi.agendado.RenovaPlanoDados.ACTION_RENOVAR_PLANO_DADOS;

import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ProgramaAlarmes extends BroadcastReceiver {
	
	private static final Logger logger = LoggerFactory.getLogger(ProgramaAlarmes.class);
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		logger.info("Programando renovação de saldo.");
		Calendar horaAlvo = Calendar.getInstance();
		horaAlvo.set(Calendar.HOUR_OF_DAY, 1);
		horaAlvo.set(Calendar.MINUTE, 0);
		horaAlvo.set(Calendar.SECOND, 0);
		horaAlvo.set(Calendar.MILLISECOND, 0);
		while (horaAlvo.before(new Date())) { // Já é depois de uma da manhã
			horaAlvo.roll(Calendar.DAY_OF_YEAR, true);
		}
		logger.info("Renovação de saldo programada para {}", horaAlvo);
		AlarmManager am = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		am.setRepeating(AlarmManager.RTC, horaAlvo.getTimeInMillis(), 
				AlarmManager.INTERVAL_DAY, PendingIntent.getBroadcast(ctx, 0, 
						new Intent(ACTION_RENOVAR_PLANO_DADOS), 
						PendingIntent.FLAG_UPDATE_CURRENT));
	}

}
