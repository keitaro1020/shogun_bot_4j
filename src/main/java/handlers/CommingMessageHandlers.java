package handlers;

import it.sauronsoftware.cron4j.Scheduler;

import java.time.ZoneId;
import java.time.ZonedDateTime;

import lombok.extern.slf4j.Slf4j;
import models.ConfigReader;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;
import com.github.masahitojp.botan.message.BotanMessageSimple;

@Slf4j
public class CommingMessageHandlers implements BotanMessageHandlers {

    private Scheduler scheduler;

    public CommingMessageHandlers()
    {
		scheduler = new Scheduler();
		// start cron4j scheduler.
		scheduler.start();
    }

	@Override
	public void register(Robot robot) {
		scheduler.schedule("40 12 * * 1-5", () -> {
			log.info("cron exec [comming]");
			robot.send(new BotanMessageSimple("@channel: そろそろ将軍様が参られますぞ" + ConfigReader.getInstance().getShogunIcon(), "bento_shogun"));
		});

		robot.respond("来た", "bento shogun come here", message -> {

			ZonedDateTime dateTime	= ZonedDateTime.now(ZoneId.of("Asia/Tokyo"));
			if(dateTime.getHour() > 11 && dateTime.getHour() < 14)
			{
				robot.send(new BotanMessageSimple("@channel: 将軍様のおなーりー" + ConfigReader.getInstance().getShogunIcon(), "bento_shogun"));
			}
			else
			{
				message.reply("こんな時間に？" + ConfigReader.getInstance().getShogunIcon());
			}
		});
	}

}
