package uz.dictionary.data;

public class Category {
	// public final String name_tr="name_tr";
	// public final String name_uz="name_uz";
	// public final String t_order="t_order";
	// public final String html_page="html_page";
	//
	String name_tr;
	String name_uz;
	int t_order;
	String html_page;

	public Category(String name_tr, String name_uz, int t_order,
			String html_page) {
		this.name_tr = name_tr;
		this.name_uz = name_uz;
		this.t_order = t_order;
		this.html_page = html_page;
	}

	public String getName_tr() {
		return name_tr;
	}

	public void setName_tr(String name_tr) {
		this.name_tr = name_tr;
	}

	public String getName_uz() {
		return name_uz;
	}

	public void setName_uz(String name_uz) {
		this.name_uz = name_uz;
	}

	public int getT_order() {
		return t_order;
	}

	public void setT_order(int t_order) {
		this.t_order = t_order;
	}

	public String getHtml_page() {
		return html_page;
	}

	public void setHtml_page(String html_page) {
		this.html_page = html_page;
	}

	
}
