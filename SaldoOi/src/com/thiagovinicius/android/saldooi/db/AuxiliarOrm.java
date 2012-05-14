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

package com.thiagovinicius.android.saldooi.db;

import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

/**
 * @author thiago
 *
 */
public class AuxiliarOrm extends OrmLiteSqliteOpenHelper {

	private static final Logger logger = LoggerFactory
			.getLogger(AuxiliarOrm.class.getSimpleName());

	private static final String NOME_BANCO = "saldo-oi.db";
	private static final int VERSAO_BANCO = 1;

	private Dao<PacoteDados, Long> daoPacoteDados = null;

	public AuxiliarOrm(Context context) {
		super(context, NOME_BANCO, null, VERSAO_BANCO);
	}

	@Override
	public void onCreate(SQLiteDatabase database,
			ConnectionSource connectionSource) {

		logger.info("{}.onCreate().", AuxiliarOrm.class.getSimpleName());

		try {
			logger.info("Criando tabela: {}", PacoteDados.class.getSimpleName());
			TableUtils.createTable(connectionSource, PacoteDados.class);
		} catch (SQLException e) {
			logger.error("Falha ao criar tabelas", e);
		}

	}

	@Override
	public void onUpgrade(SQLiteDatabase database,
			ConnectionSource connectionSource, int oldVersion, int newVersion) {

		logger.info("{}.onUpgrade()", AuxiliarOrm.class.getSimpleName());
		logger.info("Não temos nada a atualizar =).");

	}

	public synchronized Dao<PacoteDados, Long> pacoteDados()
			throws SQLException {
		if (daoPacoteDados == null) {
			daoPacoteDados = getDao(PacoteDados.class);
		}
		return daoPacoteDados;
	}

}
