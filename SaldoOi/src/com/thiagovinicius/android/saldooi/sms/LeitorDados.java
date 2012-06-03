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

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.thiagovinicius.android.saldooi.db.AuxiliarOrm;
import com.thiagovinicius.android.saldooi.db.PacoteDados;
import com.thiagovinicius.android.saldooi.db.PacoteDados.OrigemDados;
import com.thiagovinicius.android.saldooi.util.Utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public class LeitorDados extends BroadcastReceiver {

	public static final String ACTION_PROCESSAR_DADOS = "com.thiagovinicius.android.saldooi.sms.LeitorDados.ACTION_PROCESSAR_DADOS";
	private static final Logger logger = LoggerFactory
			.getLogger(LeitorDados.class.getSimpleName());

	private static Date decodificaData(String dataStr) {
		Calendar cal = Calendar.getInstance();
		String campos[] = dataStr.split("/");
		cal.set(Calendar.DAY_OF_MONTH, new Integer(campos[0]));
		cal.set(Calendar.MONTH, new Integer(campos[1]) - 1);
		cal.set(Calendar.YEAR, new Integer(campos[2]));
		cal = Utils.meiaNoite(cal);
		return cal.getTime();
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
			processaAdesaoBonusInternet(ctx, texto);
			processaBonusInternetEsgotado(ctx, texto);
		}

	}

	private void processaSaldoPrincipal(String texto) {

		final Pattern padrao = Pattern
				.compile("\\QOi, seu saldo e R$ \\E(\\d+\\.\\d{2})\\Q. Seus creditos sao validos ate a zero hora de \\E(\\d+\\/\\d+/\\d+)\\Q. Obrigado\\E");

		Matcher comparador = padrao.matcher(texto);
		int saldo;
		Date data;

		if (comparador.matches()) {
			logger.debug("Mensagem de saldo detectada; {} grupos",
					comparador.groupCount());
			if (comparador.groupCount() == 2) {
				saldo = (int) (new Double(comparador.group(1)) * 100d);
				data = decodificaData(comparador.group(2));
				logger.info("Saldo: {} => {}", comparador.group(1), saldo);
				logger.info("Validade: {} => {}", comparador.group(2), data);
			}
		}

	}

	private void processaAdesaoBonusInternet(Context ctx, String texto) {

		final Pattern padrao = Pattern
				.compile("\\QParabens! Por apenas R$ \\E\\d+[\\.,]\\d{2}\\Q voce comprou \\E(\\d+)\\Q MB em internet validos ate \\E(\\d+\\/\\d+/\\d+)!");

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

					saldo = new Integer(comparador.group(1));
					validade = decodificaData(comparador.group(2));

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
				PacoteDados novaInfo = new PacoteDados();

				novaInfo.dataInformacao = Calendar.getInstance().getTime();
				novaInfo.origem = OrigemDados.SMS;
				novaInfo.saldoBytes = 0;
				novaInfo.validade = infoAtual.validade;

				novaInfo.persiste(ctx, db.pacoteDados());

			} catch (SQLException ex) {
				logger.error("", ex);
			} finally {
				OpenHelperManager.releaseHelper();
			}

		}

	}

}
