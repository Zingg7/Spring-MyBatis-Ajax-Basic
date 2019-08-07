package cn.tedu.spring;

public class AdminServlet {
	
	public String name;
	public AdminDao adminDao;
	
	public AdminServlet(String name, AdminDao adminDao) {
		super();
		this.name = name;
		this.adminDao = adminDao;
	}

}
