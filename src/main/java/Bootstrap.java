import java.io.IOException;
import java.util.Optional;

import models.ConfigReader;
import models.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import adapters.SlackRTMAdapter2;

import com.github.masahitojp.botan.Botan;
import com.github.masahitojp.botan.brain.mapdb.MapDBBrain;

public class Bootstrap {
	private static Logger log				= LoggerFactory.getLogger(Bootstrap.class);
	private Optional<Botan> botStoppable	= Optional.empty();

	public Bootstrap() {
	}

	public int startUp() throws IOException {
		log.info("start");

		// config init
		ConfigReader configReader	= ConfigReader.getInstance();

		// db init
		Database.initialize();


		final Botan botan			= new Botan.BotanBuilder()
										.setAdapter(new SlackRTMAdapter2(configReader.getSlackApiToken()))
										.setBrain(new MapDBBrain(configReader.getKvsURI(), configReader.getKvsName()))
										.build();

		this.botStoppable		= Optional.ofNullable(botan);

		return 0;
	}

	public void stop() {
		log.info("stop");
		botStoppable.ifPresent(botan -> {
			try {
				botan.stop();
			} catch (Exception e) {
				log.error("", e);
			}
		});
		Database.dispose();
	}

	public static void main(String[] args) throws IOException {
		final Bootstrap bootstrap	= new Bootstrap();
		bootstrap.startUp();

		Runtime.getRuntime().addShutdownHook(
				new Thread() {
					@Override
					public void run() {
						bootstrap.stop();
					}
				}
		);

		bootstrap.botStoppable.ifPresent(botan -> {
			try {
				botan.start();
			} catch (Exception e) {
				log.error("", e);
			}
		});
	}

}
