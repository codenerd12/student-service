package com.student.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.config.EnableWebFlux;

import com.student.model.Student;
import com.student.model.StudentRequest;
import com.student.model.StudentResponse;
import com.student.service.StudentServiceImpl;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
@RequiredArgsConstructor
@EnableWebFlux
public class StudentController {
	
	private final StudentServiceImpl studentService;
	
	@GetMapping("/test")
	public Mono<ResponseEntity<Student>> getStudent() {
		System.out.println("----->Hello");
		Student student = Student.builder()
				.id(1l)
				.name("manish")
				.department("MCA")
				.build();
		return Mono.just(new ResponseEntity<>(student, HttpStatus.OK));
	}
	
	@PostMapping("/students")
	public Mono<ResponseEntity<StudentResponse>> create(@RequestBody Mono<StudentRequest> studentRequest) {
		StudentResponse studentResponse = StudentResponse.builder()
				.students(new ArrayList<>())
				.build();
		return studentRequest
				.flatMap(request -> studentService.create(request))
				.map(student -> studentResponse.getStudents().add(student))
				.flatMap(bool -> Mono.just(new ResponseEntity<>(studentResponse, HttpStatus.CREATED)));
		
	}
	
	@GetMapping("/students")
	public Mono<ResponseEntity<Mono<List<Student>>>> getAllStudents() {
		return Mono.just(new ResponseEntity<>(studentService.getAllStudents(), HttpStatus.OK));
	}
	
	@GetMapping("/students/{id}")
	public Mono<ResponseEntity<Mono<Student>>> findStudent(@PathVariable("id") Long id) {
		return Mono.just(new ResponseEntity<>(studentService.getStudent(id), HttpStatus.OK));
	}

}
