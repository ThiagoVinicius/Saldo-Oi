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

package com.thiagovinicius.android.saldooi.db;

import java.sql.SQLException;
import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.thiagovinicius.android.saldooi.agendado.ProgramaAlarmes;
import com.thiagovinicius.android.saldooi.views.PlanoDados;

/**
 * @author thiago
 *
 */
@DatabaseTable(tableName = "pacote-dados")
public class PacoteDados {

	public enum OrigemDados {
		/** Estimativa feita pela aplicação */
		ESTIMATIVA,
		/** Obtido a partir de um SMS enviado pela operadora */
		SMS,
	};

	@DatabaseField(columnName = "chave", generatedId = true)
	public long chave;

	/** representa o momento em que o plano de dados perde a validade. */
	@DatabaseField(columnName = "validade", dataType = DataType.DATE_LONG)
	public Date validade;

	/** representa o momento em que as informações nesse objeto foram obtidas. */
	@DatabaseField(columnName = "data-informacao", dataType = DataType.DATE_LONG)
	public Date dataInformacao;

	/**
	 * representa o saldo em bytes, no momento representado por 'dataInformacao'
	 */
	@DatabaseField(columnName = "saldoBytes")
	public long saldoBytes;

	@DatabaseField(columnName = "origem", dataType = DataType.ENUM_STRING)
	public OrigemDados origem;

	public PacoteDados() { // Não remova o construtor padrão.
	}

	public void persiste(Context ctx, Dao<PacoteDados, Long> dao)
			throws SQLException {

		dao.create(this);

		SharedPreferences prefs = PreferenceManager
				.getDefaultSharedPreferences(ctx);
		long validadeAntiga = prefs.getLong(PlanoDados.CHAVE_VALIDADE, 0L);
		if (validade.getTime() > validadeAntiga) {
			ProgramaAlarmes.atualizaAlarme(ctx);
			SharedPreferences.Editor ed = prefs.edit();
			ed.putLong(PlanoDados.CHAVE_VALIDADE, validade.getTime());
			ed.commit();
		}

	}

	public static PacoteDados encontraMaiorValidade(Dao<PacoteDados, Long> dao)
			throws SQLException {
		return dao.queryForFirst(dao.queryBuilder().orderBy("validade", false)
				.prepare());
	}

}
