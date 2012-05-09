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
 *  along with Foobar.  If not, see <http://www.gnu.org/licenses/>.
 *
*/

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
