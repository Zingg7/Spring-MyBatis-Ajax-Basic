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
		System.out.println("����User���󡭡�");
	}
	
	public void init() {
		System.out.println("��ʼ�����������á���");
	}
	
	public void destroy() {
		System.out.println("���ٷ��������á���");
	}
	
}
