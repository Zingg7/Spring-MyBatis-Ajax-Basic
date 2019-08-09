# 5. SpringBoot

## 5.1. SpringBoot的作用

SpringBoot是默认整合了Spring、SpringMVC及相关常用框架的一个综合性框架，大量的减少了相关的配置，使得创建项目和使用变得更加简单。

在常规配置方面，SpringBoot的思想是“约定大于配置”，即：大多数开发者都会使用某种配置方式的话，则SpringBoot就会直接配置成那个样子，然后，开发者在使用SpringBoot时就不用再进行相关配置，只需要知道已经被配置为那个样子了就可以了！

## 5.2. 创建SpringBoot项目

需要打开浏览器，访问`https://start.spring.io/`，填写创建项目的参数，配置完成后，点击**Generate the project**即可生成项目。

解压缩下载得到的压缩包，其中的文件夹就是项目文件夹，推荐将该文件夹移动到Workspace中，然后通过Eclipse的**Import** -> **Exsiting Maven Projects**导入该项目，务必保证当前可以连接到Maven服务器，导入后，会自动下载大量依赖，直至项目结构完整。

## 5.3. SpringBoot HelloWorld

在**src/main/resources**下默认已经存在**static**的文件夹，该文件是SpringBoot项目用于存放静态资源的文件夹，例如存放`.html`文件、图片、`.css`文件、`.js`文件等，相当于传统项目中的**webapp**文件夹，则可以在**static**创建**index.html**欢迎页面。

在**src/main/java**下默认已经存在`cn.tedu.springboot.sample`包，该包名是根据创建项目时的参数决定的，这个包就是当前项目的根包(Base-Package)，并且在该包下已经存在`SampleApplication.java`文件，该文件的名称也是根据创建项目时填写的**artifact**决定的，该文件中包含`main()`方法，直接执行`main()`方法就可以启动当前项目，所以，该类也是SpringBoot的启动类！

SpringBoot项目在启动时会启动内置的Tomcat，默认占用8080端口，如果此前该端口已经处于占用状态，则项目会启动失败！

通过`http://localhost:8080`即可访问所涉及的网页，由于SpringBoot项目内置Tomcat，该Tomcat只为当前项目服务，所以启动时设置的Context Path是空字符串，在访问时URL中不必添加项目名称，而**index.html**是默认的欢迎页面，其文件名也不必体现在URL中！

## 5.4. SpringBoot + MyBatis环境

SpringBoot项目默认没有集成持久层相关依赖，需要手动补充，或者创建项目时就选中：

	<dependency>
		<groupId>org.mybatis.spring.boot</groupId>
		<artifactId>mybatis-spring-boot-starter</artifactId>
		<version>2.1.0</version>
	</dependency>

	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
		<scope>runtime</scope>
	</dependency>

当添加以上依赖之后，SpringBoot项目再启动时就会尝试读取连接数据库的相关配置，如果还没有配置，则会启动失败！

在**src/main/resources**下有**application.properties**，该文件就是SpringBoot的配置文件，在该文件中添加配置：

	spring.datasource.url=jdbc:mysql://localhost:3306/tedu_ums?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
	spring.datasource.username=root
	spring.datasource.password=root

> 一般情况下，不需要配置连接数据库的driverClassName，因为SpringBoot会自动从jar中读取！

添加以上配置后，项目可以正常启动，但是，如果以上配置信息是错误的，也不影响启动过程，也就是说，SpringBoot启动时，会读取相关配置，但是，并不执行数据库连接，所以，就算是配置错误也并不会体现出来。

## 5.5. 测试数据库连接

在**src/test/java**下，默认已经存在项目的根包及测试类，且测试类中已经存在一个空的测试方法：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class SampleApplicationTests {
	
		@Test
		public void contextLoads() {
		}
	
	}

可以先执行以上`contextLoads()`方法的单元测试，如果测试出错，一定是测试环境或者框架环境有问题，多考虑为jar包已经损坏，应该重新下载或者更换版本！

可以在该测试类中编写单元测试：

	@Autowired
	private DataSource dataSource;
	
	@Test
	public void getConnection() throws SQLException {
		Connection conn = dataSource.getConnection();
		System.err.println(conn);
	}

如果测试通过，则此前配置的数据库连接信息是正确的！

## 5.6. 注册功能的持久层接口

先创建与数据表对应的实体类`cn.tedu.springboot.sample.entity.User`：

	public class User implements Serializable {

		private static final long serialVersionUID = 7019981109167736281L;
	
		private Integer id;
		private String username;
		private String password;
		private Integer age;
		private String phone;
		private String email;
		private Integer isDelete;
		private Integer departmentId;

		// ...
	}

然后，创建持久层接口`cn.tedu.springboot.sample.mapper.UserMapper`：

	public interface UserMapper {
		
		Integer insert(User user);
		
		User findByUsername(String username);
	
	}

为了保证MyBatis框架能确定接口文件的位置，可以在接口的声明之前添加`@Mapper`注解，不过，这样的做法就要求每一个持久层接口之前都需要添加该注解，也可以在启动类`SampleApplication`之前添加`@MapperScan`注解进行配置，则后续只需要把持久层接口都放在这个包中就可以了，无需反复添加注解：

	@SpringBootApplication
	@MapperScan("cn.tedu.springboot.sample.mapper")
	public class SampleApplication {
	
		public static void main(String[] args) {
			SpringApplication.run(SampleApplication.class, args);
		}
	
	}

## 5.7. 注册功能的映射

可以在每一个抽象方法之前使用注解配置所对应的SQL语句，例如：

	@Options(useGeneratedKeys=true, keyProperty="id")
	@Insert("insert into t_user (字段列表) values (值列表)")
	Integer insert(User user);

这种做法是MyBatis本身就支持的，并不是SpringBoot所特有的！这种做法最大的优点在于：对应关系非常直观。主要的缺陷在于：配置长篇的SQL语句时，代码不易于阅读和维护！所以，一般仍然推荐使用XML配置映射的SQL语句！

与此前一样，在**src/main/resources**下创建名为**mappers**的文件夹，然后使用**UserMapper.xml**进行配置：

	<?xml version="1.0" encoding="UTF-8" ?>  
	<!DOCTYPE mapper PUBLIC "-//ibatis.apache.org//DTD Mapper 3.0//EN"      
		"http://ibatis.apache.org/dtd/ibatis-3-mapper.dtd">
	
	<!-- namespace：当前XML文件用于配置哪个接口中抽象方法对应的SQL语句 -->
	<mapper namespace="cn.tedu.springboot.sample.mapper.UserMapper">
	
		<!-- resultMap节点：指导MyBatis如何封装查询结果 -->
		<!-- id：自定义名称 -->
		<!-- column：查询结果中的列名 -->
		<!-- property：type对应的类中的属性名 -->
		<resultMap id="UserEntityMap"
			 type="cn.tedu.springboot.sample.entity.User">
			<id column="id" property="id" />
			<result column="username" property="username" />
			<result column="password" property="password" />
			<result column="age" property="age" />
			<result column="phone" property="phone" />
			<result column="email" property="email" />
			<result column="is_delete" property="isDelete" />
			<result column="department_id" property="departmentId" />
		</resultMap>
	
		<!-- 使用insert节点配置插入数据的SQL语句 -->
		<!-- id：抽象方法的方法名 -->
		<!-- 在#{}中间的是方法的参数User类中的属性名称 -->
		<insert id="insert"
			useGeneratedKeys="true"
			keyProperty="id">
			INSERT INTO t_user (
				username, password,
				age, phone,
				email, is_delete,
				department_id
			) VALUES (
				#{username}, #{password},
				#{age}, #{phone},
				#{email}, #{isDelete},
				#{departmentId}
			)
		</insert>
		
		<select id="findByUsername"
			resultMap="UserEntityMap">
			SELECT 
				* 
			FROM 
				t_user 
			WHERE 
				username=#{username} 
		</select>
		
	</mapper>
	
另外，还需要配置XML文件的位置，则打开**application.properties**文件添加配置：

	mybatis.mapper-locations=classpath:mappers/*.xml

## 5.8. 持久层单元测试

在**src/test/java**下创建`cn.tedu.springboot.sample.mapper.UserMapperTests`单元测试类，将默认存在的`SampleApplicationTests`类之前的2行注解复制到`UserMapperTests`类之前：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class UserMapperTests {
	}

然后，在类中声明持久层对象的属性：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class UserMapperTests {
		
		@Autowired
		private UserMapper mapper;

	}

> 凡是以前在SSM项目中可以通过`getBean()`方式获取的对象，在SpringBoot项目中都可以自动装配！

然后，编写并执行测试方法：

	@RunWith(SpringRunner.class)
	@SpringBootTest
	public class UserMapperTests {
		
		@Autowired
		private UserMapper mapper;
	
		@Test
		public void insert() {
			User user = new User();
			user.setUsername("springboot");
			user.setPassword("1234");
			Integer rows = mapper.insert(user);
			System.err.println("rows=" + rows);
		}
		
		@Test
		public void findByUsername() {
			String username = "springboot";
			User user = mapper.findByUsername(username);
			System.err.println(user);
		}
	}

## 5.9. 编写控制器处理请求

先创建控制器处理请求后的返回结果对象的类型`cn.tedu.springboot.sample.util.JsonResult`：

	public class JsonResult {
		private Integer state;
		private String message;

		// SET/GET ...
	}

SpringBoot项目不需要开发者配置组件扫描，它默认的组件扫描就是项目的根包，即`cn.tedu.springboot.sample`包，当前项目中所有的组件都必须在这个包或者其子包下！

所以，创建`cn.tedu.springboot.sample.controller.UserController`控制器类，在类之前添加`@RestController`注解和`@RequestMapping("user")`注解：

	@RestController
	@RequestMapping("user")
	public class UserController {
	
	}

> 使用`@RestController`相当于`@Controller`和`@ResponseBody`的组合使用方式，当使用了`@RestController`时，该控制器类中所有处理请求的方法都是相当于添加了`@ResponseBody`注解的！一旦使用了该注解，该控制器类中的方法将不可以转发或者重定向，如果一定要转发或者重定向，必须使用`ModelAndView`作为处理请求的方法的返回值！

然后，在控制器类中添加处理请求的方法：

	// /user/reg
	@RequestMapping("reg")
	public JsonResult reg(User user) {
	}

> 在SpringBoot项目中，默认已经将`DispatcherServlet`映射的路径配置为`/*`，即所有请求。

在处理过程中，显然需要使用到持久层对象来完成数据操作，所以，应该声明持久层对象的属性：

	@Autowired
	private UserMapper userMapper;

然后，完成处理请求的细节：

	@RequestMapping("reg")
	public JsonResult reg(User user) {
		// 创建返回值对象
		// 从参数user中获取尝试注册的用户名
		// 根据以上用户名查询用户数据
		// 检查查询结果是否为null
		// 是：用户名未被占用
		// -- 执行注册
		// -- 封装返回值对象的属性：1
		// 否：用户名已经被占用
		// -- 封装返回值对象的属性：2, 错误提示信息
		// 返回
	}

具体实现代码：

	@RequestMapping("reg")
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

完成后，通过启动类启动项目，打开浏览器，输入`http://localhost:8080/user/reg?username=junit&password=1234`进行测试。

在对方法添加注解配置映射的路径时，除了`@RequestMapping`以外，还可以使用`@GetMapping`、`@PostMapping`等，它们都是限制了请求方式的！