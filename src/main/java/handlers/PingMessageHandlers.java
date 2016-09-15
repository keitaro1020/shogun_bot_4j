package handlers;

import models.ConfigReader;

import com.github.masahitojp.botan.Robot;
import com.github.masahitojp.botan.handler.BotanMessageHandlers;

public class PingMessageHandlers implements BotanMessageHandlers {

	@Override
	public void register(Robot robot) {
		robot.respond(
				"ping\\z",
				"ping method",
				message -> {
					message.reply("pong" + ConfigReader.getInstance().getShogunIcon());
				});
	}

}
