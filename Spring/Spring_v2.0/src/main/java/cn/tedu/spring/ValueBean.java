package cn.tedu.spring;

public class ValueBean {
	
	// 值是SampleBean中的names中的第2个
	public String name;
	// 值是SampleBean中的numbers中的第3个
	public int number;
	// 值是SampleBean中的cities中的"Guangzhou"
	public String city;
	// 值是SampleBean中的session中password的值
	public String password;

	public void setName(String name) {
		this.name = name;
	}

	public void setNumber(int number) {
		this.number = number;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
}
