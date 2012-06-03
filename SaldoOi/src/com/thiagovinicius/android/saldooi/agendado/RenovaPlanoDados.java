package com.thiagovinicius.android.saldooi.agendado;

import static com.thiagovinicius.android.saldooi.util.Utils.enviaMensagem;
import static com.thiagovinicius.android.saldooi.util.Utils.meiaNoite;

import java.sql.SQLException;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import com.thiagovinicius.android.saldooi.views.PlanoDados;

public class RenovaPlanoDados extends BroadcastReceiver {

	public static final String ACTION_RENOVAR_PLANO_DADOS = RenovaPlanoDados.class
			.getCanonicalName() + ".ACTION_RENOVAR_PLANO_DADOS";
	public static final String EXTRA_AGENDADO = RenovaPlanoDados.class
			.getCanonicalName() + ".EXTRA_AGENDADO";

	private static final Logger logger = LoggerFactory
			.getLogger(RenovaPlanoDados.class.getSimpleName());

	public static void renovaImediatamente(Context ctx) {
		Intent i = new Intent();
		i.setAction(ACTION_RENOVAR_PLANO_DADOS);
		i.putExtra(EXTRA_AGENDADO, false);
		ctx.sendBroadcast(i);
	}

	public static void agendaRenovacao(Context ctx) {
		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		ProgramaAlarmes.alteraAlarme(
				ctx,
				prefs.getBoolean(PlanoDados.CHAVE_HABILITADO, false)
						&& prefs.contains(PlanoDados.CHAVE_PLANO));
	}

	private boolean agendamentoHabilitado(Context ctx) {
		SharedPreferences pref = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		return pref.getBoolean(PlanoDados.CHAVE_HABILITADO, false);
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
		String result = pref.getString(PlanoDados.CHAVE_PLANO, "-1");
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

			novaValidade.persiste(ctx, db.pacoteDados());

		} catch (SQLException ex) {
			logger.error("", ex);
		} finally {
			OpenHelperManager.releaseHelper();
		}

	}

	private void programaProximaRenovacao(Context ctx) {
		RenovaPlanoDados.agendaRenovacao(ctx);
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
				ProgramaAlarmes.alteraAlarme(ctx, false);
				return;
			}

		}

		logger.info("Renovando plano de dados.");
		renovaPlano(ctx);

	}

	private static class ProgramaAlarmes {

		private static final Logger logger = LoggerFactory
				.getLogger(ProgramaAlarmes.class.getSimpleName());

		public static void alteraAlarme(Context ctx, boolean habilitar) {
			AlarmManager am = (AlarmManager) ctx
					.getSystemService(Context.ALARM_SERVICE);
			SharedPreferences prefs = PreferenceManager
					.getDefaultSharedPreferences(ctx);

			if (habilitar) {
				ProgramaAlarmes.habilitaRenovacao(ctx, prefs, am);
			} else {
				ProgramaAlarmes.desabilitaRenovacao(ctx, prefs, am);
			}
		}

		private static Calendar dataValidade(Context ctx) throws SQLException {
			Calendar dataAlvo = null;
			AuxiliarOrm db = OpenHelperManager
					.getHelper(ctx, AuxiliarOrm.class);

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
			// Nesse momento, a data está correta, resta apenas ajustar o
			// horário

			horaAlvo.set(Calendar.HOUR_OF_DAY, 0);
			horaAlvo.set(Calendar.MINUTE, 10);
			horaAlvo.set(Calendar.SECOND, 0);
			horaAlvo.set(Calendar.MILLISECOND, 0);

			if (horaAlvo.before(Calendar.getInstance())) {
				// Temos que renovar hoje, porém já passamos da hora.
				// Se não fizermos nada aqui, o alarme será programado no
				// passado,
				// e portanto, executado imediatamente.
			}

			return horaAlvo;
		}

		private static void habilitaRenovacao(Context ctx,
				SharedPreferences prefs, AlarmManager am) {
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
				ed.putLong(PlanoDados.CHAVE_AGENDADO,
						horaAlvo.getTimeInMillis());
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

}
