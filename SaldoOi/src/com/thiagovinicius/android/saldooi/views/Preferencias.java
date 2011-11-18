package com.thiagovinicius.android.saldooi.views;

import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.ACTION_ALTERA_ALARME;
import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.EXTRA_HABILITAR;

import com.thiagovinicius.android.saldooi.R;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferencias extends PreferenceActivity 
implements OnSharedPreferenceChangeListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		addPreferencesFromResource(R.xml.preferencias);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		getPreferenceScreen().getSharedPreferences().
			registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		getPreferenceScreen().getSharedPreferences().
			unregisterOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences prefs, String chave) {
		if ("renova_dados_tipo".equals(chave) || 
			"renova_dados_habilitado".equals(chave)) {
			
			Intent i = new Intent();
			i.setAction(ACTION_ALTERA_ALARME);
			i.putExtra(EXTRA_HABILITAR, 
					prefs.getBoolean("renova_dados_habilitado", false));
			sendBroadcast(i);
			
		}
	}
	
}
