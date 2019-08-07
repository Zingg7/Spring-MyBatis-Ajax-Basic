package cn.tedu.spring;

public class StudentServlet {
	
	public StudentDao studentDao;
	
	public void setStudentDao2(StudentDao studentDao) {
		System.out.println("StudentServlet.setStudentDao()");
		this.studentDao = studentDao;
	}

	public void reg() {
		System.out.println("StudentServlet.reg()");
		studentDao.reg();
	}

}
