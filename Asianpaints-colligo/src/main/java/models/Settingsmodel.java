package models;

public class Settingsmodel {
	private Integer Id;
	private String Name;
	private String Category;
	public Settingsmodel(Integer setId, String setName) {
		// TODO Auto-generated constructor stub
		this.Id = setId;
		this.Name = setName;
	}
	public Integer getId() {
		return Id;
	}
	public void setId(Integer id) {
		Id = id;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getCategory() {
		return Category;
	}
	public void setCategory(String category) {
		Category = category;
	}
}
