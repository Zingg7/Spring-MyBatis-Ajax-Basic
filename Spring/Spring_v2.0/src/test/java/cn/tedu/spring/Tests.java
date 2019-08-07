package cn.tedu.spring;

import java.util.Arrays;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Tests {

	@Test
	public void ioc() {
		// 1. 加载Spring配置文件，获取Spring容器
		ClassPathXmlApplicationContext ac
			= new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		
		// 2. 从容器中获取对象
		UserDao userDao = ac.getBean("userDao", UserDao.class);
		UserServlet userServlet = ac.getBean("userServlet", UserServlet.class);
		AdminDao adminDao = ac.getBean("adminDao", AdminDao.class);
		AdminServlet adminServlet = ac.getBean("adminServlet", AdminServlet.class);
		SampleBean sampleBean = ac.getBean("sampleBean", SampleBean.class);
		ValueBean valueBean = ac.getBean("valueBean", ValueBean.class);
		StudentServlet studentServlet = ac.getBean("studentServlet", StudentServlet.class);
		
		// 3. 测试使用
		System.out.println("userDao" + userDao);
		System.out.println("userDao.url=" + userDao.url);
		System.out.println("userDao.driver=" + userDao.driver);
		System.out.println("userServlet.userDao" + userServlet.userDao);
		System.out.println("adminDao.url=" + adminDao.url);
		System.out.println("adminServlet.name=" + adminServlet.name);
		System.out.println("adminServlet.adminDao=" + adminServlet.adminDao);
		System.out.println();
		System.out.println("sampleBean.names");
		System.out.println(sampleBean.names.getClass().getName());
		System.out.println(sampleBean.names);
		System.out.println();
		System.out.println("sampleBean.cities");
		System.out.println(sampleBean.cities.getClass().getName());
		System.out.println(sampleBean.cities);
		System.out.println();
		System.out.println("sampleBean.numbers");
		System.out.println(Arrays.toString(sampleBean.numbers));
		System.out.println();
		System.out.println("sampleBean.session");
		System.out.println(sampleBean.session.getClass().getName());
		System.out.println(sampleBean.session);
		System.out.println();
		System.out.println("sampleBean.config");
		System.out.println(sampleBean.config);
		System.out.println();
		System.out.println("valueBean.name=" + valueBean.name);
		System.out.println("valueBean.number=" + valueBean.number);
		System.out.println("valueBean.city=" + valueBean.city);
		System.out.println("valueBean.password=" + valueBean.password);
		System.out.println();
		studentServlet.reg();
		
		// 4. 释放资源
		ac.close();
	}
	
}











