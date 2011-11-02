package com.thiagovinicius.android.saldooi.agendado;

import static com.thiagovinicius.android.saldooi.util.Utils.desabilitaComponente;
import static com.thiagovinicius.android.saldooi.util.Utils.enviaMensagem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

import com.thiagovinicius.android.saldooi.R;

public class RenovaPlanoDados extends BroadcastReceiver {
	
	public static final String ACTION_RENOVAR_PLANO_DADOS = 
		RenovaPlanoDados.class.getCanonicalName() + ".ACTION_RENOVAR_PLANO_DADOS";
	
	private static final Logger logger = LoggerFactory.getLogger(RenovaPlanoDados.class);

	//TODO
	private boolean isEnabled() {
		return true;
	}
	
	/**
	 * Recupera o identificador do plano de dados configurado, a partir do banco
	 * de dados -- ou o que quer que seja utilizado quando essa app estiver 
	 * pronta. 
	 *  
	 * @return
	 */
	//TODO
	private int getIdPlano () {
		return 0;
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
		int idPlano = getIdPlano();
		String destinatario = getDestinatario(ctx, idPlano);
		String textoMensagem = getTextoPlano(ctx, idPlano);
		logger.info("Enviando mensagem. Destinatário: {}, Texto: {}",
				destinatario, textoMensagem);
		enviaMensagem(destinatario, textoMensagem);
	}
	
	//XXX talvez seja necessario implementar uma especie de ONE_SHOT aqui, 
	//    para o caso de haver um botao "Renovar agora", em alguma activity
	//    futura.
	@Override
	public void onReceive(Context ctx, Intent intent) {
		if (isEnabled()) {
			logger.info("Renovando plano de dados.");
			renovaPlano(ctx);
		} else {
			//XXX avaliar a alternativa de desabilitar o alarme que envia o Intent
			logger.info("Funcionalidade desabilitada pelo usuário. Desabilitando componente.");
			desabilitaComponente(
					ctx.getPackageManager(), 
					new ComponentName(ctx, getClass()));
		}
	}

}
