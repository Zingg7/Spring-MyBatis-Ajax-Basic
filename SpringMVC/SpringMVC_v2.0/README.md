## 2.4. 接收请求参数

### 2.4.1. 【不推荐】通过HttpServletRequest获取请求参数

可以在处理请求的方法中，添加`HttpServletRequest`类型的参数，在处理过程中，调用该参数对象的`String getParameter(String name)`就可以获取请求参数，例如：

	@RequestMapping("handle_reg.do")
	public String handleReg(HttpServletRequest request) {
		System.out.println("UserController.handleReg()");
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		Integer age = Integer.valueOf(request.getParameter("age"));
		String phone = request.getParameter("phone");
		String email = request.getParameter("email");
		
		System.out.println("\tusenrame=" + username);
		System.out.println("\tpassword=" + password);
		System.out.println("\tage=" + age);
		System.out.println("\tphone=" + phone);
		System.out.println("\temail=" + email);
		return null;
	}

通常，并不推荐使用这种做法！主要原因有：

- 使用比较繁琐，代码量较多；

- 需要手动实现数据类型的转换；

- 不便于实现单元测试。

### 2.4.2. 【推荐】将请求参数设计为处理请求的方法的参数

假设客户端将提交名为`username`的参数，则在控制器的方法中添加同名参数即可，参数的类型可以是期望的数据类型，例如期望`age`是`Integer`类型的，则声明为`Integer age`即可：

	@RequestMapping("handle_reg.do")
	public String handleReg(String username, String password, 
			Integer age, String phone, String email) {
		System.out.println("UserController.handleReg()");
		System.out.println("\t[2]username=" + username);
		System.out.println("\t[2]password=" + password);
		System.out.println("\t[2]age=" + age);
		System.out.println("\t[2]phone=" + phone);
		System.out.println("\t[2]email=" + email);
		return null;
	}

使用这种做法时，需要保证参数的名称与客户端提交的请求参数名称保持一致，否则，在控制器中的参数将是`null`值。

使用这种做法的优点很多，基本上解决了使用`HttpServletRequest`获取参数时的所有问题！

使用这种做法并不适用于请求参数的数量较多的应用场景！

### 2.4.3. 【推荐】使用封装的类型接收较多的请求参数

当客户端提交的请求参数较多时，可以将这些参数全部封装为1个自定义的数据类型，例如：

	public class User {

		private String username;
		private String password;
		private Integer age;
		private String phone;
		private String email;
		
		// SET/GET
	}

然后，将该类型作为处理请求的方法的参数即可：

	@RequestMapping("handle_reg.do")
	public String handleReg(User user) {
		System.out.println("UserController.handleReg()");
		System.out.println("\t" + user);
		return null;
	}

在使用这种做法时，需要保证客户端提交的请求参数，与自定义的数据类型中的属性名称是保持一致的！

### 2.4.4. 小结

第1种使用`HttpServletRequest`的方式是不会再使用的。

如果请求参数的数量较少，且从业务功能来说参数的数量基本固定，推荐使用第2种方式，即直接将请求参数逐一的设计在处理请求的方法中，否则，就使用第3种方式，将多个参数封装成1个自定义的数据类型。

另外，第2种方式和第3种方式是可以组合使用的，即出现在同一个处理请求的方法中！

## 2.5. 重定向

当处理请求的方法的返回值是`String`类型的，则返回的字符串使用`redirect:`作为前缀，加上重定向的目标路径，就可以实现重定向的效果。

假设注册一定成功，且注册成功后需要跳转到登录页面，则：
(如果转发，刷新后会重新提交表单)

	@RequestMapping("handle_reg.do")
	public String handleReg(User user) {
		System.out.println("UserController.handleReg()");
		System.out.println("\t" + user);
		
		// 注册成功，重定向到登录页
		// 当前位置：handle_reg.do
		// 目标位置：login.do
		return "redirect:login.do";
	}

练习：希望通过`http://localhost:8080/项目名/index.do`访问主页，主页的页面显示内容可以自由定制，当用户尝试登录时，假设一定登录成功，且登录成功后到主页。

## 2.6. 转发

### 2.6.1. 执行转发

当处理请求的方法的返回值是`String`类型，默认情况下，返回值就表示转发的意思，返回值将经过视图解析器，确定转发到的目标页面。

转发时，处理请求的方法的返回值也可以使用`forward:`作为前缀，由于默认就是转发，所以不必显式的添加前缀。

### 2.6.2. 【不推荐】通过HttpServletRequest封装需要转发的数据

假设在登录过程中，仅当用户名为`root`且密码是`1234`时允许登录，否则，在错误提示页面中提示错误的原因。

由于错误信息可能有2种，分别是用户名错误和密码错误，使用JSP页面结合EL表达式可以显示转发的数据，在控制器转发之前，就需要将错误信息封装到`HttpServletRequest`对象中，则后续JSP页面才可以通过EL表达式读取`HttpServletRequest`对象中的数据。

可以在处理请求的方法的参数列表中添加`HttpServletRequest`类型的参数，当添加多项参数时（既有用户名、密码，又有HttpServletRequest），各参数不必区分先后顺序，当添加了参数后，调用`HttpServletRequest`参数对象的`setAttribute(String name, String value)`方法封装需要转发的数据即可，无需获取转发器对象执行转发，只要最后返回字符串就表示转发：

	@RequestMapping("handle_login.do")
	public String handleLogin(String username, String password, HttpServletRequest request) {
		// 日志
		System.out.println("UserController.handleLogin()");
		
		// 判断用户名是否正确
		if ("root".equals(username)) {
			// 是：判断密码是否正确
			if ("1234".equals(password)) {
				// 是：登录成功，重定向到主页
				return "redirect:index.do";
			} else {
				// 否：密码错误
				request.setAttribute("errorMessage", "密码错误");
				return "error";
			}
		} else {
			// 否：用户名错误
			request.setAttribute("errorMessage", "用户名错误");
			return "error";
		}
	}
	
JSP:

	${errorMessage }

当然，这种做法依然是不推荐的，使用了`HttpServletRequest`作为参数后不便于执行单元测试。

### 2.6.3. 【更不推荐】使用ModelAndView

使用`ModelAndView`作为处理请求的方法的返回值类型，在返回结果之前，调用`ModelAndView`对象的`setViewName(String viewName)`方法确定转发的视图名称，调用`addObject(String name, Object value)`方法封装需要转发的数据，然后返回结果即可：

	@RequestMapping("handle_login.do")
	public ModelAndView handleLogin(String username, String password) {
		// 创建返回值对象
		ModelAndView mav = new ModelAndView();
		
		// 判断用户名是否正确
		if ("root".equals(username)) {
			// 是：判断密码是否正确
			if ("1234".equals(password)) {
				// 是：登录成功，重定向到主页
				return null;
			} else {
				// 否：密码错误
				mav.addObject("errorMessage", "ModelAndView：密码错误");
				mav.setViewName("error");
				return mav;
			}
		} else {
			// 否：用户名错误
			mav.addObject("errorMessage", "ModelAndView：用户名错误");
			mav.setViewName("error");
			return mav;
		}
		
	}

因为对于初学SpringMVC的人来说，`ModelAndView`是一个新的、比较麻烦的数据类型，并且SpringMVC提供了更简单的操作方式，所以不推荐使用`ModelAndView`。

### 2.6.4. 【推荐】使用ModelMap封装需要转发的数据

使用`ModelMap`的方式与使用`HttpServletRequest`几乎完全相同：

	@RequestMapping("handle_login.do")
	public String handleLogin(String username, String password, ModelMap modelMap) {
		// 日志
		System.out.println("UserController.handleLogin()");
		
		// 判断用户名是否正确
		if ("root".equals(username)) {
			// 是：判断密码是否正确
			if ("1234".equals(password)) {
				// 是：登录成功，重定向到主页
				return "redirect:index.do";
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

相比`HttpServletRequest`而言，使用`ModelMap`更加易于实现单元测试，并且更加轻量级，所以，推荐使用这种方式来封装需要转发的数据。

练习：假设`root`是已经被注册的用户名，在处理注册时，如果用户提交的用户名是`root`，则提示错误，否则，视为注册成功，重定向到登录页。

## 2.7. 关于@RequestMapping注解

### 2.7.1. 基本使用

在处理请求的方法之前添加`@RequestMapping`，可以配置请求路径与处理请求的方法的映射关系。

除此以外，还可以在控制器类之前添加该注解，表示增加了访问路径中的层级，例如：

	@Controller 
	@RequestMapping("user")
	public class UserController {
	}

添加该注解以后，原本通过`login.do`访问的请求路径就需要调整为`user/login.do`才可以访问。

通常，推荐为每一个控制器类都添加该注解！

同时在类和方法之前都添加了注解后，最终的访问路径就是类与方法的注解中的路径组合出来的URL。

在配置路径时，会无视两端的`/`符号，以下各种配置方式是等效的：

	user	login.do
	/user	/login.do
	user/	login.do
	user	/login.do
	/user/	/login.do

在实际使用时，只要保持语法风格的统一，就是对的，例如整个项目开发过程中，始终使用以上第1种，或者始终使用以上第2种，都是正确的做法。

### 2.7.2. 注解配置

在配置`@RequestMapping`时，可以显式的配置为：

	@RequestMapping(value="reg.do")

关于`value`属性在注解中的声明是：

	@AliasFor("path")
	String[] value() default {};

可以看到，该属性的数据类型是`String[]`，所以，也可以配置为：

	@RequestMapping(value= {"reg.do", "register.do"})

则后续无论通过这里的哪个URL都会导致映射的方法被执行。

在`value`属性的声明上方还使用了`@AliasFor`注解，表示`value`和`path`是完全等效的！从SpringMVC 4.2版本开始支持使用`path`属性，并推荐使用`path`属性取代`value`属性。

在使用时，还可以指定`method`属性，其声明是：

	RequestMethod[] method() default {};

该属性的作用是用于限制请求方式，例如：

	@RequestMapping(path= {"reg.do", "register.do"}, method=RequestMethod.POST) 

以上代码表示提交的请求必须是POST请求，如果不是，会导致405错误：

	HTTP Status 405 – Method Not Allowed
	// 404 - Resource Not Allowed

在没有配置`method`之前，是不限定请求方式的，如果配置了，则必须使用配置的请求方式中的某一种！

当为注解配置多个属性时，每一个属性都必须显式的指定属性名称！

## 2.8. 关于@RequestParam注解

可以在处理请求的方法的参数之前添加`@RequestParam`注解，首先，使用该注解可以解决名称不一致的问题，即客户端提交的请求参数名称与服务器端处理请求的方法的参数名称不一致的问题，例如：

	@RequestParam("uname") String username

如果添加了该注解，仍然存在名称不一致的问题，会导致400错误（如果没有添加该注解，即使名称不一致，服务器端的参数只是null值，并不会报错）：

	HTTP Status 400 – Bad Request

原因在于在该注解的源代码中：

	boolean required() default true;

所以，添加了该注解，默认是必须提交指定名称的参数的！如果希望该请求参数不是必须提交的，可以：

	@RequestParam(name="uname", required=false) String username 

另外，该注解中还有：

	String defaultValue() default ValueConstants.DEFAULT_NONE;

该属性用于指定默认值，即客户端没有提交指定名称的参数时，默认为某个值，例如：

	@RequestParam(name="uname", required=false, defaultValue="admin") String username

注意：在设置默认值时，必须显式的将`required`属性设置为`false`。

**小结：在什么时候需要使用该注解？**

- 前后端使用的名称不统一时；

- 强制要求客户端必须提交某参数时；

- 允许客户端不提交某参数，且视为提交了某默认值时；

- 其他固定使用场景。

### --------------------------

### 附1：关于GET和POST的区别

GET请求会将请求参数体现在URL中，POST请求会将请求参数封装在请求体中，并不会体现在URL中。

GET请求不适用于涉及隐私、安全方面的数据，也不适用于传输数据量较大的数据，通常限制值是2K，该值既取决于客户端的浏览器，也取决于服务器端。

所以，在涉及隐私、安全的数据提交，或者较大数据的提交（特别是上传文件），都应该优先考虑POST方式提交请求。

由于POST方法将请求参数封装在请求体中，没有体现在URL中，所以，如果涉及URL分享等操作，必须使用GET方式提交请求。

在发生请求时，如果使用GET请求，将一次性将请求的URL提交到服务器，所以，请求参数也就直接提交到了服务器，如果使用POST请求，会先向服务器提交第1次请求，此次请求并不携带请求参数，当服务器响应100后，客户端发出第2次请求，再将请求参数提交到服务器。所以，GET请求的访问速度比POST请求更快。

### 附2：关于转发和重定向

无论是转发还是重定向，都是客户端请求的第1个目标无法实现请求的响应，需要配合服务器端的其它组件来完成响应！

转发的原因是因为使用控制器可以处理用户请求，但是，当得到数据结果后，存在不便于显示的问题，毕竟控制器是Java类，不便于组织HTML代码，所以，会将数据结果转发给JSP页面，由JSP页面来完成数据的呈现，当然，JSP也存在不便于处理数据逻辑的问题，即与HTML高度相似的代码结构中不便于编写Java代码，所以，推荐的做法就是控制器负责处理数据，得到数据结果后把这些数据结果转发给JSP，由JSP呈现，整个过程是发生在服务器内部的，对于客户端来说是不可见的，所以，在转发时，客户端只发出了1次请求，请求的URL就是控制器的URL，并且，即使WEB-INF文件夹的内容是不可以被客户端直接访问的，也不影响把JSP放在这个文件夹下。

重定向是客户端发出第1次请求后，服务器端无法完成最终的响应，所以，只能给出302(通常是302)响应码，让客户端请求另一个URL来完成最终响应，在整个过程中，客户端是发出了2次请求的，同时，客户端也明确第2次的请求目标，所以，在客户端的浏览器中，会显示第2次请求的URL，由于客户端共发出2次请求，所以，在没有经过特殊的处理方式时，第1次请求的数据不可以直接应用到第2次请求中。
