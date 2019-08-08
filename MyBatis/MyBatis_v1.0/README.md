# 2. MyBatis框架

## 2.1. 框架的作用

简化数据库编程，开发者只要指定每项数据操作时的SQL语句及对应的抽象方法即可。

## 2.2. 创建Spring+MyBatis的项目

创建Maven Project，Group Id为cn.tedu.mybatis，Artifact Id为MyBatis，Packaing选择war。

创建完成后，生成web.xml，添加依赖，添加Tomcat运行环境，复制web.xml中的配置，复制前序项目中的spring.xml(需要删除拦截器的配置)。

然后，需要**添加一些新的依赖**，首先，添加mybatis的依赖：

	<dependency>
		<groupId>org.mybatis</groupId>
		<artifactId>mybatis</artifactId>
		<version>3.5.1</version>
	</dependency>

MyBatis框架是可以独立使用的，但是配置相对繁琐，且没有实际价值，通常都是与Spring结合使用的，甚至结合了SpringMVC，所以，需要添加`mybatis-spring`的依赖：

	<dependency>
		<groupId>org.mybatis</groupId>
		<artifactId>mybatis-spring</artifactId>
		<version>2.0.1</version>
	</dependency>

MyBatis的底层是基于jdbc实现的，所以，结合Spring使用后，需要添加`spring-jdbc`依赖，该依赖的代码与`spring-webmvc`几乎一样，只是`artifact id`不同，通常，这2个依赖的版本应该完全相同：

	<dependency>
		<groupId>org.springframework</groupId>
		<artifactId>spring-jdbc</artifactId>
		<version>4.3.8.RELEASE</version>
	</dependency>

还需要添加`mysql-connector-java`的依赖：

	<dependency>
		<groupId>mysql</groupId>
		<artifactId>mysql-connector-java</artifactId>
		<version>8.0.16</version>
	</dependency>
	
另外，还需要`commons-dbcp`数据库连接池的依赖：

	<dependency>
		<groupId>commons-dbcp</groupId>
		<artifactId>commons-dbcp</artifactId>
		<version>1.4</version>
	</dependency>

最后，检查是否已经添加好了`junit`依赖，如果已经存在，则跳过，如果没有添加，则补充。

> 添加依赖：mybatis / mybatis-spring / spring-jdbc / mysql-connector-java / commons-dbcp / junit

## 2.3. 配置数据库连接

在**src/main/resources**下创建**db.properties**文件，以**确定数据库连接**的相关配置：

	url=jdbc:mysql://localhost:3306/tedu_ums?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
	driver=com.mysql.cj.jdbc.Driver
	username=root
	password=root
	initialSize=2
	maxActive=5

然后，需要在Spring的配置文件中**读取以上配置信息**：

	<!-- 读取db.properties -->
	<util:properties id="dbConfig" location="classpath:db.properties" />

最终，程序运行时，需要使用的数据源是`BasicDataSource`，框架会通过这个类的对象获取数据库连接对象，然后实现数据访问，所以，就需要**为这个类的相关属性注入值，把数据库配置信息确定下来**：
	
	<!-- 配置数据源 -->
	<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
		<property name="url" value="#{dbConfig.url}" />
		<property name="driverClassName" value="#{dbConfig.driver}" />
		<property name="username" value="#{dbConfig.username}" />
		<property name="password" value="#{dbConfig.password}" />
		<property name="initialSize" value="#{dbConfig.initialSize}" />
		<property name="maxActive" value="#{dbConfig.maxActive}" />
	</bean>

接下来，可以检验一下以上完成的配置是否正确，则在**src/test/java**下创建`cn.tedu.mybatis.Tests`测试类，编写并执行测试方法：

	@Test
	public void getConnection() throws SQLException {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("spring.xml");
		
		BasicDataSource ds = ac.getBean("dataSource", BasicDataSource.class);
		
		Connection conn = ds.getConnection();
		System.out.println(conn);
		
		ac.close();
	}

## 2.4. 设计接口和抽象方法

创建`cn.tedu.mybatis.User`类，类的属性与`t_user`表保持一致：

	public class User {
		private Integer id;
		private String username;
		private String password;
		private Integer age;
		private String phone;
		private String email;

		// SET/GET/toString/hashCode(id)/equals(id)/Serializable

	}


在MyBatis中，要求抽象方法写在接口中，所以，需要先创建`cn.tedu.mybatis.UserMapper`接口：

	public interface UserMapper {
	}

然后，在接口中添加抽象方法，设计原则：

- 如果要执行的操作是`INSERT`/`UPDATE`/`DELETE`，返回值类型使用`Integer`，表示受影响的行数；

- 方法的名称可以自定义，但是，不允许重载；

- 参数列表根据执行SQL语句时的不确定数据来设计。

对于要执行的数据操作，先完成“增加”操作，则添加关于“增加”用户数据的抽象方法：

	Integer insert(User user);

然后，需要通过配置，使得MyBatis框架知道接口在哪里，所以，在Spring的配置文件中添加配置：

	<!-- 配置MapperScannerConfigurer -->
	<bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
		<!-- 指定MyBatis所需的接口在哪里 -->
		<property name="basePackage" value="cn.tedu.mybatis" />
	</bean>

## 2.5. 配置SQL语句

下载`SomeMapper.xml`文件，得到压缩包，在项目的**src/main/resources**下创建名为**mappers**的文件夹，解压下载的压缩包，将得到的xml文件复制到**mappers**文件夹中，并重命名为**UserMapper.xml**。

然后在该文件中配置SQL语句：

	<!-- namespace：当前XML文件用于配置哪个接口中抽象方法对应的SQL语句 -->
	<mapper namespace="cn.tedu.mybatis.UserMapper">
	
		<!-- 使用insert节点配置插入数据的SQL语句 -->
		<!-- id：抽象方法的方法名 -->
		<!-- 在#{}中间的是方法的参数User类中的属性名称 -->
		<insert id="insert">
			INSERT INTO t_user (
				username, password,
				age, phone,
				email
			) VALUES (
				#{username}, #{password},
				#{age}, #{phone},
				#{email}
			)
		</insert>
		
	</mapper>

最后，还是需要补充配置，**使得MyBatis框架知道这些XML文件在哪里，且执行时使用的数据源是哪一个**，则在Spring的配置文件中补充配置：

	<!-- SqlSessionFactoryBean -->
	<bean class="org.mybatis.spring.SqlSessionFactoryBean">
		<!-- XML文件在哪里 -->
		<property name="mapperLocations" value="classpath:mappers/*.xml" />
		<!-- 使用哪个数据源 -->
		<property name="dataSource" ref="dataSource" />
	</bean>

完成之后，在`Tests`中编写并执行单元测试：

	@Test
	public void insert() {
		ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("spring.xml");
		
		UserMapper userMapper = ac.getBean("userMapper", UserMapper.class);
		System.out.println(userMapper.getClass());
		
		User user = new User();
		user.setUsername("admin");
		user.setPassword("1234");
		Integer rows = userMapper.insert(user);
		System.out.println("rows=" + rows);
		
		ac.close();
	}

## 2.6. 实时获取新插入的数据的id

在配置`<insert>`节点时，添加`useGeneratedKeys="true"`和`keyProperty="id"`的配置：

	<insert id="insert" useGeneratedKeys="true" keyProperty="id">

然后，执行数据操作后，方法的参数对象中就会被封装自动编号的id值！

以上配置的2个属性，`useGeneratedKeys`表示“要不要获取自动生成的字段的值，即自动编号的值”，`keyProperty`表示“获取到的自动编号的值封装在参数对象的哪个属性中”，所以，在本例中，指的是`User`类中的`id`属性，并不是`t_user`表中的`id`字段。

> 通常，Property单词表示“属性”，类才有属性，数据表没有属性，Field表示“字段”，仅当描述数据表结构时才称之为字段，Column表示“列”，仅当描述查询结果时才称之为列。




### ----------------------------------

### 附1：如果快速的向数据表中插入已知的1000万条数据

假设这1000万条数据在1个`List`集合中，将这个集合进行遍历，循环1000万次，结合数据库编程技术，就可以将这些数据插入到数据库中。

这样做的缺陷：

1. 在实际工作环境中，应用服务器(程序运行所在的Tomcat服务器)与MySQL数据库服务器并不是同一台服务器，当需要执行数据操作时，会由应用服务器将SQL语句发送到MySQL数据库服务器，发送过程中就需要建立网络连接，才可以发送SQL语句，如果使用以上原始做法，就需要连接1000万次，每次发送1条SQL语句，效率非常低下！

2. 每次执行1条SQL语句之前，MySQL服务器还会对SQL语句进行词法分析、语义分析、编译等过程，才可以执行，假设有1000万条SQL语句，则这些词法分析、语义分析、编译等过程就需要经历1000万次！

针对问题1，可以使用批处理来解决，批处理可以一次性发送多条SQL语句到数据库服务器，减少传递SQL语句的次数，从而提高运行效率；

针对问题2，可以把`INSERT INTO xx () VALUES ();`这种语法调整为`INSERT INTO xx () VALUES (值列表1), (值列表2), ..., (值列表N)`，这种做法可以使得1条SQL语句插入多条数据，假设每条SQL语句插入了1000条数据，则只需要1万条SQL语句即可！

所以，总的来说，可以使用每条SQL语句插入100条数据，批处理时每次发送1000条这样的SQL语句，整体循环100次，就可以完成所有数据的插入！

> 理论上来说，批处理时，不建议一次性处理超过5000条SQL语句。另外，每条SQL语句也不是插入越多数据就越快，拼接这样的SQL语句也是需要耗时的。

### MySQL Review

1. 创建名为`tedu_ums`的数据库；

	CREATE DATABASE tedu_ums;

2. 创建名为`t_user`的数据表，该表中至少包含id、用户名、密码、年龄、手机号码、电子邮箱这6个字段，字段的数据类型和约束自行设计；

	CREATE TABLE t_user (
		id INT AUTO_INCREMENT COMMENT '用户id',
		username VARCHAR(100) NOT NULL UNIQUE COMMENT '用户名',
		password VARCHAR(20) NOT NULL COMMENT '密码',
		age INT COMMENT '年龄',
		phone VARCHAR(20) COMMENT '手机号码',
		email VARCHAR(50) COMMENT '电子邮箱',
		PRIMARY KEY (id)
	) DEFAULT CHARSET=UTF8;

3. 插入不少于10条用户数据，数据内容应该尽量完整且随机；

	INSERT INTO t_user (username, password, phone, email, age) VALUES ('root1', '123456', '13800138001', 'root@qq.com', 25),
	('root2', '123456', '13800138002', 'root@qq.com', 22),
	('root3', '123456', '13800138003', 'root@qq.com', 16),
	('root4', '123456', '13800138004', 'root@qq.com', 27),
	('root5', '123456', '13800138005', 'root@qq.com', 32),
	('root6', '123456', '13800138006', 'root@qq.com', 19),
	('root7', '123456', '13800138007', 'root@qq.com', 17),
	('root8', '123456', '13800138008', 'root@qq.com', 21),
	('root9', '123456', '13800138009', 'root@qq.com', 26),
	('root10', '123456', '13800138010', 'root@qq.com', 23),
	('root11', '123456', '13800138011', 'root@qq.com', 28),
	('root12', '123456', '13800138012', 'root@qq.com', 20),
	('root13', '123456', '13800138013', 'root@qq.com', 30);

4. 删除id=3的数据；

	DELETE FROM t_user WHERE id=3;

5. 删除id=1，id=6，id=7的数据；

	DELETE FROM t_user WHERE id=1 OR id=6 OR id=7;

	DELETE FROM t_user WHERE id IN (1, 6, 7);

6. 将id=2的用户的密码修改为`8888`；

	UPDATE t_user SET password='8888' WHERE id=2;

7. 将所有用户的密码修改为`1234`；

	UPDATE t_user SET password='1234';

8. 统计当前用户的数量；

	SELECT COUNT(*) FROM t_user;

9. 查询id=5的用户的信息；

	SELECT * FROM t_user WHERE id=5;

10. 查询所有的用户列表；

	SELECT * FROM t_user ORDER BY id ASC;

11. 查询所有用户中年龄最大的那个用户的信息（假设每个用户的年龄都不相同）。

	SELECT * FROM t_user ORDER BY age DESC LIMIT 0,1;