# 1. Spring框架

## 1.1. 框架

框架是它人编写好的一段程序，可能表现为一系列的jar包，每种框架都解决了某些问题，当需要时，在开发项目时添加相关的jar包，使用这些框架，在开发项目时就可以不必关心某些细节问题。

许多框架都约定了特定的编程方式，是对传统编程方式的改进，大多数框架可以使得编程更加简单。

使用框架的同时，也需要遵守框架的运行机制和相关的约定。

## 1.2. 需要掌握的框架

主要学习SSM框架，即：Spring + SpringMVC + Mybatis。

另有SpringBoot框架。

## 1.3. Spring框架的作用

Spring框架的主要作用是创建和管理对象！

Spring框架可以实现项目中各组件的**解耦(解除耦合度)**，**降低**项目中各组件之间的**依赖**关系！

假设存在用户注册的处理：

	public class UserServlet {

		public UserDao userDao = new UserDao();

		public void doPost() {
			// 调用UserDao对象的reg()方法实现注册
			userDao.reg();
		}

	}

	public class UserDao {
		
		public void reg() {
		}

	}

在以上代码中，UserServlet本身是不处理JDBC相关操作的，当需要执行注册(向数据库中插入数据)时，将调用UserDao对象的方法来执行，则称之为“UserServlet依赖了UserDao”！

假设UserDao中的代码有问题，例如编写的执行效率不高，或者有浪费资源，甚至后期需要使用新技术来实现，都可能导致UserDao需要被替换另一个新的例如UserDao2，当出现这种替换需求时，如果原有的代码需要进行较多的改动，则称之为“耦合度较高”，反之，如果原有的代码几乎不需要改动，则称之为“耦合度较低”，通常，提倡使用耦合度低的编码方式！

以上演示代码中，体现了依赖关系的就是在`UserServlet`中的：

	public UserDao userDao = new UserDao();

如果需要将`UserDao`替换为`UserDao2`，则代码需要改为：

	public UserDao2 userDao = new UserDao2();

当需要优化此代码时，可以：

	public interface IUserDao {
		void reg();
	}
	
	public class UserDao implements IUserDao {
	}

	public class UserDao2 implements IUserDao {
	} 

则原有的代码就可以调整为使用接口声明所需要的对象：

	public IUserDao userDao = new UserDao2();

则无论使用哪个实现类，以上代码中的声明部分(左侧)是不需要调整的！

另外，还可以通过设计模式中的工厂模式来创建对象：

	public class UserDaoFactory {
		public static IUserDao newInstance() {
			return new UserDao2();
		}
	}

然后，代码可以进一步调整为：

	public IUserDao userDao = UserDaoFactory.newInstance();

则后续需要使用`IUserDao`对象时，声明成以上代码即可，如果需要更换具体的实现类对象，也只需要修改工厂类中的返回值，类似以上声明的代码，无论在项目中出现过多少次，都是不需要调整的！

所以，总的看来，通过工厂来创建对象，前期编写的代码会更多一些，但是，可以实现“解耦”的效果，后期的管理难度会大大的降低！

在实际项目开发中，不可能为所有的类或者大量的类去设计专属工厂，而Spring框架就相当于一个大工厂，**可以生产我们配置的、希望它去生产的所有对象！后续，当需要获取对象时，直接通过Spring获取即可，并不需要自己创建对象。**所以，Spring框架也被称之为“Spring容器”。

## 1.4. Spring Demo

创建`Maven Project`，创建时勾选`Create a simple project`，**Group Id**输入`spring`，**Artifact Id**输入`SPRING-01`(本应该也是包名的一部分，但是，暂且作为项目名)，**Packaging**选择`war`(也可以是jar，后续实际使用的其实都会是war)。

创建出来的项目默认没有**web.xml**文件，作为WEB项目来说是错误的，所以，需要先生成该文件。

然后，需要添加所以依赖的框架，在**pom.xml**中添加`<dependencies>`节点，然后，在子级中添加`spring-webmvc`的依赖(其实，目前只要使用`spring-context`依赖，而`spring-webmvc`中包含该依赖，后续学习SpringMVC框架时就必须使用`spring-webmvc`)：

	<dependency>
		<groupId>org.springframework</groupId>
		<artifactId>spring-webmvc</artifactId>
		<version>4.3.8.RELEASE</version>
	</dependency>

如果下载的jar包有问题，可以将版本替换为`4.3.0`至`4.3.16`中除了`4.3.11`和`4.3.15`以外的任何版本并再次尝试。

目前，对使用的Spring的要求是**不低于4.2版本**。

当项目创建完成后，下载spring-context.zip文件，将压缩包中的文件**applicationContext.xml**复制到项目中的**src/main/resources**中。

假设希望通过Spring创建`java.util.Date`类的对象，则应该在**applicationContext.xml**中添加配置：

	<!-- id:当从Spring容器中获取该对象时使用的名称 -->
	<!-- class:需要创建的类 -->
	<bean id="date" class="java.util.Date"></bean>

接下来，可以通过单元测试查看配置效果，需要在`pom.xml`中添加先添加单元测试的依赖：

	<dependency>
		<groupId>junit</groupId>
		<artifactId>junit</artifactId>
		<version>4.12</version>
	</dependency>

然后，在**src/test/java**下创建`cn.tedu.spring.TestCase`测试类：

	public class TestCase {	
		@Test
		public void getDate() {
			// 1. 加载Spring配置文件，获得Spring容器
			ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("applicationContext.xml");
			
			// 2. 从Spring容器中获取对象
			Date date = (Date) ac.getBean("date");
			
			// 3. 测试所获取的对象
			System.out.println(date);
			
			// 4. 关闭Spring容器，释放资源
			ac.close();
		}
	}

## 1.5. 通过Spring创建对象的方式

**(a) 类中存在无参数的构造方法（实用，掌握）**

以`java.util.Date`类为例，当Spring创建对象时，会自动调用其无参数构造方法。

在Spring的配置文件中，配置方式为：

	<bean id="date" class="java.util.Date"></bean>

如果某个类中并不存在无参数的构造方法，则不可以通过这种方式进行配置。

**(b) 类中存在静态工厂方法（不实用，了解）**

如果某个类中存在某个`static`修饰的方法，且方法的返回值类型就是当前类的类型，例如`java.util.Calendar`类就是如此：

	public abstract class Calendar {
		public static Calendar getInstance() {
			// ...
		}
	}	

符合这种条件的类，可以配置为：

	<!-- (b) 类中存在静态工厂方法 -->
	<!-- factory-method:工厂方法的名称 -->
	<bean id="calendar" 
		class="java.util.Calendar"
		factory-method="getInstance">
	</bean>

**(c) 存在实例工厂方法（更不实用，了解）**

在其他类中，存在某个工厂方法，可以创建指定的类的对象，例如：

	public class Phone {
		public Phone(String name) {
		}
	}

	public class PhoneFactory {
		public Phone newInstance() {
			return new Phone("HuaWei");
		}
	}

当需要Spring创建`Phone`对象时，可以让Spring先创建`PhoneFactory`的对象(实例)，然后调用该对象的`newInstance()`方法，即可获取`Phone`的对象，从而完成对象的创建过程。

具体配置为：

	<!-- (c) 存在实例工厂方法 -->
	<!-- factory-bean:配置为工厂类的bean-id -->
	<bean id="phoneFactory" class="cn.tedu.spring.PhoneFactory">
	</bean>
	<bean id="phone" class="cn.tedu.spring.Phone" factory-bean="phoneFactory" factory-method="newInstance">
	</bean>

**小结**

一般情况下，应该保证类中存在无参数构造方法，便于Spring创建和管理对象。

以上列举了3种情况，可以使得Spring创建对象，但是，通过静态工厂方法和通过实例工厂方法创建对象的要求比较苛刻，一般不使用！

另外，并不代表其他情况下不可以由Spring创建对象，例如某个类的构造方法都是有参数的，也可以通过配置来创建对象（参见后续的知识点：通过构造方法注入属性的值）。

## 1.6. 由Spring管理的对象的作用域与生命周期

**(a) 由Spring管理的对象的作用域**

单例：单一实例，在某个时刻，可以获取到的同一个类的对象将有且仅有1个，如果反复获取，并不会得到多个实例。

单例是一种设计模式，其代码格式可以是：

	public class King {
		private static King king = new King();

		private King() {
		}

		public static King getInstance() {
			return king;
		}
	}

以上单例模式也称之为“饿汉式单例模式”，其工作特性为“程序运行初期就已经创建了该类的实例，随时获取实例时，都可以直接获取”，还有另外一种单例模式是“懒汉式单例模式”，其工作特性为“不到逼不得已不会创建类的对象”，其代码是：

	public class King {
		private static final Object LOCK = new Object();

		private static King king;

		private King() {
		}

		public static King getInstance() {
			if (king == null) {
				synchronized (LOCK) {
					if (king == null) {
						king = new King();
					}
				}
			}
			return king;
		}
	}

理论上来说，懒汉式的单例模式可能可以节省一部分性能消耗，例如整个系统运行了30分钟之后才获取对象的话，则前30分钟是不需要创建对象的！但是，这种优势只是理论上的优势，在实际应用时，2种单例模式的差异表现的并不明显！

通过以上单例模式的设计方式，可以发现单例的对象都是使用了`static`修饰的！所以，具有“唯一、常驻”的特性！

在Spring管理对象时，可以配置`lazy-init`属性，以配置是否为懒汉式的单例模式：

	<!-- lazy-init：是否懒加载，取值为true或false(默认) -->
	<bean id="user" class="cn.tedu.spring.User" lazy-init="true">
	</bean>

在Spring管理对象时，可以配置`scope`属性，以配置类的对象是否是单例的：

	<!-- scope:是否单例，取值为singleton(默认，单例)或prototype(非单例) -->
	<bean id="user" class="cn.tedu.spring.User" scope="prototype">
	</bean>

**(b) 由Spring管理的对象的生命周期**

生命周期描述了某个对象从创建到销毁的整个历程，在这个历程中，会被调用某些方法，这些方法就是生命周期方法，学习生命周期的意义在于了解这些方法的调用特征，例如何时调用、调用次数，以至于开发功能时，可以将正确的代码重写在正确的方法中！

以Servlet为例，其生命周期方法主要有`init()`、`service()`、`destroy()`，其中，`init()`和`destroy()`都是只执行1次的方法，而`service()`在每次接收到请求时都会被调用，且`init()`方法是创建对象之后就会执行的方法，`destroy()`是销毁对象的前一刻执行的方法，所以，如果有某个流对象需要初始化，初始化的代码写在`destroy()`方法中就是不合适的！反之，如果要销毁流对象，也不能把代码写在`init()`中！

由Spring管理的单例对象也并不是开发人员完全可控的，即不知道何时创建的，何时销毁的，所以，Spring提供了“允许自定义初始化方法和销毁方法”的做法，开发人员可以自行定义2个方法分别表示初始化方法和销毁方法：

	public class User {
	
		public User() {
			super();
			System.out.println("创建User对象……");
		}
		
		public void init() {
			System.out.println("初始化方法被调用……");
		}
		
		public void destroy() {
			System.out.println("销毁方法被调用……");
		}
		
	}

配置：

	<!-- init-method：初始化方法的名称 -->
	<!-- destroy-method：销毁方法的名称 -->
	<bean id="user" class="cn.tedu.spring.User" init-method="init" destroy-method="destroy">
	</bean>

注意：生命周期方法的配置是建立在“单例”基础之上的，如果对象并不是单例的，讨论生命周期就没有意义了。

## 1.7. Spring IoC

IoC：Inversion of Control，即“控制反转”，在传统的开发模式中，创建对象和管理对象都是由开发人员自行编写代码来完成的，可以理解为“开发人员拥有控制权”，在使用了Spring之后，开发人员需要做的就只是完成相关配置即可，具体的创建过程和管理方式都是由Spring框架去实现的，可以理解为“把控制权交给了框架”，所以，称之为“控制反转”。

管理对象最重要的是“配置某个对象的属性”，假设`User`类中有`public String name;`属性，甚至希望获取`User`对象的同时，`name`属性已经是有值的了，Spring在处理属性值的时候，采取了DI(Dependency Injection，依赖注入)做法。

关于DI和IoC的关系：Spring框架通过DI实现了IoC，DI是实现手段，而IoC是实现的目标。

示例：

	public class User {
		public String name;
	}

如果需要为`name`属性注入值，首先，需要为`name`属性添加SET方法：

	public class User {
	
		public String name;
	
		public void setName(String name) {
			this.name = name;
		}
	
	}

然后，在Spring的配置文件中添加配置：

	<bean id="user" 
		class="cn.tedu.spring.User"">
		<!-- name:为哪个属性注入值 -->
		<!-- value:注入的值 -->
		<property name="name" value="Java"></property>
	</bean>

其实，在配置`<property>`节点的`name`属性，取值应该是“需要注入值的属性对应的SET方法的方法名去除set字样并把首字母改为小写后得到的名称”，因为Spring在处理时，会将配置的值`name`的首字母改为大写，得到`Name`，然后在左侧拼接`set`，以得到`setName`这个名称，然后，尝试调用名为`setName`的方法！

由于Eclipse等开发工具生成SET方法的模式与Spring得到SET方法的模式是完全相同的，所以，可以粗略的理解为`<property>`节点的`name`属性值就是需要注入的属性名称！


### ---------------------------------
### 附1. 关于下载的jar包错误的解决方案

**解决方案1**

更换所依赖的版本。

**解决方案2**

在Eclipse的Window菜单中选择Preferences，在弹出的对话框中，左侧选择Maven -> User Settings，右侧的Local Repository就是本地仓库文件夹。

先关闭Eclipse，本地仓库文件夹后，把错误的jar包文件所在的文件夹删除，然后，再次打开Eclipse，等待启动完成，对项目点击鼠标右键，选择Maven -> Update Project，并在弹出的对话框中勾选**Force Update ...**再更新即可。


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

### -------------------
### 附1: List与Set

List中的元素是可以重复的，例如在同一个List中存储2个`string-1`，而Set中的元素是不可以重复的，例如在同一个Set中添加2个`string-1`，实际只会存储第1次添加的`string-1`，第2次添加的是相同的数据，则不会被存储下来！判断“元素是否相同”的标准是：调用`equals()`对比的结果是`true`，并且2个元素的`hashCode()`返回值相同。

List是序列的集合，典型的实现类有`ArrayList`和`LinkedList`，其中，`ArrayList`查询效率高，但是修改效率低，而`LinkedList`查询效率低，但是修改效率高。

Set是散列的集合，从实际表现来看，并不能说Set是无序的，例如`TreeSet`会把元素按照字典排序法进行排序，如果元素是自定义的数据类型，需要该类型实现`Comparable`接口，重写其中的`int compareTo(T other)`方法，实际上`TreeSet`会调用各元素的`compareTo()`方法实现排序，这个排序过程是运行时执行的，从数据存储的角度来看，数据在内存中依然是散列的，另外，还有`LinkedHashSet`是通过链表的形式将各个元素“链”起来的，所以，输出显示这种Set时，输出结果与添加元素的顺序是保持一致的！

所有Set都是只有key没有value的Map。

﻿## 1.11. Spring注解

### 1.11.1. 通用注解

使用注解的方式来管理对象时，就不必在Spring的配置文件中使用`<bean>`节点进行配置了，但是，需要先配置一项“组件扫描”，使得Spring框架知道需要管理的类在哪里：

	<!-- 配置组件扫描的根包 -->
	<context:component-scan base-package="cn.tedu.spring" />

然后，为需要创建对象的类添加`@Component`注解即可：

	@Component
	public class UserDao {
	
	}

也就是说，“组件扫描 + 注解”就可以实现Spring创建对象！

在配置组件扫描时，`base-package`表示“根包”，假设类都在`cn.tedu.spring`包中，可以直接配置为这个包，也可以配置为`cn.tedu`，甚至配置为`cn`都是可以的！一般不推荐使用过于简单的根包，例如实际使用的是`cn.tedu.spring.dao`、`cn.tedu.spring.servlet`等，可以把根包设置为`cn.tedu.spring`，却不建议设置为`cn`或者`cn.tedu`！

关于使用的注解，可以是：

- `@Component`：通用注解
- `@Controller`：添加在控制器之前的注解
- `@Service`：添加在业务类之前的注解
- `@Repository`：添加在处理持久层的类之前的注解

在配置Spring创建和管理对象时，在类之前添加以上4个注解中的任意1个即可，以上4个注解的作用相同，使用方式相同，语义不同。

在使用以上注解后，由Spring创建的对象的bean-id默认就是类名首字母改为小写的名称，例如`UserDao`类的默认bean-id就是`userDao`，如果需要自定义bean-id，可以对注解进行配置，例如：

	@Component("dao")
	public class UserDao {
	
	}

### 1.11.2. 关于作用域与生命周期的注解

使用`@Scope`注解可以配置某类的对象是否是单例的，可以在该注解中配置属性为`singleton`或`prototype`，当配置为`@Scope("prototype")`时表示非单例的，如果希望是单例，则不需要配置该注解，默认就是单例的。

在单例的前提下，默认是饿汉式的单例，如果希望是懒汉式的单例模式，可以在类之前添加`@Lazy`注解，在该注解中还可以配置`true`或`false`，例如`@Lazy(false)`，但是，没有必要这样配置，如果希望是饿汉式的，根本就不需要添加该注解，如果希望是懒汉式的，只需要配置`@Lazy`即可，而不需要写成`@Lazy(true)`。

在被Spring管理的类中，可以自定义方法，作为初始化方法和销毁时调用的方法，需要添加`@PostConstruct`和`@PreDestroy`注解，例如：

	@Component
	public class UserDao {
	
		public UserDao() {
			System.out.println("创建UserDao的对象！");
		}
		
		@PostConstruct
		public void init() {
			System.out.println("UserDao.init()");
		}
		
		@PreDestroy
		public void destroy() {
			System.out.println("UserDao.destroy()");
		}
		
	}

以上2个注解是`javax`包中的注解，使用时，需要为项目添加Tomcat运行环境，以使用Java EE相关的jar包，才可以识别以上2个注解！

### 1.11.3. 自动装配

假设存在：

	@Repositor
	public class UserDao {
		
		public void reg() {
			System.out.println("UserDao.reg()");
		}
	
	}

如果存在`UserServlet`需要依赖于以上`UserDao`，则在`UserServlet`中的属性之前添加`@Autowired`注解即可，例如：

	@Controller
	public class UserServlet {
		
		@Autowired
		public UserDao userDao;
		
		public void reg() {
			System.out.println("UserServlet.reg()");
			userDao.reg();
		}
	
	}

当然，以上2个类都必须是被Spring所管理的，即：都在组件扫描的包下，且都添加了`@Component`或等同功能的注解。

通过注解实现自动装配时，并不需要属性有SET方法！Spring框架就是将值直接赋值过去的！

使用`@Resource`注解也可以实现自动装配，它是Java EE中的注解，它的装配模式是：优先`byName`实现装配，如果装配失败，会尝试`byType`实现装配，且，如果`byType`装配，要求匹配类型的对象必须有且仅有1个，无论是0个还是超过1个，都会报告错误！

使用`@Resource`时还可以配置需要注入的bean的id，例如：

	@Resource(name="ud1")

使用`@Autowired`时，会优先`byType`，如果找到1个匹配类型的对象，则直接装配，如果没有匹配类型的对象，则直接报告错误，如果找到多个匹配类型的对象，则会尝试`byName`，如果`byName`装配失败，则报告错误！

## Spring小结

1. Spring的作用是创建和管理对象，使用Spring可以实现解耦；

2. 掌握`<bean>`节点的`id`和`class`属性的配置；

3. 了解`<bean>`节点的`scope`、`lazy-init`、`init-method`、`destroy-method`属性的配置；

4. 了解`<bean>`节点的`factory-bean`和`factory-method`属性的配置；

5. 掌握通过SET方式注入属性的值，掌握`value`和`ref`属性的选取原则；

6. 了解通过构造方法注入属性的值；

7. 了解注入各种集合类型的属性的值；

8. 掌握通过Spring读取**.properties**文件的方式；

9. 掌握通过Spring表达式读取其它bean中的属性；

10. 理解自动装配的`byName`和`byType`的特性；

11. 掌握`@Component`、`@Controller`、`@Service`、`@Repository`这4个注解的使用；

12. 掌握组件扫描的配置方式；

13. 了解`@Scope`、`@Lazy`、`@PostConstruct`、`@PreDestroy`注解的使用；

14. 掌握`@Autowired`或`@Resource`的使用，理解它们的装配方式。

> Spring AOP会在项目后期再讲。

### ------------------------------------------

### 附1：内存溢出/内存泄漏/Leak

内存溢出并不是“使用的内存超出了限制”，如果是使用的内存超出了限制，会直接出现`OutOfMemoryError`。

内存溢出通常是因为程序意外崩溃，而某些资源并没有被释放！例如：尝试读取硬盘上的某个文件，假设使用了`FileInputStream fis`变量，在读取过程中，出现了`IOException`导致程序崩溃，则`fis`变量就不再可用，变量对应的流对象可能还处于与硬盘上的文件是连接的状态，所以就会出现“作为程序员来说，已经无法控制这个对象了，但是，由于对象仍然处于连接状态，JVM中的垃圾回收机制并不会把它当做垃圾进行回收”，这样的数据如果越来越多，就会无谓的浪费更多的内存，导致可用内存越来越少，最终，继续积累的话，就会导致“溢出”。

所以，少量的内存溢出其实是没有明显的危害的！但是，仍然应该尽可能的处理掉所有可能存在的内存溢出问题！最简单的解决方案就是“随时用完随时关闭”。



