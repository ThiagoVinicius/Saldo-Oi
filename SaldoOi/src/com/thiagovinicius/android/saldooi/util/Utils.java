package com.thiagovinicius.android.saldooi.util;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.telephony.SmsManager;

public class Utils {
	
	//private static final Logger logger = LoggerFactory.getLogger(Utils.class);
	
	public static void desabilitaComponente (PackageManager pm, 
			ComponentName componente) {
		
		pm.setComponentEnabledSetting(componente, 
				PackageManager.COMPONENT_ENABLED_STATE_DISABLED, 
				PackageManager.DONT_KILL_APP);
	}
	
	
	//XXX Deveriamos bloquear o retorno até que a mensagem seja entregue?
	//    até que ela seja enviada?
	public static void enviaMensagem (String destinatario, String texto) {
		SmsManager servicoSms = SmsManager.getDefault();
		servicoSms.sendTextMessage(destinatario, null, texto, null, null);
	}
	
}
