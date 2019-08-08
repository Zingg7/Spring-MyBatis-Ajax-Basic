package cn.tedu.mybatis;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Tests {

	BasicDataSource ds;
	MapperScannerConfigurer msc;
	SqlSessionFactoryBean ssfb;
	
	private ClassPathXmlApplicationContext ac;
	private UserMapper userMapper;
	private DepartmentMapper departmentMapper;
	
	@Test
	public void getConnection() throws SQLException {
		BasicDataSource ds = ac.getBean("dataSource", BasicDataSource.class);
		
		Connection conn = ds.getConnection();
		System.out.println(conn);
	}
	
	@Before
	public void doBefore() {
		ac = new ClassPathXmlApplicationContext(
				"spring.xml");
		
		userMapper = ac.getBean("userMapper", UserMapper.class);
		departmentMapper = ac.getBean("departmentMapper", DepartmentMapper.class);
	}
	
	@After
	public void doAfter() {
		ac.close();
	}
	
	@Test
	public void insert() {
		User user = new User();
		user.setUsername("mybatis");
		user.setPassword("1234");
		System.out.println("执行前：" + user);
		Integer rows = userMapper.insert(user);
		System.out.println("rows=" + rows);
		System.out.println("执行后：" + user);
	}
	
	@Test
	public void deleteById() {
		Integer id = 11;
		Integer rows = userMapper.deleteById(id);
		System.out.println("rows=" + rows);
	}
	
	@Test
	public void deleteByIds() {
		List<Integer> ids = new ArrayList<Integer>();
		ids.add(1);
		ids.add(3);
		ids.add(5);
		ids.add(7);
		ids.add(9);
		Integer rows = userMapper.deleteByIds(ids, null);
		System.out.println("rows=" + rows);
	}
	
	@Test
	public void updatePassword() {
		String newPassword = "12345678";
		Integer rows = userMapper.updatePassword(newPassword);
		System.out.println("rows=" + rows);
	}
	
	@Test
	public void updatePasswordById() {
		Integer id = 1;
		String newPassword = "888888";
		
		Integer rows = userMapper.updatePasswordById(id, newPassword);
		System.out.println("rows=" + rows);
	}
	
	@Test
	public void findById() {
		Integer id = 100;
		User user = userMapper.findById(id);
		System.out.println(user);
	}
	
	@Test
	public void findUserVOById() {
		Integer id = 2;
		UserVO user = userMapper.findUserVOById(id);
		System.out.println(user);
	}
	
	@Test
	public void findByUsernameAndPassword() {
		String username = "root1";
		String password = "888888";
		User user = userMapper.findByUsernameAndPassword(username, password);
		System.out.println(user);
	}
	
	@Test
	public void count() {
		Integer count = userMapper.count();
		System.out.println("count=" + count);
	}
	
	@Test
	public void findAll() {
		List<User> list = userMapper.findAll();
		System.out.println("size=" + list.size());
		for (User user : list) {
			System.out.println(user);
		}
	}
	
	@Test
	public void find() {
		String where = "username='mybatis'";
		String orderBy = "age"; // 14 12 17 16 13
		Integer offset = 2;
		Integer count = 1;
		List<User> list = userMapper.find(where, orderBy, offset, count);
		System.out.println("size=" + list.size());
		for (User user : list) {
			System.out.println(user);
		}
	}
	
	@Test
	public void findDepartmentVOById() {
		Integer id = 1;
		DepartmentVO result
			= departmentMapper.findVOById(id);
		System.out.println(result);
	}
	
}







