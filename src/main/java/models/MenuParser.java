package models;

import java.io.IOException;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;


@Slf4j
public class MenuParser {

	public static List<MenuEntity> parseMenu(String menuUrl) throws IOException {
		HttpClient httpClient	= HttpClients.createDefault();
		HttpResponse response	= httpClient.execute(new HttpGet(menuUrl));
		String body				= EntityUtils.toString(response.getEntity(), "UTF-8");

		log.info("json get : " + body);

		Gson gson	= new Gson();
		final List<MenuEntity> menuEntityList	= gson.fromJson(body, new TypeToken<List<MenuEntity>>() {}.getType());

		return menuEntityList;
	}

	public static class MenuEntity {

		private String date;
		private List<Menu> Mon;
		private List<Menu> Tue;
		private List<Menu> Wed;
		private List<Menu> Thu;
		private List<Menu> Fri;

		public String getDate() {
			return date;
		}
		public List<Menu> getMon() {
			return Mon;
		}
		public List<Menu> getTue() {
			return Tue;
		}
		public List<Menu> getWed() {
			return Wed;
		}
		public List<Menu> getThu() {
			return Thu;
		}
		public List<Menu> getFri() {
			return Fri;
		}
	}

	public static class Menu {
		private String name;
		private String price;
		private String[] image;

		public String getName() {
			return name;
		}

		public String getPrice() {
			return price;
		}

		public String[] getImage() {
			return image;
		}
	}

	public static void main(String[] args)
	{
		try{
			parseMenu("http://bento-shogun.jp/menu/json/week.json");
		}catch(Exception e){
			log.error("", e);
		}
	}
}
