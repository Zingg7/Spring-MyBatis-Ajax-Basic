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


# 附1. 关于下载的jar包错误的解决方案

**解决方案1**

更换所依赖的版本。

**解决方案2**

在Eclipse的Window菜单中选择Preferences，在弹出的对话框中，左侧选择Maven -> User Settings，右侧的Local Repository就是本地仓库文件夹。

先关闭Eclipse，本地仓库文件夹后，把错误的jar包文件所在的文件夹删除，然后，再次打开Eclipse，等待启动完成，对项目点击鼠标右键，选择Maven -> Update Project，并在弹出的对话框中勾选**Force Update ...**再更新即可。
