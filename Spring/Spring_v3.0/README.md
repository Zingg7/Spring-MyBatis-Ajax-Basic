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

### ------------------------------------------

### 附1：内存溢出/内存泄漏/Leak

内存溢出并不是“使用的内存超出了限制”，如果是使用的内存超出了限制，会直接出现`OutOfMemoryError`。

内存溢出通常是因为程序意外崩溃，而某些资源并没有被释放！例如：尝试读取硬盘上的某个文件，假设使用了`FileInputStream fis`变量，在读取过程中，出现了`IOException`导致程序崩溃，则`fis`变量就不再可用，变量对应的流对象可能还处于与硬盘上的文件是连接的状态，所以就会出现“作为程序员来说，已经无法控制这个对象了，但是，由于对象仍然处于连接状态，JVM中的垃圾回收机制并不会把它当做垃圾进行回收”，这样的数据如果越来越多，就会无谓的浪费更多的内存，导致可用内存越来越少，最终，继续积累的话，就会导致“溢出”。

所以，少量的内存溢出其实是没有明显的危害的！但是，仍然应该尽可能的处理掉所有可能存在的内存溢出问题！最简单的解决方案就是“随时用完随时关闭”。


