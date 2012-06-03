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

package com.thiagovinicius.android.saldooi.util;

import java.util.Calendar;

import android.telephony.SmsManager;

public class Utils {

	// XXX Deveriamos bloquear o retorno até que a mensagem seja entregue?
	// até que ela seja enviada?
	public static void enviaMensagem(String destinatario, String texto) {
		SmsManager servicoSms = SmsManager.getDefault();
		servicoSms.sendTextMessage(destinatario, null, texto, null, null);
	}

	public static Calendar meiaNoite(Calendar hoje) {
		hoje = (Calendar) hoje.clone();
		hoje.set(Calendar.HOUR_OF_DAY, 0);
		hoje.set(Calendar.MINUTE, 0);
		hoje.set(Calendar.SECOND, 0);
		hoje.set(Calendar.MILLISECOND, 0);
		return hoje;
	}

}
