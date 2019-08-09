package cn.tedu.ajax;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("user")
public class UserController {
	
	@RequestMapping("login.do")
	@ResponseBody
	public JsonResult login(
		@RequestParam("username") String username,
		@RequestParam("password") String password) {
		Integer state;
		String message = null;
		
		if ("root".equals(username)) {
			if ("1234".equals(password)) {
				state = 1;
			} else {
				state = 3;
				message = "�������";
			}
		} else {
			state = 2;
			message = "�û��������ڣ�";
		}
		
		JsonResult jsonResult = new JsonResult();
		jsonResult.setState(state);
		jsonResult.setMessage(message);
		return jsonResult;
	}

}









