package com.student.service;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.student.dao.StudentDao;
import com.student.dao.model.Students;
import com.student.model.Student;
import com.student.model.StudentRequest;
import com.student.model.StudentResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Service("studentService")
@RequiredArgsConstructor
@Slf4j
public class StudentServiceImpl {
	
	
	private final StudentDao studentDao;
	
	private final ModelMapper modelMapper;
	
	
	public Mono<List<Student>> getAllStudents() {
		return studentDao.getAllStudents()
				.map(list -> list.stream()
						.map(studentDTO -> modelMapper.map(studentDTO, Student.class))
						.collect(Collectors.toList()));
	}


	public Mono<Student> getStudent(Long id) {
		return studentDao.getStudent(id)
				.map(studentDTO -> modelMapper.map(studentDTO, Student.class));
	}


	public Mono<Student> create(StudentRequest studentRequest) {
		return Mono.just(studentRequest)
				.map(stu -> modelMapper.map(stu, Students.class))
				.doOnNext(studentDTO -> log.info("STUDENT DETAIL: {} {} {}", studentDTO.getId(), studentDTO.getName(), studentDTO.getDepartment()))
				.flatMap(studentDTO -> studentDao.create(studentDTO)
						.switchIfEmpty(Mono.defer(()->Mono.empty()))
				)
				.map(studentDTO -> modelMapper.map(studentDTO, Student.class));
	}	
	

}
