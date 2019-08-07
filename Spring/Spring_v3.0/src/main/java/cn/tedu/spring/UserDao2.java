package cn.tedu.spring;

import org.springframework.stereotype.Repository;

@Repository
public class UserDao2 extends UserDao {

	public void reg() {
		System.out.println("UserDao2.reg()");
	}
	
}
