package com.thiagovinicius.android.saldooi.agendado;

import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.ACTION_ALTERA_ALARME;
import static com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes.EXTRA_HABILITAR;
import static com.thiagovinicius.android.saldooi.util.Utils.enviaMensagem;
import static com.thiagovinicius.android.saldooi.util.Utils.meiaNoite;

import java.sql.SQLException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.thiagovinicius.android.saldooi.R;
import com.thiagovinicius.android.saldooi.db.AuxiliarOrm;
import com.thiagovinicius.android.saldooi.db.PacoteDados;
import com.thiagovinicius.android.saldooi.db.PacoteDados.OrigemDados;
import com.thiagovinicius.android.saldooi.views.Preferencias;

public class RenovaPlanoDados extends BroadcastReceiver {

	public static final String ACTION_RENOVAR_PLANO_DADOS = RenovaPlanoDados.class
			.getCanonicalName() + ".ACTION_RENOVAR_PLANO_DADOS";
	public static final String EXTRA_AGENDADO = RenovaPlanoDados.class
			.getCanonicalName() + ".EXTRA_AGENDADO";

	private static final Logger logger = LoggerFactory
			.getLogger(RenovaPlanoDados.class.getSimpleName());

	private boolean agendamentoHabilitado(Context ctx) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getBoolean(Preferencias.CHAVE_DADOS_HABILITADO, false);
	}

	/**
	 * Recupera o identificador do plano de dados configurado, a partir das
	 * preferências.
	 * 
	 * @return
	 */
	private int getIdPlano(Context ctx) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		String result = pref.getString(Preferencias.CHAVE_DADOS_PLANO, "-1");
		return new Integer(result);
	}

	private String getTextoPlano(Context ctx, int idPlano) {
		String textosPlanos[] = ctx.getResources().getStringArray(
				R.array.renova_dados_texto_mensagem);
		return textosPlanos[idPlano];
	}

	private String getDestinatario(Context ctx, int idPlano) {
		String destinatarios[] = ctx.getResources().getStringArray(
				R.array.renova_dados_destinatarios);
		return destinatarios[idPlano];
	}

	private int getValidadeDias(Context ctx, int idPlano) {
		int validades[] = ctx.getResources().getIntArray(
				R.array.renova_dados_validade_dias);
		return validades[idPlano];
	}

	private void renovaPlano(Context ctx) {
		int idPlano = getIdPlano(ctx);
		if (idPlano == -1) {
			logger.info("Plano não selecionado. Não renovarei.");
			return;
		}
		String destinatario = getDestinatario(ctx, idPlano);
		String textoMensagem = getTextoPlano(ctx, idPlano);
		logger.info("Enviando mensagem. Destinatário: {}, Texto: {}",
				destinatario, textoMensagem);
		enviaMensagem(destinatario, textoMensagem);
		atualizaValidade(ctx, idPlano);
		programaProximaRenovacao(ctx);
	}

	private void atualizaValidade(Context ctx, int idPlano) {
		AuxiliarOrm db = OpenHelperManager.getHelper(ctx, AuxiliarOrm.class);

		try {
			Calendar hoje = Calendar.getInstance();
			Calendar validade = meiaNoite(hoje);
			validade.roll(Calendar.DAY_OF_YEAR, getValidadeDias(ctx, idPlano));

			PacoteDados novaValidade = new PacoteDados();
			novaValidade.dataInformacao = hoje.getTime();
			novaValidade.origem = OrigemDados.ESTIMATIVA;
			novaValidade.saldoBytes = 0;
			novaValidade.validade = validade.getTime();

			db.pacoteDados().create(novaValidade);

			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(ctx);
			long validadeAntiga = prefs.getLong(
					Preferencias.CHAVE_DADOS_VALIDADE, 0L);
			if (validade.getTimeInMillis() > validadeAntiga) {
				SharedPreferences.Editor ed = prefs.edit();
				ed.putLong(Preferencias.CHAVE_DADOS_VALIDADE,
						validade.getTimeInMillis());
				ed.commit();
			}

		} catch (SQLException ex) {
			logger.error("", ex);
		} finally {
			OpenHelperManager.releaseHelper();
		}

	}

	private void programaProximaRenovacao(Context ctx) {
		Intent i = new Intent();
		i.setAction(ACTION_ALTERA_ALARME);
		i.putExtra(EXTRA_HABILITAR, true);
		ctx.sendBroadcast(i);
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
