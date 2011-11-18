package com.thiagovinicius.android.saldooi.util;

import android.telephony.SmsManager;

public class Utils {
	
	//XXX Deveriamos bloquear o retorno até que a mensagem seja entregue?
	//    até que ela seja enviada?
	public static void enviaMensagem (String destinatario, String texto) {
		SmsManager servicoSms = SmsManager.getDefault();
		servicoSms.sendTextMessage(destinatario, null, texto, null, null);
	}
	
}
