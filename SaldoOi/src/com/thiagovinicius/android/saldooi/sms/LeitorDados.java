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

package com.thiagovinicius.android.saldooi.sms;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.thiagovinicius.android.saldooi.db.AuxiliarOrm;
import com.thiagovinicius.android.saldooi.db.PacoteDados;
import com.thiagovinicius.android.saldooi.db.PacoteDados.OrigemDados;
import com.thiagovinicius.android.saldooi.util.Utils;

public class LeitorDados extends BroadcastReceiver {

	public static final String ACTION_PROCESSAR_DADOS = "com.thiagovinicius.android.saldooi.sms.LeitorDados.ACTION_PROCESSAR_DADOS";
	private static final Logger logger = LoggerFactory
			.getLogger(LeitorDados.class.getSimpleName());

	private static Calendar decodificaData(String dataStr) {
		Calendar cal = Calendar.getInstance();
		String campos[] = dataStr.split("/");
		cal.set(Calendar.DAY_OF_MONTH, Integer.valueOf(campos[0]));
		cal.set(Calendar.MONTH, Integer.valueOf(campos[1]) - 1);
		cal.set(Calendar.YEAR, Integer.valueOf(campos[2]));
		return cal;
	}

	/**
	 * Utilize este, quando a data se referir à "zero hora de <i>data</i>".
	 */
	private static Date decodificaDataMesmoDia(String dataStr) {
		return Utils.meiaNoite(decodificaData(dataStr)).getTime();
	}

	/**
	 * Utilize este, quando a data se referir à "até <i>data</i>", ou seja,
	 * meia noite do dia seguinte.
	 */
	private static Date decodificaDataDiaSeguinte(String dataStr) {
		Calendar cal = decodificaData(dataStr);
		cal.roll(Calendar.DAY_OF_MONTH, true);
		return Utils.meiaNoite(cal).getTime();
	}

	@Override
	public void onReceive(Context ctx, Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras == null)
			return;

		byte[] mensagemRaw = (byte[]) extras.get("mensagem");
		SmsMessage mensagem = SmsMessage.createFromPdu(mensagemRaw);
		String texto = mensagem.getMessageBody();

		if (texto != null) {
			logger.debug("Recebida mensagem: \"{}\"", texto);
			processaSaldoPrincipal(texto);
			processaBonusInternetAdesao(ctx, texto);
			processaBonusInternetEsgotado(ctx, texto);
			processaBonusInternetSaldo(ctx, texto);
		}

	}

	private void processaSaldoPrincipal(String texto) {

		final Pattern padrao = Pattern
				.compile("\\QOi, seu saldo e R$ \\E(\\d+[\\.,]\\d{2})\\Q. Seus creditos sao validos ate a zero hora de \\E(\\d+\\/\\d+/\\d+)\\Q. Obrigado\\E");

		Matcher comparador = padrao.matcher(texto);
		int saldo;
		Date data;

		if (comparador.matches()) {
			logger.debug("Mensagem de saldo detectada; {} grupos",
					comparador.groupCount());
			if (comparador.groupCount() == 2) {
				saldo = (int) (Double.valueOf(comparador.group(1).replace(',',
						'.')) * 100d);
				data = decodificaDataMesmoDia(comparador.group(2));
				logger.info("Saldo: {} => {}", comparador.group(1), saldo);
				logger.info("Validade: {} => {}", comparador.group(2), data);
			}
		}

	}

	private void processaBonusInternetAdesao(Context ctx, String texto) {

		final Pattern padrao = Pattern
				.compile("\\QParabens! Por apenas R$ \\E\\d+[\\.,]\\d{2}\\Q voce comprou \\E(\\d+)\\Q MB em Internet validos ate \\E(\\d+\\/\\d+/\\d+)!");

		Matcher comparador = padrao.matcher(texto);
		int saldo;
		Date validade;

		if (comparador.matches()) {
			logger.debug("Mensagem de compra de bonus de internet; {} grupos",
					comparador.groupCount());
			if (comparador.groupCount() == 2) {
				AuxiliarOrm db = OpenHelperManager.getHelper(ctx,
						AuxiliarOrm.class);

				try {

					saldo = Integer.valueOf(comparador.group(1));
					validade = decodificaDataDiaSeguinte(comparador.group(2));

					PacoteDados entrada = new PacoteDados();
					entrada.dataInformacao = Calendar.getInstance().getTime();
					entrada.origem = OrigemDados.SMS;
					entrada.saldoBytes = saldo * 1024 * 1024;
					entrada.validade = validade;

					entrada.persiste(ctx, db.pacoteDados());

				} catch (SQLException ex) {
					logger.error("", ex);
				} finally {
					OpenHelperManager.releaseHelper();
				}

			}
		}

	}

	private void processaBonusInternetEsgotado(Context ctx, String texto) {

		final String padrao = "Voce consumiu todo pacote de internet no "
				+ "celular. Sua velocidade sera reduzida. Pra continuar "
				+ "navegando com velocidade maior, ligue*880 e compre um "
				+ "novo pacote";

		if (padrao.equals(texto)) {

			logger.debug("Mensagem de bonus de internet esgotado.");

			AuxiliarOrm db = OpenHelperManager
					.getHelper(ctx, AuxiliarOrm.class);

			try {

				PacoteDados infoAtual = PacoteDados.encontraMaiorValidade(db
						.pacoteDados());

				if (infoAtual != null) {
					PacoteDados novaInfo = new PacoteDados();

					novaInfo.dataInformacao = Calendar.getInstance().getTime();
					novaInfo.origem = OrigemDados.SMS;
					novaInfo.saldoBytes = 0;
					novaInfo.validade = infoAtual.validade;

					novaInfo.persiste(ctx, db.pacoteDados());
				}

			} catch (SQLException ex) {
				logger.error("", ex);
			} finally {
				OpenHelperManager.releaseHelper();
			}

		}

	}

	private void processaBonusInternetSaldo(Context ctx, String texto) {

		final Pattern padrao = Pattern
				.compile("\\QSeu saldo promocional de internet é de \\E(\\d+[\\.,]\\d+)\\Q MB com data de validade até \\E(\\d+\\/\\d+/\\d+)\\.");

		Matcher comparador = padrao.matcher(texto);
		double saldo;
		Date validade;

		if (comparador.matches()) {
			logger.debug("Mensagem de saldo de bonus de internet; {} grupos",
					comparador.groupCount());
			if (comparador.groupCount() == 2) {
				AuxiliarOrm db = OpenHelperManager.getHelper(ctx,
						AuxiliarOrm.class);

				try {

					saldo = Double.valueOf(comparador.group(1)
							.replace(',', '.'));
					validade = decodificaDataDiaSeguinte(comparador.group(2));

					PacoteDados entrada = new PacoteDados();
					entrada.dataInformacao = Calendar.getInstance().getTime();
					entrada.origem = OrigemDados.SMS;
					entrada.saldoBytes = Math.round(saldo * 1024d * 1024d);
					entrada.validade = validade;

					entrada.persiste(ctx, db.pacoteDados());

				} catch (SQLException ex) {
					logger.error("", ex);
				} finally {
					OpenHelperManager.releaseHelper();
				}

			}
		}

	}

}
