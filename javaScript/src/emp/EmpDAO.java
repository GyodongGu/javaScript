package emp;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import common.DAO;

public class EmpDAO {
	
	Connection conn = null;
	PreparedStatement pstmt = null;
	ResultSet rs = null;
	
	public void deleteEmp(int empNo) {
		conn = DAO.getConnect();
		String sql = "delete from emp_temp where employee_id = ?";
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, empNo);
			int x = pstmt.executeUpdate();
			System.out.println(x + "건이 삭제되었습니다.");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	public void updateEmp(Employee emp, int eNo) {
		conn = DAO.getConnect();
		String sql = "update emp_temp set employee_id = ?, first_name = ?, last_name = ?, email = ?, job_id = ?, hire_date = ?, salary = ? where employee_id = ?";
		int Cnt = 0;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, emp.getEmployeeId());
			pstmt.setString(2, emp.getFirstName());
			pstmt.setString(3, emp.getLastName());
			pstmt.setString(4, emp.getEmail());
			pstmt.setString(5, emp.getJobId());
			pstmt.setString(6, emp.getHireDate());
			pstmt.setInt(7, emp.getSalary());
			pstmt.setInt(8, eNo);
			pstmt.executeUpdate();
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	//procedure
	public void insertEmpProc(Employee emp) {
		conn = DAO.getConnect();
		String sql = "{call add_new_member(?,?,?,?,?,?)}";
		try {
			CallableStatement cstmt = conn.prepareCall(sql);
			cstmt.setString(1, emp.getFirstName());
			cstmt.setString(2, emp.getLastName());
			cstmt.setString(3, emp.getJobId());
			cstmt.setInt(4, emp.getSalary());
			cstmt.setString(5, emp.getHireDate());
			cstmt.setString(6, emp.getEmail());
			
			cstmt.execute();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void insertEmp(Employee emp) {
		conn = DAO.getConnect();
		String sql = "insert into emp_temp(employee_id, first_name, last_name, email, job_id, hire_date, salary)" + 
				"values (?,?,?,?,?,?,?)";
		
		int rCnt=0;
		
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(++rCnt, emp.getEmployeeId());	//database는 1번부터 시작한다.
			pstmt.setString(++rCnt, emp.getFirstName());
			pstmt.setString(++rCnt, emp.getLastName());
			pstmt.setString(++rCnt, emp.getEmail());
			pstmt.setString(++rCnt, emp.getJobId());
			pstmt.setString(++rCnt, emp.getHireDate());
			pstmt.setInt(++rCnt, emp.getSalary());
			
			int r = pstmt.executeUpdate();
			System.out.println(r +" 건이 입력되었습니다.");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public List<Employee> getEmplist(){
		
		List<Employee> list = new ArrayList<>();					//List타입의 ArrayList인스턴스 생성
		conn = DAO.getConnect();									//데이터베이스 연결
		String sql = "select * from emp_temp";						//sql문 
		Employee emp = null;										//Employee타입의 공관 확보
		try {
			pstmt = conn.prepareStatement(sql);						//database에 접근하기 위한 preparedStatement
			rs = pstmt.executeQuery();								//sql문 실행 결과는 ResultSet에 저장
			while(rs.next()) {
				emp = new Employee();								//Employee타입의 인스턴스 생성
				emp.setEmployeeId(rs.getInt("employee_id"));		//인스턴스에 각각의 데이터를 입력
				emp.setFirstName(rs.getString("first_name"));
				emp.setLastName(rs.getString("last_name"));
				emp.setHireDate(rs.getString("hire_date"));
				emp.setEmail(rs.getString("email"));
				emp.setSalary(rs.getInt("salary"));
				emp.setJobId(rs.getString("job_id"));
				
				list.add(emp);										//입력된 Employee타입의 인스턴스를 ArrayList에 값을 추가
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();										//접속종료
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return list;
	}
	
	public Employee getEmployee(int empNo) {
		conn = DAO.getConnect();									//database연결
		Employee emp = null;										//Employee타입의 결과물 공간 확보
		String sql = "select * from emp_temp where employee_id = ?";//실행 하고자 하는 sql문
		String sql1 = "{? = call get_dept_name(?)}";				//{?=call (procedure_name)} 실행하고자 하는 procedure
		try {
			pstmt = conn.prepareStatement(sql);						//database에 접근하기위한 preparedStatement
			pstmt.setInt(1, empNo);									//sql의 ?에 값을 입력
			rs = pstmt.executeQuery();								//sql문 실행
			CallableStatement cstmt = conn.prepareCall(sql1);		//procedure 사용하기위한 접근
			cstmt.registerOutParameter(1, java.sql.Types.VARCHAR);	//procedure 출력 parameter의 타입을 정해주는 용도
			cstmt.setInt(2, empNo);									//두 번째 ?에 parameter값을 입력
			cstmt.execute();										//sql1문 실행
			String deptName = cstmt.getString(1);					//첫 번째 ?에 대한 값을 deptName에 저장
			
			
			while(rs.next()) {
				emp =  new Employee();								//결과물 인스턴스 생성
				emp.setEmployeeId(rs.getInt("employee_id"));		//결과물에 해당하는 데이터 삽입
				emp.setFirstName(rs.getString("first_name"));
				emp.setLastName(rs.getString("last_name"));
				emp.setHireDate(rs.getString("hire_date"));
				emp.setEmail(rs.getString("email"));
				emp.setSalary(rs.getInt("salary"));
				emp.setJobId(rs.getString("job_id"));
				emp.setDeptName(deptName);							//setDeptName으로 deptName을 입력한다.
			}	
			
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				conn.close();										//연결종료
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return emp;
	}
	
	
}
