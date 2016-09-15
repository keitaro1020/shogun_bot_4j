package models.db.menu;

import java.util.Date;

public class Menu {

	private Long id;
	private Date menuDate;
	private String name;
	private Integer price;
	private String image;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Date getMenuDate() {
		return menuDate;
	}
	public void setMenuDate(Date menuDate) {
		this.menuDate = menuDate;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Integer getPrice() {
		return price;
	}
	public void setPrice(Integer price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}

}
