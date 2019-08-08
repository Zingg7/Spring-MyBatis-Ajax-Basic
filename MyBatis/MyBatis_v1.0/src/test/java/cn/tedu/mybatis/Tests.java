package cn.tedu.mybatis;

import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Tests {

	BasicDataSource ds;
	MapperScannerConfigurer msc;
	SqlSessionFactoryBean ssfb;
	
	@Test
	public void getConnection() throws SQLException {
		ClassPathXmlApplicationContext ac
			= new ClassPathXmlApplicationContext(
				"spring.xml");
		
		BasicDataSource ds = ac.getBean("dataSource", BasicDataSource.class);
		
		Connection conn = ds.getConnection();
		System.out.println(conn);
		
		ac.close();
	}
	
	@Test
	public void insert() {
		ClassPathXmlApplicationContext ac
			= new ClassPathXmlApplicationContext(
				"spring.xml");
		
		UserMapper userMapper
			= ac.getBean("userMapper", UserMapper.class);
		System.out.println(userMapper.getClass());
		
		User user = new User();
		user.setUsername("mybatis");
		user.setPassword("1234");
		System.out.println("执行前：" + user);
		Integer rows = userMapper.insert(user);
		System.out.println("rows=" + rows);
		System.out.println("执行后：" + user);
		
		ac.close();
	}
	
	@Test
	public void deleteById() {
		ClassPathXmlApplicationContext ac
			= new ClassPathXmlApplicationContext(
				"spring.xml");
		
		UserMapper userMapper
			= ac.getBean("userMapper", UserMapper.class);
		
		Integer id = 8;
		Integer rows = userMapper.deleteById(id);
		System.out.println("rows=" + rows);
		
		ac.close();
	}
	
	@Test
	public void updatePassword() {
		ClassPathXmlApplicationContext ac
			= new ClassPathXmlApplicationContext(
				"spring.xml");
		
		UserMapper userMapper
			= ac.getBean("userMapper", UserMapper.class);
		
		String newPassword = "12345678";
		Integer rows = userMapper.updatePassword(newPassword);
		System.out.println("rows=" + rows);
		
		ac.close();
	}
	
}
