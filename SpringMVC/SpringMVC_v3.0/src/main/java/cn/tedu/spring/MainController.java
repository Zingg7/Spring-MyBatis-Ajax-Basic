package cn.tedu.spring;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.filter.CharacterEncodingFilter;

@Controller
public class MainController {
	
	CharacterEncodingFilter f;

	@RequestMapping("index.do")
	public String showIndex() {
		System.out.println("MainController.showIndex()");
		return "index";
	}
	
}
