package cn.tedu.spring;

import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller 
@RequestMapping(value="user")
@SessionAttributes(names="username", types=String.class)
public class UserController {

	@RequestMapping(path= {"reg.do", "register.do"}, method= {RequestMethod.POST, RequestMethod.GET}) 
	public String showRegister() {
		return "reg";
	}
	
	@RequestMapping("login.do")
	public String showLogin() {
		return "login";
	}
	
	@RequestMapping("handle_reg.do")
	public String handleReg(User user) {
		System.out.println("UserController.handleReg()");
		System.out.println("\t" + user);
		
		// 注册成功，重定向到登录页
		// 当前位置：handle_reg.do
		// 目标位置：login.do
		return "redirect:login.do";
	}
	
	@RequestMapping("handle_login.do") 
	public String handleLogin(
			@RequestParam(name="username", required=false, defaultValue="admin") String username, 
			String password,
			ModelMap modelMap,
			HttpSession session) {
		// 日志
		System.out.println("UserController.handleLogin()");
		System.out.println("\tusername=" + username);
		System.out.println("\tpassword=" + password);
		
		// 判断用户名是否正确
		if ("root".equals(username)) {
			// 是：判断密码是否正确
			if ("1234".equals(password)) {
				// 是：登录成功，将用户名存入到Session
				// modelMap.addAttribute("username", username);
				session.setAttribute("username", username);
				// 重定向到主页
				return "redirect:../index.do";
			} else {
				// 否：密码错误
				modelMap.addAttribute("errorMessage", "ModelMap：密码错误");
				return "error";
			}
		} else {
			// 否：用户名错误
			modelMap.addAttribute("errorMessage", "ModelMap：用户名错误");
			return "error";
		}
	}
	
}




