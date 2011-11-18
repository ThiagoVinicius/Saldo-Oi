package com.thiagovinicius.android.saldooi.agendado;

import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.ACTION_ALTERA_ALARME;
import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.EXTRA_HABILITAR;
import static com.thiagovinicius.android.saldooi.util.Utils.enviaMensagem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.thiagovinicius.android.saldooi.R;

public class RenovaPlanoDados extends BroadcastReceiver {
	
	public static final String ACTION_RENOVAR_PLANO_DADOS = 
		RenovaPlanoDados.class.getCanonicalName() + ".ACTION_RENOVAR_PLANO_DADOS";
	public static final String EXTRA_AGENDADO = 
		RenovaPlanoDados.class.getCanonicalName() + ".EXTRA_AGENDADO";
	
	
	private static final Logger logger = LoggerFactory.getLogger(RenovaPlanoDados.class.getSimpleName());

	private boolean agendamentoHabilitado(Context ctx) {
		SharedPreferences pref = 
			PreferenceManager.getDefaultSharedPreferences(ctx);
		return pref.getBoolean("renova_dados_habilitado", false);
	}
	
	/**
	 * Recupera o identificador do plano de dados configurado, a partir das
	 * preferências. 
	 *  
	 * @return 
	 */
	private int getIdPlano (Context ctx) {
		SharedPreferences pref = 
			PreferenceManager.getDefaultSharedPreferences(ctx);
		String result = pref.getString("renova_dados_tipo", "0");
		return new Integer(result);
	}
	
	private String getTextoPlano (Context ctx, int idPlano) {
		String textosPlanos[] = ctx.getResources().
			getStringArray(R.array.renova_dados_texto_mensagem);
		return textosPlanos[idPlano];
	}
	
	private String getDestinatario (Context ctx, int idPlano) {
		String destinatarios[] = ctx.getResources().
			getStringArray(R.array.renova_dados_destinatarios);
		return destinatarios[idPlano];
	}
	
	private void renovaPlano (Context ctx) {
		int idPlano = getIdPlano(ctx);
		String destinatario = getDestinatario(ctx, idPlano);
		String textoMensagem = getTextoPlano(ctx, idPlano);
		logger.info("Enviando mensagem. Destinatário: {}, Texto: {}",
				destinatario, textoMensagem);
		enviaMensagem(destinatario, textoMensagem);
	}
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
		
		if (ACTION_RENOVAR_PLANO_DADOS.equals(intent.getAction()) == false) {
			return;
		}
		
		if (intent.getBooleanExtra(EXTRA_AGENDADO, false)) {
			
			if (agendamentoHabilitado(ctx)) {
				logger.info("Renovação disparada por alarme.");
			} else {
				logger.info("Renovação disparada por alarme, porém desabilitada.");
				Intent desabilitarAlarme = new Intent();
				desabilitarAlarme.setAction(ACTION_ALTERA_ALARME);
				desabilitarAlarme.putExtra(EXTRA_HABILITAR, false);
				ctx.sendBroadcast(desabilitarAlarme);
				return;
			}
			
		}
		
		logger.info("Renovando plano de dados.");
		renovaPlano(ctx);
		
	}

}
