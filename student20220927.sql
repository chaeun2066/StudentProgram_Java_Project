DROP DATABASE IF EXISTS StudentDB;

CREATE DATABASE StudentDB;
use studentdb;

DROP TABLE IF EXISTS student;
create table student(
	no char(6) not null PRIMARY KEY, -- 기본키는 이미 index 설정 ! 되어있음
    name VARCHAR(10) not null, -- name으로 index 하나 더 설정 해둘 수 있음
    kor TINYINT not null,
    eng TINYINT not null,
    math TINYINT not null,
    total smallint null,
    avr DECIMAL(5,2) null, 
    grade varchar(2) null,
    rate INT null default 0
);

create table deleteStudent(
	no char(6) not null, 
    name VARCHAR(10) not null, 
    kor TINYINT not null,
    eng TINYINT not null,
    math TINYINT not null,
    total smallint null,
    avr DECIMAL(5,2) null, 
    grade varchar(2) null,
    rate INT null,
    deleteDate datetime
);

create table updateStudent(
	no char(6) not null, 
    name VARCHAR(10) not null, 
    kor TINYINT not null,
    eng TINYINT not null,
    math TINYINT not null,
    total smallint null,
    avr DECIMAL(5,2) null, 
    grade varchar(2) null,
    rate INT null,
    updateDate datetime
);

-- 인덱스 설정 : name 
create index idx_student_name on student(name);


-- 1) 삽입
insert into student values('123456', '홍길동', 100,100,100,300,100.00,'A',0);
insert into student values('123457', '구길동', 100,100,100,300,100.00,'A',0);
insert into student values('123458', '저길동', 100,100,100,300,100.00,'A',0);
insert into student values('123459', '사길동', 100,100,100,300,100.00,'A',0);
insert into student(no,name,kor,eng,math,total,avr,grade) values('123455','후길동', 100,100,100,300,100,'A');

-- 2) 삭제
DELETE FROM student WHERE no = '010101';
DELETE FROM student where name like '%채%';

-- 3) 수정
UPDATE student SET kor = 60, eng = 60, math = 60, total = 180,  avr = 60.00, grade = 'D' WHERE no = '123456';

-- 4) 읽기
SELECT * FROM student;
SELECT * FROM student where name like '%길동%';

-- 5) 정렬 : 학번, 이름, 총점
SELECT * from student order by name asc;  -- desc
SELECT * from student order by total asc ;
SELECT * from student order by no asc;

-- +) where, group by, having(group by의 조건문), order by, limit 순서대로 작성해야한다.  

-- 6) 최댓값, 최소값 구하기
SELECT Max(total) AS max_total from student ; -- as '점수 최댓값' 별칭 이렇게 해도 가능
SELECT Min(total) AS min_total from student ;
SELECT max(kor) as max_kor from student;

-- 7) total = 300 인 사람의 레코드를 출력하시오 (= 서브 쿼리문)
SELECT * from student where total =  (SELECT Max(total) AS max_total from student); 
SELECT * from student where total = (SELECT Min(total) AS min_total from student) ;
SELECT * from student where kor = (SELECT max(kor) as max_kor from student) ;

-- 8) 프로시저 생성 (합계, 평균, 등급)
drop procedure if exists procedure_insert_student;

-- 구분자를 난 $$로 하겠다
delimiter $$  
create procedure procedure_insert_student(
	IN in_no char(6), 
	IN in_name varchar(10), 
	IN in_kor int,
	IN in_eng int,
	IN in_math int     
)
begin
	-- 총점, 평균, 등급 변수선언
    DECLARE in_total int default 0;
    DECLARE in_avr double default 0.0;
    DECLARE in_grade varchar(2) default null;
    -- 총점계산, 평균계산, 등급계산
    SET in_total = in_kor + in_eng + in_math; 
    SET in_avr = in_total / 3.0; 
    SET in_grade = 
		CASE
			WHEN in_avr >= 90.0 THEN 'A'
            WHEN in_avr >= 80.0 THEN 'B'
            WHEN in_avr >= 70.0 THEN 'C'
            WHEN in_avr >= 60.0 THEN 'D'
            ELSE 'F'
		END;
     -- 삽입 insert into student() values();
    insert into student(no, name, kor, eng, math) 
		values(in_no, in_name, in_kor, in_eng, in_math); 
    -- 수정 update student set 총점 , 평균, 등급 where id = 등록한아이디;
    UPDATE student set total = in_total, avr = in_avr, grade = in_grade
		where no = in_no; 
end $$
delimiter ;

-- Trigger 생성
delimiter $$
create trigger trg_deleteStudent
	after delete
    on student
    for each row
begin
	INSERT INTO `deleteStudent` VALUES(OLD.no, OLD.name, OLD.kor, OLD.eng, OLD.math, OLD.total, OLD.avr, 
		OLD.grade, OLD.rate, now());
end $$
delimiter ;

-- Trigger Update 생성
delimiter $$
create trigger trg_updateStudent
	after update
    on student
    for each row
begin
	insert into `updateStudent` values(OLD.no, OLD.name, OLD.kor, OLD.eng, OLD.math, OLD.total, OLD.avr, 
		OLD.grade, OLD.rate, now());
end $$
delimiter ;
    
select * from deletestudent;
select * from updatestudent;

-- 테이블, 데이터베이스 삭제
drop table if exists student;
-- drop database if exists student;