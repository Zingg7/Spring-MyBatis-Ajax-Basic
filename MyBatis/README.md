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

## 2.3. 配置数据库连接

在**src/main/resources**下创建**db.properties**文件，以确定数据库连接的相关配置：

	url=jdbc:mysql://localhost:3306/tedu_ums?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
	driver=com.mysql.cj.jdbc.Driver
	username=root
	password=root
	initialSize=2
	maxActive=5

然后，需要在Spring的配置文件中读取以上配置信息：

	<!-- 读取db.properties -->
	<util:properties id="dbConfig"
		location="classpath:db.properties" />

最终，程序运行时，需要使用的数据源是`BasicDataSource`，框架会通过这个类的对象获取数据库连接对象，然后实现数据访问，所以，就需要为这个类的相关属性注入值，把数据库配置信息确定下来：
	
	<!-- 配置数据源 -->
	<bean id="dataSource"
		class="org.apache.commons.dbcp.BasicDataSource">
		<property name="url" 
			value="#{dbConfig.url}" />
		<property name="driverClassName" 
			value="#{dbConfig.driver}" />
		<property name="username" 
			value="#{dbConfig.username}" />
		<property name="password" 
			value="#{dbConfig.password}" />
		<property name="initialSize" 
			value="#{dbConfig.initialSize}" />
		<property name="maxActive" 
			value="#{dbConfig.maxActive}" />
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

然后，需要通过配置，是的MyBatis框架知道接口在哪里，所以，在Spring的配置文件中添加配置：

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

最后，还是需要补充配置，使得MyBatis框架知道这些XML文件在哪里，且执行时使用的数据源是哪一个，则在Spring的配置文件中补充配置：

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

## 2.7. 使用MyBatis实现简单的查询

在设计查询的抽象方法时：

1. 返回值类型使用所期望的类型；

2. 其它部分的设计与增/删/改相同；

3. 如果查询的是某1条数据记录，如果有匹配的数据，则返回正确的查询结果，如果没有匹配的数据，将返回`null`。

例如：根据用户id查询用户数据详情时：

	User findById(Integer id);

在配置该方法的XML映射时，使用的`<select>`节点必须配置`resultType`或者`resultMap`属性中的某一个：

	<select id="findById" resultType="cn.tedu.mybatis.User">
		SELECT * FROM t_user WHERE id=#{id}
	</select>

例如：获取当前数据表用户数据的数量：

	Integer count();

映射配置为：

	<select id="count" resultType="java.lang.Integer">
		SELECT COUNT(*) FROM t_user
	</select>

例如：查询所有用户数据时：

	List<User> findAll();
	
配置的映射：

	<select id="findAll" resultType="cn.tedu.mybatis.User">
		SELECT * FROM t_user ORDER BY id ASC
	</select>

## 2.8. 使用多个参数

假设需要实现：将id=?的用户的密码修改为?，则抽象方法：

	Integer updatePasswordById(Integer id, String newPassword);

然后配置xml中的映射：

	<update id="updatePasswordById">
		UPDATE t_user SET password=#{newPassword} where id=#{id}
	</update>

如果直接执行以上代码，会报告错误：

	org.mybatis.spring.MyBatisSystemException: nested exception is org.apache.ibatis.binding.BindingException: Parameter 'newPassword' not found. Available parameters are [arg1, arg0, param1, param2]
	
根本原因是MyBatis框架只能识别1个参数，无论这个参数是什么，都会直接被使用，而多余的参数是不可识别的！

可以在抽象方法的每一个参数之前添加`@Param`注解，MyBatis框架在处理时，会将这些参数封装成1个Map，依然能满足“只能识别1个参数”的需求，后续在配置XML映射时，使用的`#{}`占位符中的名称就必须是注解中配置的名称，表示的是MyBatis自动封装的Map中的Key：

	Integer updatePasswordById(@Param("id") Integer arg0, @Param("password") String arg1);

配置的XML映射：

	<update id="updatePasswordById">
		UPDATE t_user SET password=#{password} where id=#{id}
	</update>

**小结：如果涉及的抽象方法的参数达到2个甚至更多，则每个参数之前都必须添加`@Param`注解，并在注解中指定名称，后续配置XML映射时，使用的`#{}`中的名称也是注解中配置的名称！**

练习：同时根据用户名和密码查询用户数据

## 2.9. 动态SQL-foreach

MyBatis中的动态SQL指的是根据参数不同，动态的生成不同的SQL语句。

例如：根据若干个id删除用户数据，设计的抽象方法：

	Integer deleteByIds(List<Integer> ids);

然后，在配置映射时：

	<delete id="deleteByIds">
		DELETE FROM 
			t_user 
		WHERE 
			id 
		IN 
			(
			<foreach collection="list"
				item="id" separator=",">
				#{id}
			</foreach>
			)
	</delete>


在配置`<foreach>`节点时：

- `collection`：被遍历的参数对象，首先，如果对应的抽象方法的参数只有1个时，如果参数是`List`集合类型的，取值为`list`，如果参数是数组类型的，取值为`array`，另外，如果对应的抽象方法的参数有多个，则每个参数肯定都添加了`@Param`注解，此处需要配置的值就是注解中配置的名称；

- `item`：遍历过程中获取到的数据的名称，相当于增强for循环的语法中，括号中的第2个部分，在`<foreach>`节点的子级可以使用`#{}`占位符，占位符中的名称就是`item`属性的值；

- `separator`：遍历过程中各元素使用的分隔符；

- `open`和`close`：遍历产生的代码的最左侧字符和最右侧字符。

## 2.10. 动态SQL-if

假设存在抽象方法：

	List<User> find(String where, String orderBy, Integer offset, Integer count);

在配置SQL语句时，可以使用if标签进行对参数的判断，从而产生不同的SQL语句的某个部分，例如：

	<select id="xx" resultType="xx.xx.xx.User">
		SELECT
			*
		FROM
			t_user
		<if test="where != null">
		WHERE
			#{where}
		</if>
		<if test="orderBy != null">
		ORDER BY
			#{orderBy}
		</if>
		<if test="offset != null">
		LIMIT
			#{offset}, #{count}
		</if>
	</select>

以上配置是错误的，并不能所有位置都使用`#{}`占位符，有几处需要使用`${}`格式的占位符：

	<select id="find" 
		resultType="cn.tedu.mybatis.User">
		SELECT
			*
		FROM
			t_user
		<if test="where != null">
		WHERE
			${where}
		</if>
		<if test="orderBy != null">
		ORDER BY
			${orderBy}
		</if>
		<if test="offset != null">
		LIMIT
			#{offset}, #{count}
		</if>
	</select>

## 2.11. MyBatis中的占位符

在MyBatis中，编写XML中的SQL语句时，可以使用`#{}`格式的占位符，还可以使用`${}`格式的占位符！

MyBatis在处理有占位符的SQL时，如果是`${}`格式的占位符，会先通过字符串拼接的方式把变量值替换并拼接出SQL语句，然后尝试编译该SQL语句，如果是`#{}`格式的占位符，会使用`?`进行占位并尝试编译，编译过后再使用值进行替换。

小结：以前在使用JDBC时，可以使用`?`表示的部分，都应该使用`#{}`，也可以理解为只有“值”才可以使用`#{}`，这种做法是预编译的，否则，如果要对SQL语句中的某个子句或者其他语句的某个部分，甚至是WHERE子句中的表达式，使用`${}`，这种做法并不会预编译。

### 6. 当字段名与属性名不完全相同时的查询

修改`t_user`表结构，添加名为`is_delete`的字段：

	alter table t_user add column is_delete int;

	update t_user set is_delete=0;

然后，对应的`User`类中也需要添加对应的属性，在Java中，不推荐在变量名中使用`_`符号，所以，添加的属性应该是：

	private Integer isDelete;

由于名称不是完全相同了，所以，此前的查询功能就无法查询出数据的`is_delete`字段的值！

MyBatis封装查询结果的标准就是“将查询到的数据封装到与列名完全相同的属性中”，所以，如果字段名与属性名不一致，就会导致默认的列名与属性名不一致，可以在查询时，为列名自定义别名，以保持名称一致，所以，解决方案可以是：

	SELECT 
		id, username,
		password, age,
		phone, email,
		is_delete AS isDelete
	FROM 
		t_user 
	ORDER BY 
		id ASC

如果在查询时，使用`*`表示要查询的字段列表，MyBatis就无法自动封装那些名称不一致的数据，可以在XML文件配置`<resultMap>`节点，并且在查询的`<select>`节点中，使用`resultMap`属性取代`resultType`属性：

	<!-- resultMap节点：指导MyBatis如何封装查询结果 -->
	<!-- id：自定义名称 -->
	<!-- column：查询结果中的列名 -->
	<!-- property：type对应的类中的属性名 -->
	<resultMap id="UserEntityMap" type="cn.tedu.mybatis.User">
		<result column="id" property="id" />
		<result column="username" property="username" />
		<result column="password" property="password" />
		<result column="age" property="age" />
		<result column="phone" property="phone" />
		<result column="email" property="email" />
		<result column="is_delete" property="isDelete" />
	</resultMap>

	<select id="findAll" resultMap="UserEntityMap">
		SELECT 
			*
		FROM 
			t_user 
		ORDER BY 
			id ASC
	</select>

**小结：无论是取别名，还是配置`<resultMap>`，只要能保证MyBatis知道如何封装名称不一致的数据，就可以正确查询到所需要的结果！如果查询时不用`*`表示字段列表，且名称不一致的字段较少，则可以优先考虑使用别名，如果使用`*`查询，或者名称不一致的字段较多，则应该优先考虑配置`<resultMap>`。**

## 2.12. 关联表查询

创建`t_department`部门信息表，要求表中存在`id`和`name`这2个字段，向表中插入不少于3条数据：

	CREATE TABLE t_department (
		id INT AUTO_INCREMENT COMMENT '部门id',
		name VARCHAR(50) NOT NULL UNIQUE COMMENT '部门名称',
		PRIMARY KEY (id)
	) DEFAULT CHARSET=UTF8;

	INSERT INTO t_department (name) VALUES ('软件研发部'), ('人力资源部'), ('财务部');

在`t_user`表中添加`department_id`字段，为每一个用户数据分配部门id：

	ALTER TABLE t_user ADD COLUMN department_id INT;

	UPDATE t_user SET department_id=1 WHERE id IN (2,10,17);
	UPDATE t_user SET department_id=2 WHERE id IN (16,14,12);
	UPDATE t_user SET department_id=3 WHERE id IN (4,13);

假设存在需求：根据id查询某用户详情，要求直接显示用户所在部门的名称。

首先，直接查询`t_user`表是不足以得到完整答案的，为了保证数据表管理的规范，在`t_user`表中只会存储部门的`id`，并不会存储部门的`name`，所以，需要实现该需求，就必须使用关联查询：

	SELECT 
		t_user.id, username,
		phone, email,
		password, age,
		is_delete AS isDelete,
		department_id AS departmentId,
		name AS departmentName
	FROM 
		t_user 
	LEFT JOIN
		t_department 
	ON 
		department_id=t_department.id
	WHERE
		t_user.id=?

这样的查询可以符合当前需求，但是，在设计代码时，却没有任何一个实体类可以封装以上查询结果！因为实体类都是与数据表一一相对应的，所以就需要另外创建VO(Value Object)类，VO类的设计原则是根据查询结果来确定各属性的：

	public class UserVO {
		private Integer id;
		private String username;
		private String password;
		private Integer age;
		private String phone;
		private String email;
		private Integer isDelete;
		private Integer departmentId;
		private String departmentName;
		// SET/GET/hashCode/equals/toString/Serializable
	}

其实，VO类与实体类的设计方式是几乎一样的，只是定位不同，实体类与数据表对应，VO类与查询结果对应。

设计的抽象方法的返回值就应该是`UserVO`：

	UserVO findUserVOById(Integer id);

然后，配置映射时，需要注意自定义别名，或者配置`<resultMap>`，同时，注意：如果某个字段名在2张或者涉及的多张表中都存在，必须明确的指定表名，例如这2张表中都有`id`字段，每次涉及该字段都必须在左侧指定表名：

	<select id="findUserVOById" resultType="cn.tedu.mybatis.UserVO">
		SELECT 
			t_user.id, username,
			phone, email,
			password, age,
			is_delete AS isDelete,
			department_id AS departmentId,
			name AS departmentName
		FROM 
			t_user 
		LEFT JOIN
			t_department 
		ON 
			department_id=t_department.id
		WHERE
			t_user.id=#{id}
	</select>


