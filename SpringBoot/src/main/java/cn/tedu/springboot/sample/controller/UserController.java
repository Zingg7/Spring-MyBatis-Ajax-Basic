package cn.tedu.springboot.sample.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cn.tedu.springboot.sample.entity.User;
import cn.tedu.springboot.sample.mapper.UserMapper;
import cn.tedu.springboot.sample.util.JsonResult;

@RestController
@RequestMapping("user")
public class UserController {

	@Autowired
	private UserMapper userMapper;
	
	@PostMapping("reg")
	public JsonResult reg(User user) {
		// 创建返回值对象
		JsonResult jsonResult = new JsonResult();
		// 从参数user中获取尝试注册的用户名
		String username = user.getUsername();
		// 根据以上用户名查询用户数据
		User result = userMapper.findByUsername(username);
		// 检查查询结果是否为null
		if (result == null) {
			// 是：用户名未被占用
			// 执行注册
			userMapper.insert(user);
			// 封装返回值对象的属性：1
			jsonResult.setState(1);
		} else {
			// 否：用户名已经被占用
			// 封装返回值对象的属性：2, 错误提示信息
			jsonResult.setState(2);
			jsonResult.setMessage("注册失败！尝试注册的用户名(" + username + ")已经被占用！");
		}
		// 返回
		return jsonResult;
	}
	
}
