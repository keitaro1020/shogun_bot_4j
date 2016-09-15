package models.db.menu;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.skife.jdbi.v2.StatementContext;
import org.skife.jdbi.v2.tweak.ResultSetMapper;

public class MenuJdbiMapper implements ResultSetMapper<Menu> {

	@Override
	public Menu map(int index, ResultSet r, StatementContext ctx)
			throws SQLException {

		final Menu menu	= new Menu();
		menu.setId(r.getLong("id"));
		menu.setMenuDate(r.getDate("menuDate"));
		menu.setName(r.getString("name"));
		menu.setPrice(r.getInt("price"));
		menu.setImage(r.getString("image"));
		return menu;
	}

}
