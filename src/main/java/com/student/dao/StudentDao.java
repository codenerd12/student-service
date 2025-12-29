package com.student.dao;

import java.util.List;

import com.student.dao.model.Students;
import com.student.model.StudentResponse;

import reactor.core.publisher.Mono;

public interface StudentDao {
	
	Mono<List<Students>> getAllStudents();

	Mono<Students> getStudent(Long id);

	Mono<Students> create(Students student);

}
