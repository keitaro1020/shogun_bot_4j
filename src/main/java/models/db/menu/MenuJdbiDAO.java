package models.db.menu;

import java.util.Date;
import java.util.List;

import org.skife.jdbi.v2.sqlobject.Bind;
import org.skife.jdbi.v2.sqlobject.BindBean;
import org.skife.jdbi.v2.sqlobject.SqlQuery;
import org.skife.jdbi.v2.sqlobject.SqlUpdate;
import org.skife.jdbi.v2.sqlobject.customizers.Define;
import org.skife.jdbi.v2.sqlobject.customizers.Mapper;

public interface MenuJdbiDAO {

	@SqlUpdate("create table if not exists menu ("
			+ "id int(10) not null auto_increment, "
			+ "menuDate date not null, "
			+ "name varchar(100) default null, "
			+ "price int(10) default 0, "
			+ "image varchar(500) default null "
			+ ");")
	void createRelationTable();

	@SqlUpdate("insert into menu(menuDate, name, price, image) values (:menuDate, :name, :price, :image)")
	public void insert(@BindBean Menu menu);

	@SqlUpdate("delete from menu where menuDate = :menuDate")
	public void deleteByMenuDate(@Bind("menuDate") Date menuDate);

	@SqlQuery("select * from menu where id = :id")
	@Mapper(MenuJdbiMapper.class)
	public Menu findById(@Bind("id") Long id);

	@SqlQuery("select * from menu")
	@Mapper(MenuJdbiMapper.class)
	public List<Menu> findAll();

	@SqlQuery("select * from menu where menuDate = :menuDate")
	@Mapper(MenuJdbiMapper.class)
	public List<Menu> findByMenuDate(@Bind("menuDate") Date menuDate);

    @SqlQuery("<query> --comment\\<>")
    public String query(@Define("query") String query);

	void close();
}
