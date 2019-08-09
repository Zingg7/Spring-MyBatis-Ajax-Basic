package cn.tedu.springboot.sample.mapper;

import cn.tedu.springboot.sample.entity.User;

public interface UserMapper {
	
	Integer insert(User user);
	
	User findByUsername(String username);

}





