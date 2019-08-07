package cn.tedu.spring;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

@Repository
@Scope("singleton")
@Lazy
public class UserDao {

	public UserDao() {
		System.out.println("创建UserDao的对象！");
	}
	
	public void reg() {
		System.out.println("UserDao.reg()");
	}
	
	@PostConstruct
	public void init() {
		System.out.println("UserDao.init()");
	}
	
	@PreDestroy
	public void destroy() {
		System.out.println("UserDao.destroy()");
	}
	
}






