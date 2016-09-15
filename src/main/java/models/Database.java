package models;

import java.util.concurrent.atomic.AtomicReference;

import lombok.extern.slf4j.Slf4j;
import models.db.menu.MenuJdbiDAO;

import org.h2.jdbcx.JdbcConnectionPool;
import org.skife.jdbi.v2.DBI;

@Slf4j
public class Database {
	private static AtomicReference<JdbcConnectionPool> ds = new AtomicReference<>();

	/**
	 * データベースの初期化
	 */
	public static void initialize() {
		log.info("database initialize.");

		ds.set(createConnection());
		final DBI dbi = new DBI(ds.get());

		final MenuJdbiDAO menuDao	= dbi.open(MenuJdbiDAO.class);
		menuDao.createRelationTable();
		menuDao.close();

		log.info("database initialized.");
	}

	/**
	 * データベースへの接続を確立する
	 */
	private static JdbcConnectionPool createConnection() {
		final ConfigReader reader = ConfigReader.getInstance();
		return JdbcConnectionPool.create(reader.getDatabaseURI(), reader.getDatabaseId(), reader.getDatabasePassword());
	}

	public static void dispose() {
		ds.get().dispose();
	}

	public static MenuJdbiDAO getMenuDAO()
	{
		final DBI dbi = new DBI(ds.get());
		final MenuJdbiDAO menuDao	= dbi.open(MenuJdbiDAO.class);

		return menuDao;
	}
}
