package cn.tedu.spring;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Tests {
	
	@Test
	public void getBean() {
		System.out.println("准备加载Spring配置文件，获取Spring容器……");
		ClassPathXmlApplicationContext ac
			= new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		System.out.println("完成加载Spring配置文件，获取Spring容器！");
		
		System.out.println("准备获取对象……");
		UserDao userDao 
			= ac.getBean("userDao", UserDao.class);
		UserDao userDao2 
			= ac.getBean("userDao", UserDao.class);
		UserServlet userServlet
			= ac.getBean("userServlet", UserServlet.class);
		System.out.println("完成获取对象！");
		
		System.out.println("准备执行测试……");
		System.out.println("userDao=" + userDao);
		System.out.println("userDao2=" + userDao2);
		System.out.println("userServlet=" + userServlet);
		System.out.println();
		userServlet.reg();
		System.out.println("完成测试！");
		
		System.out.println("准备释放资源……");
		ac.close();
		System.out.println("运行结束！");
	}

}







