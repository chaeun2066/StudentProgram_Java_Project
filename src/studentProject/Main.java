package studentProject;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class Main {
	public static Scanner sc = new Scanner(System.in);
	public static final int INPUT = 1, UPDATE = 2, DELETE = 3, SEARCH = 4, OUTPUT = 5, SORT = 6, STATS = 7, EXIT = 8;

	public static void main(String[] args) {
		DBConnection dbConn = new DBConnection();

		// db 연결
		dbConn.connect();

		boolean loopFlag = false;
		while (!loopFlag) {
			// 메뉴입력
			int num = displayMenu();
			switch (num) {
			case INPUT:
				// 학생정보입력-> 데이타베이스연결 insert
				studentInputData();
				break;
			case UPDATE:
				studentUpDate();
				break;
			case DELETE:
				studentDelete();
				break;
			case SEARCH:
				studentSearch(); // 이름으로 검색하기 이름이 더 편하니까
				break;
			case OUTPUT:
				studentOutput(); // resultSet 으로 DB의 레코드들의 결과값들이 전달이 되는 것이다., hasnext로 다음값 가져와서 있으면, new Student() 객체로 만들고 list에 넣어주면 된다. 그리고 list 출력
				break;
			case SORT:
				studentSort();
				break;
			case STATS:
				studentStats();
				break;
			case EXIT:
				System.out.println("프로그램이 종료 됩니다.");
				loopFlag = true;
				break;
			default:
				System.out.println("숫자 1 ~ 8번까지 다시 입력바랍니다.");
				break;
			}
		} // end of while

		System.out.println("종료");

	}

	//학생 정보 통계
	public static void studentStats() {
		List<Student> list = new ArrayList<Student>();
		// 학생번호 비교, 저장
		try {
			System.out.print("1:최고 점수, 2:최저 점수>> ");
			int type = sc.nextInt();
			
			boolean value = checkInputPattern(String.valueOf(type),5);
			if(!value) return;

			// 학생번호삭제
			// 데이타베이스 연결,학생이름으로 검색
			DBConnection dbConn = new DBConnection();
			dbConn.connect();

			list = dbConn.selectMaxMin(type); // 100점이 2명있으면 2명 다 보여주는 것.

			if (list.size() <= 0) {// 리턴값 0이면 해당 파일이 없어서 안지워지는 것임. 0rows affected
				System.out.println("출력할 리스트가 없습니다." + list.size());
				return;
			}

			// 리스트 내용을 보여준다.
			for (Student student : list) {
				System.out.println(student);
			}

			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("타입이 맞지 않습니다. 재입력요청" + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 학생 통계 에러" + e.getMessage());
		}
	
	}

	// 학생 정보 정렬 : 학번, 이름 , 총점
	public static void studentSort() {
		List<Student> list = new ArrayList<Student>();
		
		try {
			// 데이타베이스 연결,학생번호 객체 전체 출력
			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			
			// 정렬 방식 선택 번호 입력
			System.out.print("정렬방식(1: no, 2: name, 3: total)>> ");
			int type = sc.nextInt();
			
			// 번호 패턴 검색
			boolean value = checkInputPattern(String.valueOf(type), 4);
			if (!value) {  return;	}

			list = dbConn.selectOrderBy(type);
			
			if (list.size() <= 0) {// 리턴값 0이면 해당 파일이 없어서 안지워지는 것임. 0rows affected
				System.out.println("출력할 리스트가 없습니다." + list.size());
				return;
			}

			// 리스트 내용을 보여준다.
			for (Student student : list) {
				System.out.println(student);
			}

			dbConn.close();
		} catch (Exception e) {
			System.out.println("데이터 베이스 정렬 에러" + e.getMessage());
		}
		return;
	}

	// 학생 정보 수정(점수만 수정)
	public static void studentUpDate() {
		List<Student> list = new ArrayList<Student>();
		// 수정할 학생번호 입력
		System.out.print("수정할 학생번호 입력하시오>> ");
		String no = sc.nextLine();

		// 번호 패턴 검색
		boolean value = checkInputPattern(no, 1);
		if (!value) {
			return;
		}

		// 번호로 검색해서 불러내야됨.
		// 데이타베이스 연결,학생이름으로 검색
		DBConnection dbConn = new DBConnection();
		dbConn.connect();

		list = dbConn.selectSearch(no, 1);

		if (list.size() <= 0) {// 리턴값 0이면 해당 파일이 없어서 안지워지는 것임. 0rows affected
			System.out.println("출력할 리스트가 없습니다." + list.size());
			return;
		}

		// 리스트 내용을 보여준다.
		for (Student student : list) {
			System.out.println(student);
		}

		System.out.println("-".repeat(68));
		// 수정할 리스트를 보여줘야한다.
		// 국,영,수 점수 재입력
		Student imsiStudent = list.get(0); // 0인덱스부터 시작한다
		System.out.print("국어" + imsiStudent.getKor() + ">>");
		int kor = sc.nextInt();
		value = checkInputPattern(String.valueOf(kor), 3);
		if (!value)
			return;
		imsiStudent.setKor(kor);

		System.out.print("영어" + imsiStudent.getEng() + ">>");
		int eng = sc.nextInt();
		value = checkInputPattern(String.valueOf(eng), 3);
		if (!value)
			return;
		imsiStudent.setEng(eng);

		System.out.print("수학" + imsiStudent.getMath() + ">>");
		int math = sc.nextInt();
		value = checkInputPattern(String.valueOf(math), 3);
		if (!value)
			return;
		imsiStudent.setMath(math);

		// 총합, 평균, 등급
		imsiStudent.calTotal();
		imsiStudent.calAvr();
		imsiStudent.calGrade();

		// 데이터베이스 수정할 부분을 Update 해야한다.
		int returnUpdateValue = dbConn.update(imsiStudent);

		if (returnUpdateValue == -1) {// 리턴값 0이면 해당 파일이 없어서 안지워지는 것임. 0rows affected
			System.out.println("학생 수정 문제" + returnUpdateValue);
			return;
		} else {
			System.out.println("학생 수정 완료" + returnUpdateValue);
		}

		dbConn.close();
	}

	// 학생 이름 검색
	public static void studentSearch() {
		List<Student> list = new ArrayList<Student>();
		// 학생번호 비교, 저장
		try {
			// 검색할 학생이름 입력
			System.out.print("검색 할 학생 이름을 입력하시오.>>");
			String name = sc.nextLine();

			// 패턴 검색
			boolean value = checkInputPattern(name, 2); // 2번 이 뭔지 상수 처리 해주자.
			if (!value) {
				return;
			}

			// 학생번호삭제
			// 데이타베이스 연결,학생이름으로 검색
			DBConnection dbConn = new DBConnection();
			dbConn.connect();

			list = dbConn.selectSearch(name, 2);

			if (list.size() <= 0) {// 리턴값 0이면 해당 파일이 없어서 안지워지는 것임. 0rows affected
				System.out.println("출력할 리스트가 없습니다." + list.size());
				return;
			}

			// 리스트 내용을 보여준다.
			for (Student student : list) {
				System.out.println(student);
			}

			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("타입이 맞지 않습니다. 재입력요청" + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 이름 검색 에러" + e.getMessage());
		}

	}

	// 전체 학생 정보 출력
	public static void studentOutput() {
		List<Student> list = new ArrayList<Student>();
		try {
			// 데이타베이스 연결,학생번호 객체 전체 출력
			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			list = dbConn.select();

			if (list.size() <= 0) {// 리턴값 0이면 해당 파일이 없어서 안지워지는 것임. 0rows affected
				System.out.println("출력할 리스트가 없습니다." + list.size());
				return;
			}

			// 리스트 내용을 보여준다.
			for (Student student : list) {
				System.out.println(student);
			}

			dbConn.close();
		} catch (Exception e) {
			System.out.println("데이타베이스 출력 에러" + e.getMessage());
		}
		return;
	}

	// 학생 정보 삭제
	public static void studentDelete() {
		try {
			// 삭제할 학생번호 입력
			System.out.print("삭제학생번호입력(010101)>>");
			String no = sc.nextLine();
			// no 문자열패턴검색
			boolean value = checkInputPattern(no, 1);
			if (!value) {
				return;
			}
			// 학생번호삭제
			// 데이타베이스 연결,학생번호 객체삭제
			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			int deleteReturnValue = dbConn.delete(no);
			if (deleteReturnValue == -1) {// 리턴값 0이면 해당 파일이 없어서 안지워지는 것임. 0rows affected
				System.out.println("삭제실패입니다." + deleteReturnValue);
			} else if (deleteReturnValue == 0) {
				System.out.println("삭제할 번호가 존재하지 않습니다." + deleteReturnValue);
			} else {
				System.out.println("삭제성공입니다. 리턴값=" + deleteReturnValue);
			}
			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("타입이 맞지 않습니다. 재입력요청" + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 삭제 에러" + e.getMessage());
		}

	}

	// 학생 정보 입력
	public static void studentInputData() {
		try {
			// 학년(1~3학년:01,02,03)반(1~9:01~09)번호(1~30:01~60)
			System.out.print("학년(01,02,03)반(01~09)번호(01~60)>>");
			String no = sc.nextLine();

			// no 문자열패턴검색
			boolean value = checkInputPattern(no, 1);
			if (!value) {
				return;
			}

			System.out.print("이름입력>>");
			String name = sc.nextLine();
			// 이름 문자열패턴검색
			value = checkInputPattern(name, 2);
			if (!value)
				return;

			System.out.print("kor 입력>>");
			int kor = sc.nextInt();
			// kor (0~100)패턴검색
			value = checkInputPattern(String.valueOf(kor), 3);
			if (!value)
				return;

			System.out.print("eng 입력>>");
			int eng = sc.nextInt();
			// eng (0~100)패턴검색
			value = checkInputPattern(String.valueOf(eng), 3);
			if (!value)
				return;

			System.out.print("math 입력>>");
			int math = sc.nextInt();
			// eng (0~100)패턴검색
			value = checkInputPattern(String.valueOf(math), 3);
			if (!value)
				return;

			// 학생객체생성
			Student student = new Student(no, name, kor, eng, math);
//			student.calTotal();
//			student.calAvr();
//			student.calGrade();
			// 데이타베이스 연결,입력
			DBConnection dbConn = new DBConnection();
			dbConn.connect();
			int insertReturnValue = dbConn.insert(student);
			if (insertReturnValue == -1) {
				System.out.println("삽입실패입니다.");
			} else {
				System.out.println("삽입성공입니다. 리턴값=" + insertReturnValue);
			}
			dbConn.close();
		} catch (InputMismatchException e) {
			System.out.println("입력타입이 맞지 않습니다. 재입력요청" + e.getMessage());
			return;
		} catch (Exception e) {
			System.out.println("데이타베이스 입력 에러" + e.getMessage());
		} finally {
			sc.nextLine();
		}

	}

	// 메뉴선택
	public static int displayMenu() {
		System.out.println("=".repeat(68));
		int num = -1;

		try {
			System.out.print("1.입력, 2.수정, 3.삭제, 4.검색, 5.출력, 6.정렬, 7.통계, 8.종료\n입력>>");
			num = sc.nextInt();

			String pattern = "^[1-8]$";
			boolean regex = Pattern.matches(pattern, String.valueOf(num));
		} catch (InputMismatchException e) {
			System.out.println("범위 내의 숫자를 입력해주세요.");
			num = -1;
		} finally {
			// buffer clean
			sc.nextLine();
		}
		return num;
	}

	// 문자패턴 검색
	private static boolean checkInputPattern(String data, int patternType) {
		String pattern = null;
		boolean regex = false;
		String message = null;
		switch (patternType) {
		case 1:
			pattern = "^0[1-3]0[1-9][0-6][0-9]$";
			message = "no 재입력요망";
			break; // 숫자만
		case 2:
			pattern = "^[가-힣]{2,4}$";
			message = "성명 재입력요망";
			break;
		case 3:
			pattern = "^[0-9]{1,3}$";
			message = "과목 재입력요망";
			break;
		case 4:
			pattern = "^[1-3]$";
			message = "정렬 타입 재입력요망";
			break;
		case 5:
			pattern = "^[1-2]$";
			message = "통계 타입 재입력요망";
			break;
		}

		regex = Pattern.matches(pattern, data);

		if (patternType == 3) {
			if (!regex || Integer.parseInt(data) < 0 || Integer.parseInt(data) > 100) {
				System.out.println(message);
				return false;
			}
		} else {
			if (!regex) {
				System.out.println(message);
				return false;
			}
		}
		return regex;
	}

}
