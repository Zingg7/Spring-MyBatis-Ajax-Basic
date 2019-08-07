package cn.tedu.spring;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class SampleBean {

	// Frank, Andy, Lucy, Kate
	public List<String> names;
	
	// Beijing, Shanghai, Guangzhou, Shenzhen
	public Set<String> cities;
	
	// { 9, 5, 2, 7 } 
	public int[] numbers;
	
	// username=root, password=1234, from=Hangzhou, age=26
	public Map<String, String> session;
	
	// url=xx, driver=xx, username=xx, password=xx
	public Properties config;

	public void setNames(List<String> names) {
		this.names = names;
	}

	public void setCities(Set<String> cities) {
		this.cities = cities;
	}

	public void setNumbers(int[] numbers) {
		this.numbers = numbers;
	}

	public void setSession(Map<String, String> session) {
		this.session = session;
	}

	public void setConfig(Properties config) {
		this.config = config;
	}
	
}