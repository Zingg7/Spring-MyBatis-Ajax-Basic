package cn.tedu.spring;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("ex")
public class ExceptionController {
	
	@ExceptionHandler({NullPointerException.class, ArrayIndexOutOfBoundsException.class})
	public String handleException(Throwable ex, HttpServletRequest request) {
		if (ex instanceof NullPointerException) {
			request.setAttribute("errorMessage", "空指针异常！");
		} else if (ex instanceof ArrayIndexOutOfBoundsException) {
			request.setAttribute("errorMessage", "数据下标越界异常异常！");
		} else {
			request.setAttribute("errorMessage", "未知异常：" + ex.getClass().getName());
		}
		
		return "error";
	}

	@RequestMapping("null.do")
	public String showNull(String name, ModelMap modelMap) {
		name.length();
		return null;
	}
	
	@RequestMapping("index.do")
	public String showIndex() {
		int[] arr = new int[10];
		System.out.println(arr[1000000]);
		return null;
	}
	
	@RequestMapping("number.do")
	public String showNumber() {
		String str = "hello";
		Integer.valueOf(str);
		return null;
	}
	
}










