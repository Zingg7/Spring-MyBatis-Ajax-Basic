package cn.tedu.spring;

import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Tests {
	
	@Test
	public void getBean() {
		System.out.println("׼������Spring�����ļ�����ȡSpring��������");
		ClassPathXmlApplicationContext ac
			= new ClassPathXmlApplicationContext(
				"applicationContext.xml");
		System.out.println("��ɼ���Spring�����ļ�����ȡSpring������");
		
		System.out.println("׼����ȡ���󡭡�");
		UserDao userDao 
			= ac.getBean("userDao", UserDao.class);
		UserDao userDao2 
			= ac.getBean("userDao", UserDao.class);
		UserServlet userServlet
			= ac.getBean("userServlet", UserServlet.class);
		System.out.println("��ɻ�ȡ����");
		
		System.out.println("׼��ִ�в��ԡ���");
		System.out.println("userDao=" + userDao);
		System.out.println("userDao2=" + userDao2);
		System.out.println("userServlet=" + userServlet);
		System.out.println();
		userServlet.reg();
		System.out.println("��ɲ��ԣ�");
		
		System.out.println("׼���ͷ���Դ����");
		ac.close();
		System.out.println("���н�����");
	}

}







