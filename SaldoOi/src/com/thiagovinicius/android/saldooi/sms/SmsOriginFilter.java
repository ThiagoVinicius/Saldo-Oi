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
