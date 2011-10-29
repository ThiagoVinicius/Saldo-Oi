package com.thiagovinicius.android.saldooi.sms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.telephony.SmsMessage;

public class SaldoPrincipal extends LeitorDados {

	private static final Pattern padrao = Pattern.compile("\\QOi, seu saldo e R$ \\E(\\d+[.]\\d{2})\\Q. Seus creditos sao validos ate a zero hora de \\E(\\d+\\/\\d+/\\d+)\\Q. Obrigado\\E");
	private static final Logger logger = LoggerFactory.getLogger(SaldoPrincipal.class);
	
	@Override
	public void processaMensagem(SmsMessage mensagem) {
		String texto = mensagem.getMessageBody();
		Matcher comparador = padrao.matcher(texto);
		int saldo;
		String data;
		
		logger.debug("Recebida mensagem: \"{}\"", texto);
		if (comparador.matches()) {
			logger.debug("Ladies and gentleman, we got a match! ({} groups)", comparador.groupCount());
			if (comparador.groupCount() == 2) {
				saldo = (int) (new Double(comparador.group(1)) / 100d);
				data = comparador.group(2);
				logger.info("Saldo: {} => {}", comparador.group(1), saldo);
				logger.info("Validade: {} => {}", comparador.group(2), data);
			}
		}
	}

}
