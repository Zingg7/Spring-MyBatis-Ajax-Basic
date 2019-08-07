## 2.9. 使用Session

当需要向Session中存入数据时，可以使用`ModelMap`对象将数据进行封装，操作方式与封装转发的数据完全相同，例如：

	modelMap.addAttribute("username", username);

然后，需要在当前控制器类之前添加`@SessionAttributes`注解，并且，在注解中显式的指定`ModelMap`中封装的哪些数据是需要存储在Session中的，例如：

	@Controller 
	@RequestMapping(value="user")
	@SessionAttributes("username")
	public class UserController {
		// ...
	}

当添加了以上注解后，如果`ModelMap`中被存入了名为`username`的数据，该数据就在Session中，而`ModelMap`中的其它数据依然只能用于转发，也就是数据的作用域只在Request级别。

关于`@SessionAttributes`注解，其属性的配置可以参考该注解的源代码：

	@AliasFor("names")
	String[] value() default {};

	@AliasFor("value")
	String[] names() default {};

	Class<?>[] types() default {};

通过以上源代码可以看到：`value`和`names`属性的作用是完全相同，用于指定`ModelMap`中的哪些名称对应的数据需要存放到Session中，可以使用字符串数组表示多个属性，另外，还可以配置`types`属性用于指定Session的数据的数据类型，也可以是数组类型，与配置的`names`保持一致即可。

使用这种做法操作Session非常简单，但是，也存在一系列的问题：

1. 默认情况下，重定向时会把Session中的数据暴露在URL中；

2. 通过`ModelMap`存放的数据一定会在Request的作用域中，所以，通过这种方式存放到Session中的数据，其实在Request中也是存在的；

3. 通过这种方式存放到Session中的数据，不可以通过Session对象的`invalidate()`方法清除！只能通过`SessionStatus`类的`setComplete()`方法进行清除！

**更加简单**的操作Session的方式就是直接在处理请求的方法中添加`HttpSession`类型的参数，然后在方法体中直接操作即可，例如：

	@RequestMapping("handle_login.do") 
	public String handleLogin(String username, String password, ModelMap modelMap, HttpSession session) {
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

使用这种做法并不存在以上使用`@SessionAttributes`时的各种问题，操作也非常简单，缺点就是不易于执行单元测试！

可以忽略“不易于执行单元测试”，甚至“不使用`@SessionAttributes`”的原因可能是：可以使用专门的测试工具去测试控制器，所以，在控制器中的方法本身是不需要执行单元测试的，甚至在大型项目中根本就不会使用Session，那各种使用方式都是不需要的。

## 2.10. SpringMVC的拦截器(Interceptor)

如果项目中有多个请求需要执行相同的数据处理方案，就可以使用拦截器来实现。

拦截器的作用并不一定是要把请求“拦截下来，不允许向后执行”，其主要特征是：若干种不同的请求都需要先执行拦截器中的代码，才可以向后执行。

当然，拦截器也确实具备“拦截”的功能，即：可以将请求拦截下来，不允许向后执行。

假设需要定义一个“登录拦截器”，实现“如果用户已经登录，则放行，如果未登录，则拦截，不允许向后执行”。

首先，需要自定义`cn.tedu.spring.LoginInterceptor`拦截器类，实现`HandlerInterceptor`：

	public class LoginInterceptor implements HandlerInterceptor {
	
		public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
			// 返回false表示拦截，不允许向后执行
			System.out.println("LoginInterceptor.preHandle()");
			return false;
		}
	
		public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
			System.out.println("LoginInterceptor.postHandle()");
		}
	
		public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
			System.out.println("LoginInterceptor.afterCompletion()");
		}
	
	}

拦截器需要在Spring的配置文件中进行配置才可以使用：

	<!-- 配置拦截器链 -->
	<mvc:interceptors>
		<!-- 配置第1个拦截器 -->
		<mvc:interceptor>
			<!-- 拦截的路径 -->
			<mvc:mapping path="/index.do"/>
			<!-- 拦截器类 -->
			<bean class="cn.tedu.spring.LoginInterceptor" />
		</mvc:interceptor>
	</mvc:interceptors>

如果需要实现“验证登录以决定是否拦截或者放行”的功能，需要重写拦截器类中的`preHandle()`方法：

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
	
注意：即使调用了`response.sendRedirect()`进行重定向，如果要阻止继续向后运行，仍然需要`return false;`。

在配置拦截器时，使用的是`<mvc:mapping />`配置需要拦截的路径，每个拦截器都可以配置1~N个该节点。

在配置路径时，还可以使用`*`作为通配符，例如配置为`<mvc:mapping path="/product/*" />`，则表示例如`/product/index.do`、`/product/add.do`、`/product/list.do`等路径都会被拦截！但是，1个`*`只能通配1层资源，例如`/product/*`就不会匹配上`/product/x/y.do`，如果需要通配若干层路径，可以使用2个`*`，即配置为`/product/**`，使用2个`*`是无视层级的，无论是`/product/list.do`，还是`/product/x/y.do`，甚至更多层级的，都可以通配！

另外，还可以添加`<mvc:exclude-mapping />`用于配置例外路径，也就是“白名单”，被添加在白名单中的路径将不被拦截器处理，与`<mvc:mapping />`的配置方式完全相同，可以有多个配置白名单的节点，在配置时，路径中也可以使用通配符。

在配置整个`<mvc:interceptor>`节点时，其子级的节点必须先配置`<mvc:mapping />`，再配置`<mvc:exclude-mapping />`，最后配置`<bean>`，不可以颠倒顺序！

## 2.11. 在SpringMVC中统一处理异常

在Java中，异常的继承体系是：

	Throwable
		Error
			OutOfMemoryError
		Exception
			SQLException
			IOException
				FileNotFoundException
			RuntimeException
				NullPointerException
				ClassCastException
				ArithmeticException
				NumberFormatException
				IndexOutOfBoundsException
					ArrayIndexOutOfBoundsException
					StringIndexOutOfBoundsException

在Exception中，RuntimeException及其子孙类异常是比较特殊的异常，完全不受Java处理异常的语法约束，因为这些异常可能出现的频率极高，并且，这些异常是可以通过更加严谨的编程来杜绝异常的发生的！

常见的处理异常的做法是捕获(try...catch)或者声明抛出(throw/throws)，在实际处理时，如果当前类适合处理异常，就应该使用try...catch捕获并处理，如果当前类不适合处理异常，则应该声明抛出，然后续调用这个方法的角色进行处理。

在服务器端的项目中，通常适合处理异常的都是控制器，但是，某些异常可能在多个不同的功能中都会出现，在处理不同的请求时采取相同的代码进行处理，就会导致代码冗余，不便于统一管理，所以，在SpringMVC框架中就提供了统一处理异常的机制。

可以在控制器类中添加统一处理异常的方法，关于该方法：

1. 应该使用`public`权限；

2. 返回值的意义与处理请求的方法完全相同；

3. 方法名称可以自定义；

4. 方法中必须包含异常类型的参数，且参数的类型能包括所有可能需要处理的异常，例如可能处理`NullPointerException`，则参数的类型可以是`NullPointerException`或者`RuntimeException`或者`Exception`甚至`Throwable`，如果同时还需要处理`NumberFormatException`，参数类型就不可以是`NullPointerException`，简单来说，写`Throwable`绝对错不了；

5. 与处理请求的方法不同，不可以随心所欲的添加参数，如果需要转发数据，只能添加`HttpServletRequest`参数，不可以使用`ModelMap`；

6. 必须添加`@ExceptionHandler`注解。

所以，处理请求的方法可以是：

	@ExceptionHandler
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

一旦添加了该方法，当前类中任何处理请求的方法都不必处理相关异常，等同于这些方法把异常抛出了，将由以上方法进行统一处理！

需要注意的是：该方法只能处理当前类处理请求时出现的异常，如果其他控制器类的方法抛出了异常，是不会被处理的！可以把该处理异常的方法放在所有控制器类公共的父类中！

关于`@ExceptionHandler`的源代码：

	public @interface ExceptionHandler {
	
		/**
		 * Exceptions handled by the annotated method. If empty, will default to any
		 * exceptions listed in the method argument list.
		 */
		Class<? extends Throwable>[] value() default {};
	
	},

该注解可以指定需要被处理的异常的种类！参数可以是数组，即同时指定多种异常都将被该方法进行处理！

## 2.12. SpringMVC框架小结

1. 理解SpringMVC执行核心流程；

2. 掌握获取请求参数的方式；

3. 理解转发与重定向；

4. 了解转发时如何转发数据；

5. 掌握@RequestMapping、@RequestParam注解的使用；

6. 掌握拦截器的使用；

7. 掌握处理异常的使用。



#### 附1：什么时候需要使用Session

由于Http协议是无状态协议，每次请求与响应结束后，服务器与客户端就会断开，期间产生的数据及使用数据创建的状态都不会被保留下来，导致下次再次访问时，不可以使用前序产生的数据和状态，为了解决这个问题，就产生了Cookie和Session的用法，其中，Cookie是把特定的数据存储在客户端，而Session是把特定的信息存储在服务器端的内存中。

通常，使用Session存储：

1. 用户的身份的唯一标识，例如：用户的id；

2. 使用频率较高的数据，例如：用户名；

3. 不便于使用其它解决方案去存储或者传递的数据。

#### 附2. 拦截器(Interceptor)和过滤器(Filter)的区别 

拦截器和过滤器都是可以设置在若干种不同的请求处理之前的，都可以实现“拦截”和“放行”的做法，项目中，都可以存在若干个拦截器或者过滤器形成拦截器链或者过滤器链。

过滤器是JavaEE中的组件，拦截器是SpringMVC中的组件，只要是使用Java语言做服务器端开发都可以使用过滤器，但是，只有使用了SpringMVC框架才可以使用拦截器，并且，如果使用SpringMVC时，`DispatcherServlet`映射的路径是`*.do`，则只有以`.do`为后缀的请求才可能被拦截器处理，也就是说，只有被`DispatcherServlet`映射到的路径才可能被拦截器处理。

过滤器是执行在所有`Servlet`组件之前的，而拦截器是执行在`DispatcherServlet`之后、且在各Controller控制器之前及之后的组件！

过滤器是需要在**web.xml**中进行配置的，其过滤的路径只能通过`<url-pattern>`节点配置1个路径，配置非常不灵活，拦截器可以通过若干个`<mvc:mapping />`节点配置若干个黑名单，还可以通过若干个`<mvc:exclude-mapping />`节点配置若干个白名单，配置就非常灵活！

虽然拦截器和过滤器可以实现的的功能几乎相同，且拦截器的配置更加灵活，但是，由于执行时间节点的差异，拦截器也并不能完全取代过滤器！

### 附3. 解决乱码问题

计算机能够直接识别并处理的都是二进制数，也就是由0和1组成的序列，每个存储0或者1的空间称为“位(bit)”，由于每个二进制位只能存储1个0或者1个1，只能表达2种可能性，就不足以表示更多的内容，在计算机中，使用了更大的单位“字节(byte)”作为基本单位，每个字节由8个二进制位组成。

在ASCII编码表中指定了人类生活使用的字符与二进制数的对应关系，例如`a`对应的就是`110 0001`，假设输入了1个`a`，其实计算机处理的是`110 0001`，当计算机运算得到`110 0001`，就会显示为`a`。

由于ASCII编码表只制定了1个字节的对应关系，但是，中文的汉字种类太多，1个字节无法表达，就需要更多的字节数，例如使用2个字节，就可以表示更多种对应关系，Java语言在处理字符时，内存中就使用的Unicode编码。

当数据需要传输时，如果传输的是`a`，只需要1个字节就够了，如果传输的是`中`这个汉字，至少需要2个字节，所以，如果单纯直接传输二进制数的序列，接收方可能接收到`1110 0001 1101 1100 1011 1010`，却不知道如何进行分隔！所以，为了保证能够正确的分隔这些二进制的序列，就产生了传输编码，例如UTF-8。

在UTF-8中，如果某个字符是2个字节的，则使用的格式是：

	110x xxxx	10xx xxxx

如果某个字符是3个字节的，则使用的格式是：

	1110 xxxx	10xx xxxx	10xx xxxx

如果某个字符是4个字节的，则使用的格式是：

	1111 0xxx	10xx xxxx	10xx xxxx	10xx xxxx

通常，UTF-8分为常用版本(utf8mb3)和包括了不常用字符的版本(utf8mb4)，一般默认指的是常用版本。

当然，除了UTF-8以外，还有其它的编码格式，例如GBK、GB2312、ISO-8859-1、latin1等，不同的编码格式的编码规范是不相同的，甚至有些编码格式并不支持中文！所以，如果发出和接收使用的是不同的编码，甚至使用了不支持中文的编码，就会导致无法解读，进而出现乱码！

所以，乱码问题都是由于“使用的编码不统一”所导致的，而解决方案就是“使用统一的编码”，在项目中，常见的需要指定编码的位置有：项目的源代码、网络传输和接收的编码、界面、其它网络连接、数据库等存储位置……可以简单的理解为：只要能够指定编码的位置，统统指定同一种编码，就不会出现乱码，如果没有指定，就可能出现乱码。