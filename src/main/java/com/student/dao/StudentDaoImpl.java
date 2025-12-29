package com.student.dao;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.student.dao.model.Students;
import com.student.dao.repository.StudentR2dbcRepository;
import com.student.model.StudentResponse;

import lombok.AllArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Repository("studentDao")
@AllArgsConstructor
public class StudentDaoImpl implements StudentDao {
	
	private final StudentR2dbcRepository studentR2dbcRepository;

	@Override
	public Mono<List<Students>> getAllStudents() {
		return studentR2dbcRepository.findAll().collectList();
		//return studentR2dbcRepository.getAllStudents().collectList();
	}

	@Override
	public Mono<Students> getStudent(Long id) {
		return studentR2dbcRepository.findById(id);
	}

	@Override
	public Mono<Students> create(Students student) {
		/*Mono<StudentDTO> blockingWrapper = Mono.fromCallable(() -> {
			final Mono<StudentDTO> studentDTO = studentR2dbcRepository.save(student).b;
			return studentDTO;
		});
		
		return blockingWrapper.subscribeOn(Schedulers.boundedElastic());*/
		return studentR2dbcRepository.save(student);
	}

}
