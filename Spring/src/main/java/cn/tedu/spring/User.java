package cn.tedu.spring;

public class User {

	public String name;
	public String from;

	public void setFrom(String from) {
		this.from = from;
	}

	public void setName(String name) {
		this.name = name;
	}

	public User() {
		super();
		System.out.println("创建User对象……");
	}
	
	public void init() {
		System.out.println("初始化方法被调用……");
	}
	
	public void destroy() {
		System.out.println("销毁方法被调用……");
	}
	
}
