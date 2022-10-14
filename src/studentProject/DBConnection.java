package studentProject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DBConnection {
	private Connection connection = null;
	private ResultSet rs = null; // 레코드 단위로 가져온다 // 

	// connect -> 컨트롤 쉬프트 넘버락쪽 /랑 * 누르면 펼쳤다 가능
	public void connect() {	
		// db.properties 파일 로드
		Properties properties = new Properties();

		FileInputStream fis;

		try {
			fis = new FileInputStream("C:/java_test/studentProject/src/studentProject/db.properties");
			properties.load(fis);
		} catch (FileNotFoundException e) {
			System.out.println("FileInputStream Error" + e.getMessage());
		} catch (IOException e) {
			System.out.println("Properties Error" + e.getMessage());
		}

		// ============================================

		// ============================================

		try {
			// driver 로드 = 환경 제공, MySQL 인식할 수 있도록 도와준다.
			Class.forName(properties.getProperty("driver"));
			// db 접속 요청 = 그 관련된 장치에 대해 접속 할 수 있는 handle 이다. 삽입, 삭제, 수정을 할 수 있다. 쿼리문을 통해서
			connection = DriverManager.getConnection(properties.getProperty("url"), properties.getProperty("userid"),
					properties.getProperty("password"));
		} catch (ClassNotFoundException e) {
			System.out.println("Class.forname Load Error" + e.getMessage());
		} catch (SQLException e) {
			System.out.println("Connection Error" + e.getMessage());
		}
		// ============================================
	}

	// insert
	public int insert(Student student) {
		PreparedStatement ps = null; // 자원 다른 사람도 써야해 / 쿼리문을 작동시키려면 필요하다. 2중으로 보안하고 싶으면 prepare을 쓰면 된다
		int insertReturnValue = -1;
//		String insertQuery = "insert into student(no,name,kor,eng,math,total,avr,grade) values(?,?,?,?,?,?,?,?)"; // 보안상의 문제가 되니까 ?로 함

		String insertQuery = "call procedure_insert_student(?,?,?,?,?)";
		
		try {
			ps = connection.prepareStatement(insertQuery); // 값을 넣기 위해서 준비시키고 / 아직 쿼리문이 완벽하게 완성된 것이 아니니까 prepare 한 것이다. 
			ps.setString(1, student.getNo());
			ps.setString(2, student.getName());
			ps.setInt(3, student.getKor());
			ps.setInt(4, student.getEng());
			ps.setInt(5, student.getMath());
//			ps.setInt(6, student.getTotal());
//			ps.setDouble(7, student.getAvr());
//			ps.setString(8, student.getGrade());
			// 삽입성공하면 리턴값 1 : 삽입 블럭지정하고 번개 클릭한다.
			insertReturnValue = ps.executeUpdate(); // 얘가 번개 버튼 누르는 실행이랑 똑같다.
		} catch (Exception e) {
			System.out.println("INSERT ERROR : " + e.getMessage());
		} finally {
			try {
				if (ps != null) { // 자원이 열려있으면 !
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement Close Error" + e.getMessage());
			}
		}
		return insertReturnValue; // 나 잘 입력이 됐다라는 값을 넣어줘야함.

	}

	// delete statement
	public int delete(String no) {
		PreparedStatement ps = null; // 자원 다른 사람도 써야해
		int deleteReturnValue = -1;
		String deleteQuery = "DELETE FROM student WHERE no = ?";

		try {
			ps = connection.prepareStatement(deleteQuery); // 값을 넣기 위해서 준비시키고
			ps.setString(1, no);

			// 삭제성공하면 리턴값 1
			deleteReturnValue = ps.executeUpdate(); // 얘가 번개 버튼 누르는 실행이랑 똑같다.
		} catch (Exception e) {
			System.out.println("DELETE ERROR : " + e.getMessage());
		} finally {
			try {
				if (ps != null) { // 자원이 열려있으면 !
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement Close Error" + e.getMessage());
			}
		}
		return deleteReturnValue;
	}

	// select name search statement
	public List<Student> select() {
		List<Student> list = new ArrayList<Student>();
		PreparedStatement ps = null; // 자원 다른 사람도 써야해
		String selectQuery = "SELECT * FROM student";
		ResultSet rs = null;
		try {
			ps = connection.prepareStatement(selectQuery); // 완벽한 쿼리문이니까 statement 로 해도 됨.
			// 삭제성공하면 리턴값 1 ps.executeUpdate(query) : int 리턴값 이랑 executeQuery(query) :
			// ResultSet 리턴값
			// UPDATE => DB 에 write(저장,변환)하는 기능(insert, update, delete)을 갖는건 int(2개 넣으면 2
			// 리턴, 10개 삭제 10, 안했다 0, 잘못했다 -1) 이다.
			// QUERY => read(읽기) 하는 기능이다. 모든 레코드들의 값을 가져와준다. 그래서 리턴 줄 때 레코드 단위로 ResultSet이
			// 받는 것이다.
			// Select 성공하면 리턴값 ResultSet, 오류면 null(객체, 대문자니까)
			rs = ps.executeQuery(selectQuery);

			// 결과값이 없을 때를 체크하는 방법
			if (!(rs != null || rs.isBeforeFirst())) { // 바로 앞이 첫번째이다 = 레코드 가장 앞에 커서가 이동한다. = 첫번째 읽을 위치에 있어 ! ^[ ] 없으면,
														// false를 주는 것이다 ! 
								// isBeforeFirst = 결과값이 레코드 셋이 저장되어있는데, 레코드 단위로 저장되어있다. 그 첫번째 레코드를 읽기 위해 커서가 위치해있는지 물어보는 것
				return list;
			}
			// rs.next() : 현재 커서에 있는 레코드 위치로 움직이는 것 그 레코드를 계속 읽어오는 것이다. +)hasNext는 다음게 있느냐? 만 물어보는 것이다. 
			while (rs.next()) {
				String no = rs.getString("no");
				String name = rs.getString("name");
				int kor = rs.getInt("kor");
				int eng = rs.getInt("eng");
				int math = rs.getInt("math");
				int total = rs.getInt("total");
				double avr = rs.getDouble("avr");
				String grade = rs.getString("grade");
				int rate = rs.getInt("rate"); // 만약 null 값이면 뭘로 가져올지? defalut로 0을 주겠다구DB에서 설정해놓기

				list.add(new Student(no, name, kor, eng, math, total, avr, grade, rate)); // 5개 레코드 있으면 list에 5개 가져오는것
			}

		} catch (Exception e) {
			System.out.println("SELECT ERROR : " + e.getMessage());
		} finally {
			try {
				if (ps != null) { // 자원이 열려있으면 !
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement Close Error" + e.getMessage());
			}
		}
		return list;
	}

	public List<Student> selectSearch(String data, int type) {
		List<Student> list = new ArrayList<Student>();
		PreparedStatement ps = null; // 자원 다른 사람도 써야해
		ResultSet rs = null;
		String selectSearchQuery = "SELECT * FROM student where "; // 완벽하게 같은걸로 하고 싶을때 Like를 = 로
		try {

			switch (type) {
			case 1:
				selectSearchQuery += "no LIKE ? ";
				break;
			case 2:
				selectSearchQuery += "name LIKE ? ";
				break;
			default:
				System.out.println("잘못된 입력 타입 ");
				return list;
			}

			ps = connection.prepareStatement(selectSearchQuery);

//			ps.setString(1, namePattern); // namePattern를 아예 한번에 넣어보는것도 괜찮아.

//			String namePattern = "%" + data + "%";
			ps.setString(1, "%" + data + "%"); // = 와일드카드 , 완벽하게 같게 하고 싶을 때

			rs = ps.executeQuery();

			// 결과값이 없을 때를 체크하는 방법
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}

			while (rs.next()) {
				String no = rs.getString("no");
				String name = rs.getString("name");
				
				int kor = rs.getInt("kor");
				int eng = rs.getInt("eng");
				int math = rs.getInt("math");
				int total = rs.getInt("total");
				double avr = rs.getDouble("avr");
				String grade = rs.getString("grade");
				int rate = rs.getInt("rate"); // 만약 null 값이면 뭘로 가져올지? defalut로 0을 주겠다구DB에서 설정해놓기

				list.add(new Student(no, name, kor, eng, math, total, avr, grade, rate)); // 5개 레코드 있으면 list에 5개 가져오는
																							// 것임.
			}
		} catch (Exception e) {
			System.out.println("SELECTSEARCH ERROR : " + e.getMessage());
		} finally {
			try {
				if (ps != null) { // 자원이 열려있으면 !
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement Close Error" + e.getMessage());
			}
		}
		return list;
	}

	// update
	public int update(Student student) { // 저장과 독같음
		PreparedStatement ps = null; // 자원 다른 사람도 써야해
		int updateReturnValue = -1;
		String updateQuery = "UPDATE student SET kor = ?, eng = ?, math = ?, total = ?,  avr = ?, grade = ? WHERE no = ?"; // 나름

		try {
			ps = connection.prepareStatement(updateQuery); // 값을 넣기 위해서 준비시키고
			ps.setInt(1, student.getKor());
			ps.setInt(2, student.getEng());
			ps.setInt(3, student.getMath());
			ps.setInt(4, student.getTotal());
			ps.setDouble(5, student.getAvr());
			ps.setString(6, student.getGrade());
			ps.setString(7, student.getNo());
			// 수정성공하면 리턴값 1 : 삽입 블럭지정하고 번개 클릭한다.
			updateReturnValue = ps.executeUpdate(); // 얘가 번개 버튼 누르는 실행이랑 똑같다.
		} catch (Exception e) {
			System.out.println("UPDATE ERROR : " + e.getMessage());
		} finally {
			try {
				if (ps != null) { // 자원이 열려있으면 !
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement Close Error" + e.getMessage());
			}
		}
		return updateReturnValue;
	}

	// select order by statement
	public List<Student> selectOrderBy(int type) {
		List<Student> list = new ArrayList<Student>();
		PreparedStatement ps = null; // 자원 다른 사람도 써야해
		ResultSet rs = null;
		String selectOrderByQuery = "SELECT * FROM student order by ";
		try {
			switch (type) {
			case 1:
				selectOrderByQuery += "no asc ";
				break;
			case 2:
				selectOrderByQuery += "name asc ";
				break;
			case 3:
				selectOrderByQuery += "total desc ";
				break;
			default:
				System.out.println("정렬 타입 오류");
				return list;
			}

			ps = connection.prepareStatement(selectOrderByQuery); // 값을 넣기 위해서 준비시키고

			rs = ps.executeQuery(selectOrderByQuery);

			// 결과값이 없을 때를 체크하는 방법
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}

			int rank = 0;
			// rs.next() : 현재 커서에 있는 레코드 위치로 움직이는 것 // 계속 읽어오는 것이다.
			while (rs.next()) {
				String no = rs.getString("no");
				String name = rs.getString("name");
				int kor = rs.getInt("kor");
				int eng = rs.getInt("eng");
				int math = rs.getInt("math");
				int total = rs.getInt("total");
				double avr = rs.getDouble("avr");
				String grade = rs.getString("grade");
				int rate = rs.getInt("rate");

				if (type == 3) {
					rate = ++rank;
					// 나중에 DB 업데이트 rate 업데이트 해야한다.
				}
				
				list.add(new Student(no, name, kor, eng, math, total, avr, grade, rate));
			}

		} catch (Exception e) {
			System.out.println("SELECT 정렬 ERROR : " + e.getMessage());
		} finally {
			try {
				if (ps != null) { // 자원이 열려있으면 !
					ps.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement Close Error" + e.getMessage());
			}
		}
		return list;
	}

	//select MaxMin statement
	public List<Student> selectMaxMin(int type) {
		List<Student> list = new ArrayList<Student>();
//		PreparedStatement ps = null; // 자원 다른 사람도 써야해
		
		Statement statement = null; // statement 로 하는거
		
		ResultSet rs = null;
		String selectMaxMinQuery = "SELECT * from student where total = ";
		try {
			switch (type) {
			case 1: 
				selectMaxMinQuery += "(SELECT Max(total) AS max_total from student) "; //서브 쿼리들이다
				break;
			case 2:
				selectMaxMinQuery += "(SELECT Min(total) AS min_total from student) ";
				break;
			default:
				System.out.println("통계 오류");
				return list;
			}

//			ps = connection.prepareStatement(selectMaxMinQuery); // 값을 넣기 위해서 준비시키고
			statement = connection.createStatement(); // statement 로 하는거

			
//			rs = ps.executeQuery(selectMaxMinQuery);
			rs = statement.executeQuery(selectMaxMinQuery); // statement 로 하는거

			// 결과값이 없을 때를 체크하는 방법
			if (!(rs != null || rs.isBeforeFirst())) {
				return list;
			}

			// rs.next() : 현재 커서에 있는 레코드 위치로 움직이는 것 // 계속 읽어오는 것이다.
			while (rs.next()) {
				String no = rs.getString("no");
				String name = rs.getString("name");
				int kor = rs.getInt("kor");
				int eng = rs.getInt("eng");
				int math = rs.getInt("math");
				int total = rs.getInt("total");
				double avr = rs.getDouble("avr");
				String grade = rs.getString("grade");
				int rate = rs.getInt("rate");
				
				list.add(new Student(no, name, kor, eng, math, total, avr, grade, rate));
			}

		} catch (Exception e) {
			System.out.println("STATS MAX MIN ERROR : " + e.getMessage());
		} finally {
			try {
				if (statement != null) { // 자원이 열려있으면 ! statement <-> ps
					statement.close();
				}
			} catch (SQLException e) {
				System.out.println("PrepareStatement Close Error" + e.getMessage());
			}
		}
		return list;
	}
	
	// connection close
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			System.out.println("Connection Close Error" + e.getMessage());
		}
	}


}
