package cn.tedu.mybatis;

import java.util.List;

import org.apache.ibatis.annotations.Param;

public interface UserMapper {

	Integer insert(User user);
	
	Integer deleteById(Integer id);
	
	Integer deleteByIds(
			@Param("ids") List<Integer> ids, 
			@Param("str") String str);
	
	Integer updatePassword(String newPassword);
	
	Integer updatePasswordById(
			@Param("id") Integer arg0, 
			@Param("password") String arg1);
	
	User findById(Integer id);
	
	UserVO findUserVOById(Integer id);
	
	User findByUsernameAndPassword(
			@Param("username") String username,
			@Param("password") String password);
	
	Integer count();
	
	List<User> findAll();
	
	List<User> find(
			@Param("where") String where, 
			@Param("orderBy") String orderBy, 
			@Param("offset") Integer offset, 
			@Param("count") Integer count);
	
}
