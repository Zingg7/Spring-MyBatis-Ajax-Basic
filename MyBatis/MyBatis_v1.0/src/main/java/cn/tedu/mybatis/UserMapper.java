package cn.tedu.mybatis;

public interface UserMapper {

	Integer insert(User user);
	
	Integer deleteById(Integer id);
	
	Integer updatePassword(String newPassword);
	
}
