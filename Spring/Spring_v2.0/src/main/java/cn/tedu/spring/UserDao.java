package cn.tedu.spring;

public class UserDao {
	
	public String url; // jdbc:mysql://...
	public String driver; // com.mysql.jdbc.Driver

	public void setUrl(String url) {
		this.url = url;
	}

	public void setDriver(String driver) {
		this.driver = driver;
	}

}
