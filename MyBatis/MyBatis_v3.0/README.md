查询某个部门的信息，同时，获取该部门中所有用户的信息。

执行该操作的SQL语句大致是：

	SELECT 
		*
	FROM
		t_department
	LEFT JOIN
		t_user
	ON
		t_department.id=t_user.department_id
	WHERE
		t_department.id=?

执行以上查询，可能产生多条结果，因为某1个部门中可能有多个用户！但是，实际需求是“查询某个部门的信息”，结果应该只有1个数据！

为了合理的表示查询结果，可以定义部门信息的VO类`cn.tedu.mybatis.DepartmentVO`，该类的设计：

	public class DepartmentVO {
		private Integer id; // 部门id
		private String name; // 部门名称
		private List<User> users; // 部门中的用户
	}

在开发持久层功能时，由于操作的数据主体不再是“用户”数据，则以前的`UserMapper`接口就不再适用，应该先创建新的`cn.tedu.mybatis.DepartmentMapper`持久层接口，并在接口中定义“根据部门id查询部门详情”的抽象方法：

	public interface DepartmentMapper {
		
		DepartmentVO findVOById(Integer id);

	}

与添加新的接口的原因相同，也应该给出新的XML文件配置以上抽象方法的SQL语句，所以，可以把原有的`UserMapper.xml`复制并粘贴，然后重命名为`DepartmentMapper.xml`，删除原有的配置，再配置以上抽象方法的SQL语句：

	<select id="findVOById" resultType="xx.xx.xx.DepartmentVO">
		SELECT 
			t_department.id, name,
			t_user.id, username,
			password, age,
			phone, email,
			is_delete AS isDelete,
			department_id AS departmentId
		FROM
			t_department
		LEFT JOIN
			t_user
		ON
			t_department.id=t_user.department_id
		WHERE
			t_department.id=#{id}
	</select>

在以上查询结果中，会出现2个名为`id`的列名，后续在处理数据时，MyBatis将无法正确的区分所需要的`id`值是哪一列的数据，所以，在查询时，至少需要为以上2个`id`字段中的1个定义别名，或者将2个都定义不同的别名，保证查询结果中的列名都是唯一的，不会出现冲突：

	SELECT 
			t_department.id AS did, name,
			t_user.id AS uid ... ...

在配置`<resultMap>`时，关于唯一字段的配置，应该使用`<id />`节点进行配置，而不应该使用`<result />`节点来配置，2个节点的配置方式完全相同，即使不使用`<id />`而使用`<result />`也能实现功能，但是，MyBatis是天生自带缓存的，使用`<id />`节点配置的数据会作为缓存数据的标识，而使用`<result />`节点的配置则不会。

在配置1对多关系时，需要使用`<collection>`节点，例如1个部门有多个用户，在数据方面，1个部门的VO对象中有1个`List`集合存储多个`User`数据。

完整的`<resultMap>`配置应该是：

	<resultMap id="DepartmentVOMap" type="cn.tedu.mybatis.DepartmentVO">
		<id column="did" property="id"/>
		<result column="name" property="name"/>
		<!-- collection节点：用于配置集合类型的属性 -->
		<!-- property：依然表示类的属性 -->
		<!-- ofType：集合里面放的是什么类型的数据 -->
		<collection property="users" ofType="cn.tedu.mybatis.User">
			<!-- column：依然是查询结果中的列名 -->
			<!-- property：ofType的类型中的属性名 -->
			<id column="uid" property="id"/>
			<result column="username" property="username"/>
			<result column="password" property="password"/>
			<result column="age" property="age"/>
			<result column="phone" property="phone"/>
			<result column="email" property="email"/>
			<result column="is_delete" property="isDelete"/>
			<result column="department_id" property="departmentId"/>
		</collection>
	</resultMap>

查询节点的配置为：

	<select id="findVOById" resultMap="DepartmentVOMap">
		SELECT 
			t_department.id AS did, name,
			t_user.id AS uid, username,
			password, age,
			phone, email,
			is_delete, department_id
		FROM
			t_department
		LEFT JOIN
			t_user
		ON
			t_department.id=t_user.department_id
		WHERE
			t_department.id=#{id}
	</select>

注意：在使用了`<resultMap>`的查询中，并不需要因为查询结果的列名与类的属性名不同而定义别名，例如以上的`is_delete`和`department_id`就没有再定义别名，而只需要为名字冲突的定义别名，例如以上的用户表的id和部门表的id。
