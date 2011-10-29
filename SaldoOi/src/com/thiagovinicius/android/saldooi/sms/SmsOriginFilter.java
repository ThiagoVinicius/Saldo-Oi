package com.thiagovinicius.android.saldooi.sms;


import static com.thiagovinicius.android.saldooi.sms.LeitorDados.ACTION_PROCESSAR_DADOS;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;

import com.thiagovinicius.android.saldooi.R;

public class SmsOriginFilter extends BroadcastReceiver {

	@Override
	public void onReceive(Context ctx, Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras == null)
            return;
        
        Set<String> remetentes = new HashSet<String>(Arrays.asList(
        	ctx.getResources().getStringArray(R.array.remetentes_conhecidos)));

        Object[] pdus = (Object[]) extras.get("pdus");

        for (int i = 0; i < pdus.length; i++) {
        	
        	byte mensagemRaw[] = (byte[]) pdus[i];
            SmsMessage message = SmsMessage.createFromPdu(mensagemRaw);
            String fromAddress = message.getOriginatingAddress();
            
            if (remetentes.contains(fromAddress)) {
            	Intent di = new Intent(ACTION_PROCESSAR_DADOS);
            	di.putExtra("mensagem", mensagemRaw);
            	ctx.sendBroadcast(di);
            }
            
        }
        
	}

}
