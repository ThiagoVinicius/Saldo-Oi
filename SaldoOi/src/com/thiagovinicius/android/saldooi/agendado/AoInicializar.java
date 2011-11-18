package com.thiagovinicius.android.saldooi.agendado;

import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.ACTION_ALTERA_ALARME;
import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.EXTRA_HABILITAR;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class AoInicializar extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			SharedPreferences prefs = 
				PreferenceManager.getDefaultSharedPreferences(ctx);
			
			Intent i = new Intent();
			i.setAction(ACTION_ALTERA_ALARME);
			i.putExtra(EXTRA_HABILITAR, 
					prefs.getBoolean("renova_dados_habilitado", false));
			ctx.sendBroadcast(i);
		}
	}

}
