package cn.tedu.spring;

import java.util.Calendar;
import java.util.Date;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class TestCase {

	@Test
	public void getDate() {
		// 1. 加载Spring配置文件，获得Spring容器
		System.out.println("准备获得Spring容器……");
		ClassPathXmlApplicationContext ac
			= new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		System.out.println("已经获得Spring容器。");
		
		// 2. 从Spring容器中获取对象
		System.out.println("准备从Spring容器中获取对象……");
		Date date = (Date) ac.getBean("date");
		Calendar cal = (Calendar) ac.getBean("calendar");
		Phone phone = (Phone) ac.getBean("phone");
		User user1 = (User) ac.getBean("user");
		User user2 = (User) ac.getBean("user");
		User user3 = (User) ac.getBean("user");
		System.out.println("已经从Spring容器中获取对象。");
		
		// 3. 测试所获取的对象
		System.out.println("准备测试所获取的对象……");
		System.out.println(date);
		System.out.println(cal);
		System.out.println(phone);
		System.out.println(user1);
		System.out.println(user2);
		System.out.println(user3);
		System.out.println(user1.name);
		System.out.println(user1.from);
		System.out.println("已经测试所获取的对象。");
		
		// 4. 关闭Spring容器，释放资源
		System.out.println("准备释放资源……");
		ac.close();
		System.out.println("已经释放资源。");
	}
	
}







