
### 1.7.1. 通过SET方式注入属性的值

假设存在：

	public class UserDao {
		
		public String driver; // com.mysql.jdbc.Driver
	
	}

如果需要为`driver`注入值，首先需要为该属性添加SET方法（强烈建议使用开发工具生成SET方法，不要手动编写）：

	public void setDriver(String driver) {
		this.driver = driver;
	}

然后，在Spring的配置文件中，添加`<property>`节点以进行配置：

	<!-- 通过SET方式注入基本值 -->
	<bean id="userDao" class="cn.tedu.spring.UserDao">
		<property name="driver" value="com.mysql.jdbc.Driver" />
	</bean>

另外，如果某个属性的值并不是基本值（在Spring中，把基本数据类型的值和字符串统一称为基本值），例如：

	public class UserServlet {
		public UserDao userDao;
	}

以上属性的值应该是前序创建的`UserDao`类的对象，则，在配置时，可以通过`<property>`节点的`ref`属性引用那个`<bean>`的`id`：
	<!-- 通过SET方式注入引用值 -->
	<bean id="userServlet" class="cn.tedu.spring.UserServlet">
		<property name="userDao" ref="userDao" />
	</bean>

> 综合来看，无论属性的值是什么类型，只要是通过SET方式注入属性值，首先都必须为属性添加SET方法，然后在`<bean>`节点下级通过`<property>`节点进行配置，如果属性的值是基本值，则使用`value`属性直接编写对应的值，如果属性的值不是基本值，则使用`ref`属性引用另一个`<bean>`的`id`（如果没有所说的另一个`<bean>`，就想办法配出这样一个`<bean>`）。

### 1.7.2. 通过构造方法注入属性的值

假设存在：

	public class AdminDao {
		
		public String url;

		public AdminDao(String url) {
			super();
			this.url = url;
		}

	}

在配置时：

	<!-- 通过构造方法注入属性值 -->
	<bean id="adminDao" class="cn.tedu.spring.AdminDao">
		<!-- index：参数的序号，即第几个参数 -->
		<constructor-arg index="0" value="jdbc:mysql://..." />
	</bean>

练习：创建`AdminServlet`类，在类中声明`public String name;`和`public AdminDao adminDao;`这2个属性，通过1个构造方法为这2个属性注入值，其中，`name`的值是`"处理管理员请求的类"`，`adminDao`的值就是此前创建的`AdminDao`的对象。

Java类：

	public class AdminServlet {
		
		public String name;
		public AdminDao adminDao;
		
		public AdminServlet(String name, AdminDao adminDao) {
			super();
			this.name = name;
			this.adminDao = adminDao;
		}	
	}

配置：

	<!-- 通过构造方法注入多个属性值 -->
	<bean id="adminServlet" class="cn.tedu.spring.AdminServlet">
		<constructor-arg index="0"
			value="处理管理员请求的类" />
		<constructor-arg index="1"
			ref="adminDao" />
	</bean>

### 1.7.3. 小结

通过SET方式注入必须为属性添加规范的SET方法，在配置时，使用`<property>`节点注入属性的值，该节点中，`name`属性可以理解为就是属性名称；

通过构造方法注入必须自定义带参数的构造方法，且构造方法会基于参数为属性赋值；

无论通过哪种方式，如果注入的值是基本值，通过`value`属性配置，如果注入的值是引用其他Bean，通过`ref`引用那个`<bean>`的`id`。

通常推荐为绝大部分类提供无参数的构造方法，所以，通过SET方式注入是比较实用的做法，而通过构造方法注入的使用频率就非常低。

## 1.8. 注入集合类型的属性值

#### 1.8.1. List

假设存在：

	// Frank, Andy, Lucy, Kate
	public List<String> names;

如果希望通过SET方式注入属性的值，需要先生成SET方法，然后，配置为：

	<!-- 注入List类型的值：Frank, Andy, Lucy, Kate -->
	<property name="names">
		<list>
			<value>Frank</value>
			<value>Andy</value>
			<value>Lucy</value>
			<value>Kate</value>
		</list>
	</property>

Spring框架在处理时，会使用`ArrayList`封装`List`类型的属性的值。

### 1.8.2. Set

假设存在：

	// Beijing, Shanghai, Guangzhou, Shenzhen
	public Set<String> cities;

则配置为：

	<!-- 注入Set类型的值：Beijing, Shanghai, Guangzhou, Shenzhen -->
	<property name="cities">
		<set>
			<value>Beijing</value>
			<value>Shanghai</value>
			<value>Guangzhou</value>
			<value>Shenzhen</value>
		</set>
	</property>

Spring框架在处理时，会使用`LinkedHashSet`封装`Set`类型的属性的值。

### 1.8.3. 数组

假设存在：

	// { 9, 5, 2, 7 } 
	public int[] numbers;

则配置为：

	<!-- 注入数组类型的值：{ 9, 5, 2, 7 } -->
	<property name="numbers">
		<array>
			<value>9</value>
			<value>5</value>
			<value>2</value>
			<value>7</value>
		</array>
	</property>

其实，在Spring框架中，注入`List`集合类型的值和数组类型的值时，使用`<list>`节点或者`<array>`节点都是可以的，即：数据是`List`类型的，使用`<array>`来配置，或者数据是数组类型的，使用`<list>`来配置，都是正确的。但是，在实际应用时，还是应该注意区分。

### 1.8.4. Map

假设存在：

	// username=root, password=1234, from=Hangzhou, age=26
	public Map<String, String> session;

则配置为：

	<!-- 注入Map类型的值：username=root, password=1234, from=Hangzhou, age=26 -->
	<property name="session">
		<map>
			<entry key="username" value="root" />
			<entry key="password" value="1234" />
			<entry key="from" value="Hangzhou" />
			<entry key="age" value="26" />
		</map>
	</property>

### 1.8.5. Properties

在配置`Properties`类型的属性值时，其配置的节点结构是：

	<!-- 注入Properties类型的值 -->
	<property name="config">
		<props>
			<prop key="username">root</prop>
			<prop key="password">1234</prop>
		</props>
	</property>

另外，也可以准备专门的**.properties**文件，例如在**src/main/resources**下创建**db.properties**文件：

	url=jdbc:mysql://localhost:3306/db_name?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
	driver=com.mysql.jdbc.Driver
	username=root
	password=1234

然后，在Spring的配置文件中，使用`<util:properties>`节点就可以直接读取**.properties**文件：

	<util:properties id="config" 
		location="classpath:db.properties" />

以上`<util:properties>`节点也是一种`<bean>`节点，所以，注入值时，可以通过`ref`引用这个节点：

	<!-- 注入Properties类型的值 -->
	<property name="config" ref="config" />

## 1.9. Spring表达式

Spring表达式是使用占位符的方式定义在Spring的配置文件中的，作用是用于获取其他`<bean>`中的属性的值！

假设存在：

	public class ValueBean {
		// 值是SampleBean中的names中的第2个
		public String name;
		
	}

首先，需要确定注入值的方式，可以是通过SET方式注入，也可以通过构造方法注入，如果选择通过SET方式注入，需要先生成SET方法：

	public void setName(String name) {
		this.name = name;
	}

然后，在Spring的配置文件中进行配置：

	<!-- 使用Spring表达式 -->
	<bean id="valueBean" class="cn.tedu.spring.ValueBean">
		<!-- 值是SampleBean中的names中的第2个 -->
		<property name="name"
			value="#{sampleBean.names[1]}" />
	</bean>

可以发现，Spring表达式的基本格式是使用`#{}`进行占位，其内部语法是：

	#{bean的id.属性}

如果属性的类型是`List`集合、`Set`集合或者数组，则在属性右侧使用`[]`写出索引或者下标：

	#{bean的id.属性[索引或者下标]}

如果属性的类型是`Map`集合或者`Properties`，可以使用的语法：

	#{bean的id.属性.key}

	#{bean的id.属性['key']}

## 1.10. 自动装配(autowire)

自动装配：不需要在Spring的配置文件中进行属性值的注入，只需要配置允许自动装配，Spring就会自动的完成属性值的注入。

假设存在`StudentServlet`依赖于`StudentDao`：

	public class StudentServlet {
		
		public StudentDao studentDao;
		
		public void setStudentDao(StudentDao studentDao) {
			this.studentDao = studentDao;
		}
	
		public void reg() {
			System.out.println("StudentServlet.reg()");
			studentDao.reg();
		}
	
	}

	public class StudentDao {
		
		public void reg() {
			System.out.println("StudentDao.reg()");
		}
	
	}

就可以配置为：

	<bean id="studentDao"
		class="cn.tedu.spring.StudentDao" />
	<bean id="studentServlet"  class="cn.tedu.spring.StudentServlet" autowire="byName">
	</bean>

以上配置中，`autowire`属性就是用于配置自动装配的。

当取值是`byName`时，表示“按照名称自动装配”，在这个过程中，Spring会先检测到在`StudentServlet`中有名为`studentDao`的属性，会根据该属性得到`setStudentDao`这个方法名称，然后，尝试找到与SET方法名称对应的bean的id，即查找某个id为`studentDao`的`<bean>`，如果找到，则自动调用`setStudentDao`方法来完成自动装配，如果没有找到匹配的bean-id，则不会尝试自动装配。简单的来说，就是**SET方法的名称需要与bean-id相对应**，属性的名称可以和bean-id不对应。自动装配是一种“尝试性”的操作，能装就装，装不上也不会报错！

另外，还可以取值为`byType`，表示“按照类型自动装配”，在这个过程，Spring会检测`StudentServlet`中以`set`作为前缀的方法，并尝试调用这些方法，调用时，**会在Spring容器中查找与参数类型相符合的bean，如果没有匹配的对象，则不自动装配，如果找到1个，则执行该方法，以完成自动装配，如果找到2个或者更多，则直接报错错误**。

还有其它装配模式，一般不必了解。

**在实际开发时，并不会使用这种方式来实现自动装配**，因为这种做法存在的问题：属性是否被装配了，表现的不明显，如果不详细的阅读完整的源代码，根本无法确定类中的哪些属性被装配了值，而哪些属性没有被装配值！

目前，主要理解自动装配的思想，及`byName`和`byType`这2种装配模式的特性即可。


#### 附2：List与Set

List中的元素是可以重复的，例如在同一个List中存储2个`string-1`，而Set中的元素是不可以重复的，例如在同一个Set中添加2个`string-1`，实际只会存储第1次添加的`string-1`，第2次添加的是相同的数据，则不会被存储下来！判断“元素是否相同”的标准是：调用`equals()`对比的结果是`true`，并且2个元素的`hashCode()`返回值相同。

List是序列的集合，典型的实现类有`ArrayList`和`LinkedList`，其中，`ArrayList`查询效率高，但是修改效率低，而`LinkedList`查询效率低，但是修改效率高。

Set是散列的集合，从实际表现来看，并不能说Set是无序的，例如`TreeSet`会把元素按照字典排序法进行排序，如果元素是自定义的数据类型，需要该类型实现`Comparable`接口，重写其中的`int compareTo(T other)`方法，实际上`TreeSet`会调用各元素的`compareTo()`方法实现排序，这个排序过程是运行时执行的，从数据存储的角度来看，数据在内存中依然是散列的，另外，还有`LinkedHashSet`是通过链表的形式将各个元素“链”起来的，所以，输出显示这种Set时，输出结果与添加元素的顺序是保持一致的！

所有Set都是只有key没有value的Map。