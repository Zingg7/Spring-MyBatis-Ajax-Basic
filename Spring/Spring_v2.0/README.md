
### 1.7.1. ͨ��SET��ʽע�����Ե�ֵ

������ڣ�

	public class UserDao {
		
		public String driver; // com.mysql.jdbc.Driver
	
	}

�����ҪΪ`driver`ע��ֵ��������ҪΪ���������SET������ǿ�ҽ���ʹ�ÿ�����������SET��������Ҫ�ֶ���д����

	public void setDriver(String driver) {
		this.driver = driver;
	}

Ȼ����Spring�������ļ��У����`<property>`�ڵ��Խ������ã�

	<!-- ͨ��SET��ʽע�����ֵ -->
	<bean id="userDao" class="cn.tedu.spring.UserDao">
		<property name="driver" value="com.mysql.jdbc.Driver" />
	</bean>

���⣬���ĳ�����Ե�ֵ�����ǻ���ֵ����Spring�У��ѻ����������͵�ֵ���ַ���ͳһ��Ϊ����ֵ�������磺

	public class UserServlet {
		public UserDao userDao;
	}

�������Ե�ֵӦ����ǰ�򴴽���`UserDao`��Ķ�����������ʱ������ͨ��`<property>`�ڵ��`ref`���������Ǹ�`<bean>`��`id`��
	<!-- ͨ��SET��ʽע������ֵ -->
	<bean id="userServlet" class="cn.tedu.spring.UserServlet">
		<property name="userDao" ref="userDao" />
	</bean>

> �ۺ��������������Ե�ֵ��ʲô���ͣ�ֻҪ��ͨ��SET��ʽע������ֵ�����ȶ�����Ϊ�������SET������Ȼ����`<bean>`�ڵ��¼�ͨ��`<property>`�ڵ�������ã�������Ե�ֵ�ǻ���ֵ����ʹ��`value`����ֱ�ӱ�д��Ӧ��ֵ��������Ե�ֵ���ǻ���ֵ����ʹ��`ref`����������һ��`<bean>`��`id`�����û����˵����һ��`<bean>`������취�������һ��`<bean>`����

### 1.7.2. ͨ�����췽��ע�����Ե�ֵ

������ڣ�

	public class AdminDao {
		
		public String url;

		public AdminDao(String url) {
			super();
			this.url = url;
		}

	}

������ʱ��

	<!-- ͨ�����췽��ע������ֵ -->
	<bean id="adminDao" class="cn.tedu.spring.AdminDao">
		<!-- index����������ţ����ڼ������� -->
		<constructor-arg index="0" value="jdbc:mysql://..." />
	</bean>

��ϰ������`AdminServlet`�࣬����������`public String name;`��`public AdminDao adminDao;`��2�����ԣ�ͨ��1�����췽��Ϊ��2������ע��ֵ�����У�`name`��ֵ��`"�������Ա�������"`��`adminDao`��ֵ���Ǵ�ǰ������`AdminDao`�Ķ���

Java�ࣺ

	public class AdminServlet {
		
		public String name;
		public AdminDao adminDao;
		
		public AdminServlet(String name, AdminDao adminDao) {
			super();
			this.name = name;
			this.adminDao = adminDao;
		}	
	}

���ã�

	<!-- ͨ�����췽��ע��������ֵ -->
	<bean id="adminServlet" class="cn.tedu.spring.AdminServlet">
		<constructor-arg index="0"
			value="�������Ա�������" />
		<constructor-arg index="1"
			ref="adminDao" />
	</bean>

### 1.7.3. С��

ͨ��SET��ʽע�����Ϊ������ӹ淶��SET������������ʱ��ʹ��`<property>`�ڵ�ע�����Ե�ֵ���ýڵ��У�`name`���Կ������Ϊ�����������ƣ�

ͨ�����췽��ע������Զ���������Ĺ��췽�����ҹ��췽������ڲ���Ϊ���Ը�ֵ��

����ͨ�����ַ�ʽ�����ע���ֵ�ǻ���ֵ��ͨ��`value`�������ã����ע���ֵ����������Bean��ͨ��`ref`�����Ǹ�`<bean>`��`id`��

ͨ���Ƽ�Ϊ���󲿷����ṩ�޲����Ĺ��췽�������ԣ�ͨ��SET��ʽע���ǱȽ�ʵ�õ���������ͨ�����췽��ע���ʹ��Ƶ�ʾͷǳ��͡�

## 1.8. ע�뼯�����͵�����ֵ

#### 1.8.1. List

������ڣ�

	// Frank, Andy, Lucy, Kate
	public List<String> names;

���ϣ��ͨ��SET��ʽע�����Ե�ֵ����Ҫ������SET������Ȼ������Ϊ��

	<!-- ע��List���͵�ֵ��Frank, Andy, Lucy, Kate -->
	<property name="names">
		<list>
			<value>Frank</value>
			<value>Andy</value>
			<value>Lucy</value>
			<value>Kate</value>
		</list>
	</property>

Spring����ڴ���ʱ����ʹ��`ArrayList`��װ`List`���͵����Ե�ֵ��

### 1.8.2. Set

������ڣ�

	// Beijing, Shanghai, Guangzhou, Shenzhen
	public Set<String> cities;

������Ϊ��

	<!-- ע��Set���͵�ֵ��Beijing, Shanghai, Guangzhou, Shenzhen -->
	<property name="cities">
		<set>
			<value>Beijing</value>
			<value>Shanghai</value>
			<value>Guangzhou</value>
			<value>Shenzhen</value>
		</set>
	</property>

Spring����ڴ���ʱ����ʹ��`LinkedHashSet`��װ`Set`���͵����Ե�ֵ��

### 1.8.3. ����

������ڣ�

	// { 9, 5, 2, 7 } 
	public int[] numbers;

������Ϊ��

	<!-- ע���������͵�ֵ��{ 9, 5, 2, 7 } -->
	<property name="numbers">
		<array>
			<value>9</value>
			<value>5</value>
			<value>2</value>
			<value>7</value>
		</array>
	</property>

��ʵ����Spring����У�ע��`List`�������͵�ֵ���������͵�ֵʱ��ʹ��`<list>`�ڵ����`<array>`�ڵ㶼�ǿ��Եģ�����������`List`���͵ģ�ʹ��`<array>`�����ã������������������͵ģ�ʹ��`<list>`�����ã�������ȷ�ġ����ǣ���ʵ��Ӧ��ʱ������Ӧ��ע�����֡�

### 1.8.4. Map

������ڣ�

	// username=root, password=1234, from=Hangzhou, age=26
	public Map<String, String> session;

������Ϊ��

	<!-- ע��Map���͵�ֵ��username=root, password=1234, from=Hangzhou, age=26 -->
	<property name="session">
		<map>
			<entry key="username" value="root" />
			<entry key="password" value="1234" />
			<entry key="from" value="Hangzhou" />
			<entry key="age" value="26" />
		</map>
	</property>

### 1.8.5. Properties

������`Properties`���͵�����ֵʱ�������õĽڵ�ṹ�ǣ�

	<!-- ע��Properties���͵�ֵ -->
	<property name="config">
		<props>
			<prop key="username">root</prop>
			<prop key="password">1234</prop>
		</props>
	</property>

���⣬Ҳ����׼��ר�ŵ�**.properties**�ļ���������**src/main/resources**�´���**db.properties**�ļ���

	url=jdbc:mysql://localhost:3306/db_name?useUnicode=true&characterEncoding=utf-8&serverTimezone=Asia/Shanghai
	driver=com.mysql.jdbc.Driver
	username=root
	password=1234

Ȼ����Spring�������ļ��У�ʹ��`<util:properties>`�ڵ�Ϳ���ֱ�Ӷ�ȡ**.properties**�ļ���

	<util:properties id="config" 
		location="classpath:db.properties" />

����`<util:properties>`�ڵ�Ҳ��һ��`<bean>`�ڵ㣬���ԣ�ע��ֵʱ������ͨ��`ref`��������ڵ㣺

	<!-- ע��Properties���͵�ֵ -->
	<property name="config" ref="config" />

## 1.9. Spring���ʽ

Spring���ʽ��ʹ��ռλ���ķ�ʽ������Spring�������ļ��еģ����������ڻ�ȡ����`<bean>`�е����Ե�ֵ��

������ڣ�

	public class ValueBean {
		// ֵ��SampleBean�е�names�еĵ�2��
		public String name;
		
	}

���ȣ���Ҫȷ��ע��ֵ�ķ�ʽ��������ͨ��SET��ʽע�룬Ҳ����ͨ�����췽��ע�룬���ѡ��ͨ��SET��ʽע�룬��Ҫ������SET������

	public void setName(String name) {
		this.name = name;
	}

Ȼ����Spring�������ļ��н������ã�

	<!-- ʹ��Spring���ʽ -->
	<bean id="valueBean" class="cn.tedu.spring.ValueBean">
		<!-- ֵ��SampleBean�е�names�еĵ�2�� -->
		<property name="name"
			value="#{sampleBean.names[1]}" />
	</bean>

���Է��֣�Spring���ʽ�Ļ�����ʽ��ʹ��`#{}`����ռλ�����ڲ��﷨�ǣ�

	#{bean��id.����}

������Ե�������`List`���ϡ�`Set`���ϻ������飬���������Ҳ�ʹ��`[]`д�����������±꣺

	#{bean��id.����[���������±�]}

������Ե�������`Map`���ϻ���`Properties`������ʹ�õ��﷨��

	#{bean��id.����.key}

	#{bean��id.����['key']}

## 1.10. �Զ�װ��(autowire)

�Զ�װ�䣺����Ҫ��Spring�������ļ��н�������ֵ��ע�룬ֻ��Ҫ���������Զ�װ�䣬Spring�ͻ��Զ����������ֵ��ע�롣

�������`StudentServlet`������`StudentDao`��

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

�Ϳ�������Ϊ��

	<bean id="studentDao"
		class="cn.tedu.spring.StudentDao" />
	<bean id="studentServlet"  class="cn.tedu.spring.StudentServlet" autowire="byName">
	</bean>

���������У�`autowire`���Ծ������������Զ�װ��ġ�

��ȡֵ��`byName`ʱ����ʾ�����������Զ�װ�䡱������������У�Spring���ȼ�⵽��`StudentServlet`������Ϊ`studentDao`�����ԣ�����ݸ����Եõ�`setStudentDao`����������ƣ�Ȼ�󣬳����ҵ���SET�������ƶ�Ӧ��bean��id��������ĳ��idΪ`studentDao`��`<bean>`������ҵ������Զ�����`setStudentDao`����������Զ�װ�䣬���û���ҵ�ƥ���bean-id���򲻻᳢���Զ�װ�䡣�򵥵���˵������**SET������������Ҫ��bean-id���Ӧ**�����Ե����ƿ��Ժ�bean-id����Ӧ���Զ�װ����һ�֡������ԡ��Ĳ�������װ��װ��װ����Ҳ���ᱨ��

���⣬������ȡֵΪ`byType`����ʾ�����������Զ�װ�䡱����������̣�Spring����`StudentServlet`����`set`��Ϊǰ׺�ķ����������Ե�����Щ����������ʱ��**����Spring�����в����������������ϵ�bean�����û��ƥ��Ķ������Զ�װ�䣬����ҵ�1������ִ�и÷�����������Զ�װ�䣬����ҵ�2�����߸��࣬��ֱ�ӱ������**��

��������װ��ģʽ��һ�㲻���˽⡣

**��ʵ�ʿ���ʱ��������ʹ�����ַ�ʽ��ʵ���Զ�װ��**����Ϊ�����������ڵ����⣺�����Ƿ�װ���ˣ����ֵĲ����ԣ��������ϸ���Ķ�������Դ���룬�����޷�ȷ�����е���Щ���Ա�װ����ֵ������Щ����û�б�װ��ֵ��

Ŀǰ����Ҫ����Զ�װ���˼�룬��`byName`��`byType`��2��װ��ģʽ�����Լ��ɡ�


#### ��2��List��Set

List�е�Ԫ���ǿ����ظ��ģ�������ͬһ��List�д洢2��`string-1`����Set�е�Ԫ���ǲ������ظ��ģ�������ͬһ��Set�����2��`string-1`��ʵ��ֻ��洢��1����ӵ�`string-1`����2����ӵ�����ͬ�����ݣ��򲻻ᱻ�洢�������жϡ�Ԫ���Ƿ���ͬ���ı�׼�ǣ�����`equals()`�ԱȵĽ����`true`������2��Ԫ�ص�`hashCode()`����ֵ��ͬ��

List�����еļ��ϣ����͵�ʵ������`ArrayList`��`LinkedList`�����У�`ArrayList`��ѯЧ�ʸߣ������޸�Ч�ʵͣ���`LinkedList`��ѯЧ�ʵͣ������޸�Ч�ʸߡ�

Set��ɢ�еļ��ϣ���ʵ�ʱ���������������˵Set������ģ�����`TreeSet`���Ԫ�ذ����ֵ����򷨽����������Ԫ�����Զ�����������ͣ���Ҫ������ʵ��`Comparable`�ӿڣ���д���е�`int compareTo(T other)`������ʵ����`TreeSet`����ø�Ԫ�ص�`compareTo()`����ʵ����������������������ʱִ�еģ������ݴ洢�ĽǶ��������������ڴ�����Ȼ��ɢ�еģ����⣬����`LinkedHashSet`��ͨ���������ʽ������Ԫ�ء����������ģ����ԣ������ʾ����Setʱ�������������Ԫ�ص�˳���Ǳ���һ�µģ�

����Set����ֻ��keyû��value��Map��