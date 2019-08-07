package cn.tedu.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class UserServlet {
	
	@Autowired
	public UserDao userDao2;
	
	public void reg() {
		System.out.println("UserServlet.reg()");
		userDao2.reg();
	}

}





