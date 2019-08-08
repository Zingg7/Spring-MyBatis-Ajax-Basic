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


