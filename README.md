# Spring_Revise


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

### 1.8.1. List

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

## 1.11. Spring注解

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

### -------------------------------------

### 附1：内存溢出/内存泄漏/Leak

内存溢出并不是“使用的内存超出了限制”，如果是使用的内存超出了限制，会直接出现`OutOfMemoryError`。

内存溢出通常是因为程序意外崩溃，而某些资源并没有被释放！例如：尝试读取硬盘上的某个文件，假设使用了`FileInputStream fis`变量，在读取过程中，出现了`IOException`导致程序崩溃，则`fis`变量就不再可用，变量对应的流对象可能还处于与硬盘上的文件是连接的状态，所以就会出现“作为程序员来说，已经无法控制这个对象了，但是，由于对象仍然处于连接状态，JVM中的垃圾回收机制并不会把它当做垃圾进行回收”，这样的数据如果越来越多，就会无谓的浪费更多的内存，导致可用内存越来越少，最终，继续积累的话，就会导致“溢出”。

所以，少量的内存溢出其实是没有明显的危害的！但是，仍然应该尽可能的处理掉所有可能存在的内存溢出问题！最简单的解决方案就是“随时用完随时关闭”。



﻿# 2. SpringMVC

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


﻿# 2. MyBatis框架

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


# 3. Ajax

## 3.1. 服务器端如何响应请求

传统的响应方式有转发和重定向，这样的做法有很多问题，比如：转发和重定向都决定了响应的具体页面，不适合多种客户端（浏览器、Android手机、Android平板电脑、iOS手机、iOS平板电脑……）的项目，因为不同的终端设备的性能特征是不一样的，把同样的一个页面都显示给不同的终端设备是极不合适的！正确的做法应该是“服务器端只响应客户端所需要的数据”，至于这些数据如何呈现在终端设备中，由各客户端的开发团队去解决！
	
如果使用响应正文的方式，还存在“响应数据量小”的优势，则响应速度更快，产生的流量消耗小，用户体验好！

## 3.2. 服务器端响应正文

假设客户端会提交`http://localhost:8080/AJAX/user/login.do`请求，如果需要响应方式是“响应正文”，则需要在处理请求的方法之前补充添加`@ResponseBody`注解：

	@Controller
	@RequestMapping("user")
	public class UserController {
		
		@RequestMapping("login.do")
		@ResponseBody
		public String login() {
			return "LOGIN SUCCESS.";
		}
	
	}

默认情况下，响应的内容使用了ISO-8859-1编码，所以，不支持中文。

练习：用户在此次提交请求时，**必须**提交用户名和密码作为请求参数，仅当用户名为`root`且密码为`1234`时会登录成功(`LOGIN SUCCESS.`)，否则，响应用户名错误(`USERNAME ERROR.`)或者密码错误(`PASSWORD ERROR.`)。

## 3.3. 服务器响应的正文格式--JSON格式

JSON(JavaScript Object Notation, JS 对象简谱) 是一种轻量级的数据交换格式。它基于 ECMAScript (欧洲计算机协会制定的js规范)的一个子集，采用完全独立于编程语言的文本格式来存储和表示数据。简洁和清晰的层次结构使得 JSON成为理想的数据交换语言。易于人阅读和编写，同时也易于机器解析和生成，并有效地提升网络传输效率。

通常，服务器向客户端响应的数据可能不只是1个数据，以登录操作为例，也许可以响应为`1`表示登录成功，使用`2`表示登录失败且是因为用户名错误，使用`3`表示密码错误，则客户端就可以通过对这个值的判断，得知当前操作结果，但是，其它操作可能会需要更多的数据，例如“客户端尝试获取当前登录的用户信息”，需要响应的数据可能包括：用户名、手机号码、电子邮箱、年龄等一系列数据，由于响应结果只是1个字符串，要把这些数据很好的组织起来，才可以方便客户端从这1个字符串中获取其中某部分的数据，否则，如果只是响应为`"root13800138001root@163.com26"`这样，客户端就无法处理这个响应结果。

早期通常使用XML语法来组织这些数据：

	<user>
		<username>root</username>
		<age>26</age>
		<phone>13800138001</phone>
		<email>root@163.com</email>
	</user>

使用XML存在的问题：

- 数据量略大，传输略慢，流量消耗略大，用户体验略差；

- 解析难度大。

目前推荐使用的组织数据的格式是JSON格式，以上数据使用JSON组织后的表现为：

	{
		"username":"root",
		"age":26,
		"phone":"13800138001",
		"email":"root@163.com",
		"skill":["Java", "Java OOP", "Java SE", "Java WEB", "MySQL", "Spring"],
		"department":{
			"id":1,
			"name":"RD"
		}
	}

JSON数据在Javascript中是默认即识别的对象，可以直接得到其中的属性值：

	<script type="text/javascript">
		var json = {
			"username":"root",
			"age":26,
			"phone":"13800138001",
			"email":"root@163.com"
		};

		console.log(json.username);
		console.log(json.age);
	</script>

关于JSON数据格式：

- 使用`{}`表示对象，整个JSON数据就是1个对象；

- 所有的属性名都是字符串类型的，在JavaScript中，可以使用单引号或者双引号框柱，因为JSON数据可能在多种不同的编程语言中都出现，一般推荐使用双引号，属性值如果是字符串，也需要使用双引号框柱，如果是数值或者布尔值，则可以不用双引号框柱，属性名和属性值使用冒号`:`分隔，多个属性的配置之间使用逗号`,`分隔；

- 属性值的类型还可以是数组，使用中括号`[]`框柱数组的各元素，各元素之间使用逗号`,`分隔，在JavaScript中处理时，使用例如`json.skill`就可以获取到整个数组，使用`json.skill.length`就可以获取数组的长度，使用`json.skill[0]`就可以获取数组中下标为0的元素，也可以使用循环语法进行循环；

- 属性值的类型还可以是另一个对象，使用`{}`表示对象。

如果在JavaScript中，得到是一个使用JSON语法组织的字符串，而不是JSON对象，可以调用`JSON.parse(str)`函数，将字符串转换为JSON对象。

## 3.4. 服务器端实现响应JSON格式的数据

如果需要服务端响应JSON格式的数据，不可能自行拼接出JSON格式的字符串，可以通过工具来解决，首先，需要添加`jackson-databind`的依赖：

	<dependency>
		<groupId>com.fasterxml.jackson.core</groupId>
		<artifactId>jackson-databind</artifactId>
		<version>2.9.8</version>
	</dependency>

然后，在项目中自定义`cn.tedu.ajax.JsonResult`响应结果类型：

	public class JsonResult {
		
		private Integer state;
		private String message;
	
		public Integer getState() {
			return state;
		}
	
		public void setState(Integer state) {
			this.state = state;
		}
	
		public String getMessage() {
			return message;
		}
	
		public void setMessage(String message) {
			this.message = message;
		}
	
	}

并修改处理请求的方法，返回值类型使用以上自定义的类型：

	@RequestMapping("login.do")
	@ResponseBody
	public JsonResult login(
		@RequestParam("username") String username, @RequestParam("password") String password) {
		Integer state;
		String message = null;
		
		if ("root".equals(username)) {
			if ("1234".equals(password)) {
				state = 1;
			} else {
				state = 3;
				message = "密码错误！";
			}
		} else {
			state = 2;
			message = "用户名不存在！";
		}
		
		JsonResult jsonResult = new JsonResult();
		jsonResult.setState(state);
		jsonResult.setMessage(message);
		return jsonResult;
	}

如果直接运行，会提示406错误：

	HTTP Status 406 – Not Acceptable

需要在spring.xml中添加配置：

	<!-- 注解驱动 -->
	<mvc:annotation-driven />

然后，控制器中处理请求的方法响应的正文就是JSON格式的字符串了。

在控制器中响应正文时，需要添加`@ResponseBody`注解，SpringMVC框架内置了一系列的转换器(Converter)，用于将方法的返回值转换为响应的正文，在这一系列的转换器中，SpringMVC设计了对应关系和优先级，例如，当方法的返回值类型是`String`时，就会自动调用`StringHttpMessageConverter`，当项目中添加了`jackson-databind`依赖时，如果方法的返回值类型是SpringMVC默认不识别的，就会自动使用`Jackson`依赖中的转换器！`Jackson`依赖还会将响应头(Response Headers)中的`Content-Type`设置为`application/json, charset=utf-8`。

**小结：需要自定义数据类型，以决定响应的JSON数据格式(有哪些属性，分别是什么类型)，然后用自定义类型作为方法的返回值，并处理完成后返回该类型的对象，`Jackson`依赖就会自动的设置为支持中文，且把响应的对象转换成JSON字符串。**

## 3.5. AJAX

AJAX = Asynchronous JavaScript and XML（异步的JavaScript和XML）。

AJAX不是新的编程语言，而是一种使用现有标准的新方法。

AJAX是与服务器交换数据并更新部分网页的艺术，在不重新加载整个页面的情况下。

在实际实现时，通常是基于jQuery框架实现AJAX访问，主要是因为原生技术的代码比较繁琐，且存在浏览器的兼容性问题，在jQuery中，定义了`$.ajax()`函数，用于处理AJAX请求，调用该函数即可实现异步访问：

	<script type="text/javascript" src="jquery-3.4.1.min.js"></script>
	<script type="text/javascript">
	$("#btn-login").click(function(){
		// $.ajax()函数的参数是1个JSON对象
		// url：请求提交到哪里
		// data：需要提交的请求参数
		// type：请求类型
		// dataType：服务器端响应的数据类型，可以是text/xml/json，取值取决于Response Headers中的Content-Type
		// success：服务器端HTTP响应码是2xx时的回调(callback)函数，函数的参数就是服务器端响应的正文结果
		$.ajax({
			"url":"user/login.do",
			"data":$("#form-login").serialize(),
			"type":"post",
			"dataType":"json",
			"success":function(result) {
				if (result.state == 1) {
					alert("登录成功！");
				} else {
					alert(result.message);
				}
			}
		});
	});
	</script>

	
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
