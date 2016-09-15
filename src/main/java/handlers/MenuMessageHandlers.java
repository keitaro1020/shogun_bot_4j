package handlers;

import it.sauronsoftware.cron4j.Scheduler;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import models.ConfigReader;
import models.Database;
import models.MenuParser;
import models.MenuParser.MenuEntity;
import models.db.menu.Menu;
import models.db.menu.MenuJdbiDAO;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;
import com.github.masahitojp.botan.message.BotanMessageSimple;

@Slf4j
public class MenuMessageHandlers implements BotanMessageHandlers {

    private Scheduler scheduler;

	@Override
	public final void initialize(final Robot robot) {
		scheduler = new Scheduler();
		// start cron4j scheduler.
		scheduler.start();
	}

	@Override
	public void register(Robot robot) {

		try
		{
			robot.respond("menu_all", "all menu list(csv)", message ->{

				log.info("menu_all");
				MenuJdbiDAO menuDao	= Database.getMenuDAO();
				List<Menu> menuList	= menuDao.findAll();

				if(menuList.size() > 0) {
					final StringBuilder sb	= new StringBuilder();
					menuList.stream().forEach(menu ->{
						sb
						.append("id:" + menu.getId() + ",")
						.append("menuDate:" + menu.getMenuDate() + ",")
						.append("name:" + menu.getName() + ",")
						.append("price:" + menu.getPrice() + ",")
						.append("image:" + menu.getImage())
						.append(System.getProperty("line.separator"))
						;
					});

					message.reply(sb.toString());
				}else{
					message.reply("menu not found");
				}
			});

			robot.respond("menu_setup", "menu setup", message -> {

				log.info("menu_setup");
				try {
					setupMenu();
					message.reply("setup finish!");

				} catch (Exception e) {
					log.trace(e.getMessage(), e);
					message.reply("error ! : " + e.getMessage());

					return;
				}
			});

			robot.respond("(menu) (?<body>.+)", "get menu date(yyyy-MM-dd)", message ->{

				final String dateStr	= message.getMatcher().group("body");

				try
				{
					final String menuStr	= getMenuString(dateStr);
					message.reply(menuStr);
				}
				catch(DateTimeParseException e)
				{
					log.trace(e.getMessage(), e);
					message.reply("日付はyyyy-MM-dd形式で指定して下さい");

					return;
				}
			});

			scheduler.schedule("40 9 * * 1-5", () -> {
				try {
					setupMenu();
				} catch (Exception e) {
					log.trace(e.getMessage(), e);
				}
			});
			scheduler.schedule("0 12 * * 1-5", () -> {
				try {
					final String menuStr	= getMenuString(MENU_TARGET_TODAY);
					robot.send(new BotanMessageSimple("@channel: " + menuStr, "bento_shogun"));
				} catch (Exception e) {
					log.trace(e.getMessage(), e);
				}
			});
			scheduler.schedule("0 17 * * 0-4", () -> {
				try {
					final String menuStr	= getMenuString(MENU_TARGET_TOMORROW);
					robot.send(new BotanMessageSimple("@channel: " + menuStr, "bento_shogun"));
				} catch (Exception e) {
					log.trace(e.getMessage(), e);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			log.trace(e.getMessage(), e);

			throw e;
		}
	}

	private void setupMenu() throws Exception
	{
		final DateTimeFormatter formatter		= DateTimeFormatter.ISO_LOCAL_DATE;
		final List<MenuEntity> menuEntityList	= MenuParser.parseMenu("http://bento-shogun.jp/menu/json/week.json");

		if(menuEntityList != null && menuEntityList.size() > 0)
		{
			MenuJdbiDAO menuDao	= Database.getMenuDAO();
			for(MenuEntity menuEntity : menuEntityList)
			{
				final String baseDateStr	= menuEntity.getDate();
				final LocalDate baseDate	= LocalDate.parse(baseDateStr, formatter);

				setupMenuSub(menuDao, menuEntity.getMon(), baseDate, 0);
				setupMenuSub(menuDao, menuEntity.getTue(), baseDate, 1);
				setupMenuSub(menuDao, menuEntity.getWed(), baseDate, 2);
				setupMenuSub(menuDao, menuEntity.getThu(), baseDate, 3);
				setupMenuSub(menuDao, menuEntity.getFri(), baseDate, 4);
			}
		}
	}

	private void setupMenuSub(MenuJdbiDAO menuDao, List<models.MenuParser.Menu> orgMenuList, LocalDate baseDate, int plusDay)
	{
		if(orgMenuList == null) return;

		final Date menuDate	= convertLocalDateToOldDate(baseDate.plusDays(plusDay));
		menuDao.deleteByMenuDate(menuDate);

		orgMenuList.stream().forEach(_menu -> {
			final Menu menu	= new Menu();
			menu.setMenuDate(menuDate);
			menu.setName(_menu.getName());
			menu.setPrice(Integer.parseInt(_menu.getPrice()));

			String image	= _menu.getImage() != null ? _menu.getImage().length > 0 ? _menu.getImage()[0] : null : null;
			menu.setImage(image);

			menuDao.insert(menu);
		});
	}

	private String getMenuString(String target)
	{
		final DateTimeFormatter formatter	= DateTimeFormatter.ISO_LOCAL_DATE;
		LocalDate targetDate	= null;
		if(MENU_TARGET_TODAY.equals(target))
		{
			targetDate		= LocalDate.now();
		}
		else if(MENU_TARGET_TOMORROW.equals(target))
		{
			targetDate		= LocalDate.now().plusDays(1);
		}
		else
		{
			targetDate	= LocalDate.parse(target, formatter);
		}

		MenuJdbiDAO menuDao	= Database.getMenuDAO();
		List<Menu> menuList	= menuDao.findByMenuDate(convertLocalDateToOldDate(targetDate));

		final StringBuilder sb	= new StringBuilder();
		sb.append(targetDate).append("のお品書きにござる").append(ConfigReader.getInstance().getShogunIcon()).append(System.getProperty("line.separator"));

		if(menuList.size() > 0)
		{
			menuList.stream().forEach(_menu -> {
				sb
				.append("・" + _menu.getName())
				.append("[" + _menu.getPrice() + "円]")
				.append(System.getProperty("line.separator"))
				.append("http://bento-shogun.jp/menu/" + _menu.getImage())
				.append(System.getProperty("line.separator"))
				;
			});
		}
		else
		{
			sb.append("かたじけない！休みでござる！").append(ConfigReader.getInstance().getShogunIcon());
		}

		return sb.toString();
	}

	private Date convertLocalDateToOldDate(LocalDate date)
	{
		return Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
	}

	private static final String MENU_TARGET_TODAY		= "today";
	private static final String MENU_TARGET_TOMORROW	= "tomorrow";
}
