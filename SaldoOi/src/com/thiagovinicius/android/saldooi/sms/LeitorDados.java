package com.thiagovinicius.android.saldooi.sms;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

public abstract class LeitorDados extends BroadcastReceiver {

	public static final String ACTION_PROCESSAR_DADOS = "com.thiagovinicius.android.saldooi.sms.LeitorDados.ACTION_PROCESSAR_DADOS";
	
	@Override
	public void onReceive(Context ctx, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        
        byte[] mensagemRaw = (byte[]) extras.get("mensagem");
        SmsMessage mensagem = SmsMessage.createFromPdu(mensagemRaw);
        
        if (mensagem.getMessageBody() != null) {
        	processaMensagem(mensagem);
        }
        
        
	}
	
	public abstract void processaMensagem (SmsMessage mensagem);

}
