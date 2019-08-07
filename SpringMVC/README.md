# 2. SpringMVC

## 2.1. SpringMVC的作用

SpringMVC主要解决了View-Controller交互的问题。

传统的`Controller`具体表现为一个个的`Servlet`类，在一个普通的项目中，需要实现的功能至少有50个以上，假设是50个，则项目中就需要创建50个`Servlet`类去处理这50个功能对应的请求，在**web.xml**中每个`Servlet`类至少需要8行代码进行配置，则共计需要400行代码进行配置（当然，JavaEE允许通过注解的方式进行配置），如果是一个更加复杂的系统，就会导致：`Servlet`实例过多，类文件太多，配置文件太长，所导致的代码开发难度和维护难度大的问题。另外，还存在`Servlet`或其它Java EE中的API功能较弱的问题。

## 2.2. SpringMVC中的核心组件

- `DispatcherServlet`：前端控制器，用于接收所有请求，并进行分发；

- `HandlerMapping`：记录了请求路径与实际处理请求的控制器之间的对应关系；

- `Controller`：实际处理请求的控制器；

- `ModelAndView`：控制器的处理结果，其中`Model`表示处理请求时得到的即将响应给客户端数据，`View`表示负责响应时显示的视图的名称；

- `ViewResolver`：根据视图的名称，确定视图组件。

![](01.png)

## 2.3. SpringMVC HelloWorld

### 2.3.1. 案例目标

在浏览器中，通过`http://localhost:8080/项目名称/hello.do`进行访问，页面中将显示“Hello, SpringMVC!!!”字样。

### 2.3.2. 创建项目

创建**Maven Project**，勾选**Create a simple project**，**Group Id**为`cn.tedu.spring`，**Artifact Id**为`SPRINGMVC-01`，**Packaging**必须选择`war`。

创建完成后，先生成**web.xml**；从前序项目中复制**spring-webmvc**依赖；复制Spring的配置文件到新项目中，删除原有配置；添加Tomcat运行环境。

### 2.3.3. 通过DispatcherServlet接收所有请求

SpringMVC中已经定义好了`DispatcherSerlvet`类，如果需要该`Servlet`能够接收并处理请求，首先，就需要在**web.xml**中进行配置：

	<servlet>
		<servlet-name>SpringMVC</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
	</servlet>
	
	<servlet-mapping>
		<servlet-name>SpringMVC</servlet-name>
		<url-pattern>*.do</url-pattern>
	</servlet-mapping>

> 在配置时，如果不明确`DispatcherServlet`的包名，可以随意创建某个Java类，在类中声明该`Servlet`，由Eclipse完成导包，则`import`语句中就有了该类的全名。

至此，`DispatcherServlet`就可以接收所有以`.do`为后缀的请求。

默认情况下，所有的`Servlet`都是第一次接收并处理请求时才完成的初始化操作，并且，SpringMVC框架是基于Spring框架基础之上的，在项目启动初期，就应该加载整个Spring环境，以保证SpringMVC框架能正常运行！所以，可以通过配置去实现：当启动Tomcat时，就直接初始化`DispatcherServlet`，在`DispatcherServlet`中，已经定义了某个属性，值是Spring配置文件的位置，当`DispatcherServlet`被初始化时，就会读取该配置文件！

在配置时，可以通过`<load-on-startup>`使得`DispatcherServlet`是默认直接初始化的！

在`DispatcherServlet`的父类`FrameworkServlet`中，有`contextConfigLocation`属性，其作用就是制定Spring配置文件的位置，一旦创建了`DispatcherServlet`对象，就会自动读取所配置的文件，以加载Spring的环境！则配置为：

	<init-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>classpath:spring.xml</param-value>
	</init-param>

至此，关于`DispatcherServlet`的完整配置：

	<servlet>
		<servlet-name>SpringMVC</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>classpath:spring.xml</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
	
	<servlet-mapping>
		<servlet-name>SpringMVC</servlet-name>
		<url-pattern>*.do</url-pattern>
	</servlet-mapping>

以上配置实现的效果就是：当Tomcat启动时，就会创建`DispatcherServlet`的对象，并且，`DispatcherServlet`会读取所配置的`spring.xml`文件，以加载Spring环境，后续，`DispatcherServlet`将接收所有以`.do`为后缀的请求！

如果需要验证现在的配置结果，可以自定义`cn.tedu.spring.User`类，然后，在类中显式的定义构造方法：

	public class User {
		public User() {
			System.out.println("创建了User的对象！");
		}
	}

然后在Spring的配置文件中进行配置：

	<bean class="cn.tedu.spring.User" />

由于Tomcat启动时就会初始化`DispatcherServlet`，进而加载Spring配置文件，就会由Spring框架创建`User`对象，导致构造方法被执行，所以，当启动Tomcat时，就会看到构造方法中输出的内容！

### 2.3.4. 使用控制器接收请求

首先创建`cn.tedu.spring.HelloController`控制器类，在类之前添加`@Controller`注解：

	package cn.tedu.spring;
	
	import org.springframework.stereotype.Controller;
	
	@Controller
	public class HelloController {
	
	}

然后，需要在Spring的配置文件设置组件扫描：

	<!-- 组件扫描 -->
	<context:component-scan base-package="cn.tedu.spring" />

然后，在控制类中添加处理请求的方法，方法的设计原则是：

1. 应该使用`public`权限；

2. 暂时使用`String`作为返回值类型；

3. 方法名称自由定义；

4. 方法中暂时不添加参数。

所以，可以将方法设计为：

	public String showHello() {
		return null;
	}

然后，在方法之前添加`@RequestMapping`注解，用于配置请求路径，以确定请求路径与处理请求的方法的对应关系：

	@RequestMapping("hello.do")
	public String showHello() {
		return null;
	}

所以，可以在方法中加入一段测试代码：

	@RequestMapping("hello.do")
	public String showHello() {
		System.out.println("HelloController.showHello()");
		return null;
	}

然后，打开浏览器，输入`http://localhost:8080/SPRINGMVC-01/hello.do`，在浏览器中会提示404错误，因为目前尚未处理页面，在Eclipse的控制台应该可以看到以上输出的内容！每访问一次，就会出现1次输出语句！

### 2.3.5. 响应页面

处理请求的方法的参数是`String`类型时，默认表示响应给客户端的视图的名称，后续，会经过`ViewResolver`进行处理，得到具体的视图。

首先，应该创建jsp页面，例如在**WEB-INF**下创建**index.jsp**文件，页面内容随意，例如显示“Hello, SpringMVC!!!”字样。

然后，需要设计处理请求的方法的返回值，并配置`ViewResolver`！`ViewResolver`是一个接口，具体使用的实现类是`InternalResourceViewResolver`，需要在Spring的配置文件中配置它的`prefix`和`suffix`属性，分别表示“前缀”和“后缀”，`InternalResourceViewResolver`的工作模式是以**webapp**文件夹为根路径，将“前缀 + 处理请求的方法的返回值 + 后缀”得到具体的JSP文件的位置，刚才创建的JSP文件在**webapp/WEB-INF/index.jsp**，则可以：

	/WEB-INF/	+	index	+	.jsp

	""	+	/WEB-INF/index.jsp	+	""

还可以是其他组合方式，只要能组合出文件的路径都是正确的，可以在处理请求的方法中返回`"index"`，然后配置为：

	<!-- 视图解析器 -->
	<bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<property name="prefix" value="/WEB-INF/" />
		<property name="suffix" value=".jsp" />
	</bean>

至此，开发完成，可以再次测试访问。

## 作业

开发新项目`SPRINGMVC-02`，要求实现的目标：

1. 通过`http://localhost:8080/SPRINGMVC-02/reg.do`能打开注册页面，该页面中至少包含用户名、密码、年龄、手机号码、电子邮箱的5个输入框，及1个提交按钮；

2. 通过`http://localhost:8080/SPRINGMVC-02/login.do`能打开登录页面，该页面中至少包含用户名、密码2个输入框，及1个提交按钮；

> 以上2个请求的处理对应2个方法，这2个方法可以在同一个控制器类中。

### -----------------------------------

### 附1：Tomcat启动失败的解决方案

首先，如果启动时，弹出对话框，对话框中提示了端口号，多是因为端口号冲突，例如此前已经启动了Tomcat却没有关闭，仍再次启动，就会出现冲突。如果要解决问题，可以先停止此前占用端口的程序，例如在Eclipse找到停止按钮，或者在Tomcat的bin目录下执行shutdown指令，如果不知道如何，可以重启电脑！

如果启动过程中报告异常信息，提示`ClassNotFoundException`或者`FileNotFoundException`，就检查对应的类是否存在，如果提示的类是依赖的jar包中的类，可能是jar包文件已经损坏，需要重新下载(可以删除.m2中的jar包或者更换版本)。

如果启动过程中报告的异常信息是`ZipException`，一定是某个jar包损坏，则检查最近添加的依赖的jar包并重新下载。

如果启动过程中报告的异常信息是`LifeCycleException`，通常是由于缓存问题导致的，则需要Clean项目及Tomcat。

如果把项目和Tomcat都Clean了仍错误，可以在**Servers**面板中删除现有的Tomcat，同时删除名为**Servers**的项目，然后重新添加Tomcat。

如果问题依然存在，则继续删除Tomcat，添加时，添加其他Tomcat。


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

### ----------------------

### 附1：什么时候需要使用Session

由于Http协议是无状态协议，每次请求与响应结束后，服务器与客户端就会断开，期间产生的数据及使用数据创建的状态都不会被保留下来，导致下次再次访问时，不可以使用前序产生的数据和状态，为了解决这个问题，就产生了Cookie和Session的用法，其中，Cookie是把特定的数据存储在客户端，而Session是把特定的信息存储在服务器端的内存中。

通常，使用Session存储：

1. 用户的身份的唯一标识，例如：用户的id；

2. 使用频率较高的数据，例如：用户名；

3. 不便于使用其它解决方案去存储或者传递的数据。

### 附2. 拦截器(Interceptor)和过滤器(Filter)的区别 

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


