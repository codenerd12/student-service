package com.student.dao.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;

import com.student.dao.model.Students;
import com.student.dao.query.constants.DatabaseQueriesConstants;

import reactor.core.publisher.Flux;

public interface StudentR2dbcRepository extends R2dbcRepository<Students, Long>{
	
	@Query(DatabaseQueriesConstants.STUDENTS_LIST)
	Flux<Students> getAllStudents();

}
