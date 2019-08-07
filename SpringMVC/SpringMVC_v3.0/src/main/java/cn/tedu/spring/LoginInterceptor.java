package cn.tedu.spring;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class LoginInterceptor implements HandlerInterceptor {

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		System.out.println("LoginInterceptor.preHandle()");
		// 获取HttpSession对象
		HttpSession session = request.getSession();
		// 判断Session中有没有登录的数据
		if (session.getAttribute("username") == null) {
			// 没有登录数据，即未登录，则重定向到登录页
			// http://localhost:8080/XX/index.do
			// http://localhost:8080/XX/user/password.do
			String projectName = request.getServletContext().getContextPath();
			response.sendRedirect(projectName + "/user/login.do");
			// 返回false表示拦截，不允许向后执行
			return false;
		}
		// 返回true表示放行，允许向后执行
		return true;
	}

	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		System.out.println("LoginInterceptor.postHandle()");
	}

	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		System.out.println("LoginInterceptor.afterCompletion()");
	}


}
