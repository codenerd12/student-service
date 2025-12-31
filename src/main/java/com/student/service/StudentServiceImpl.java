package com.student.service;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.batch.item.validator.ValidationException;
import org.springframework.stereotype.Service;

import com.student.cache.CacheService;
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
	
	private final CacheService<Student> cacheService;
	private final StudentDao studentDao;
	
	private final ModelMapper modelMapper;
	
	
	public Mono<List<Student>> getAllStudents() {
		return studentDao.getAllStudents()
				.map(list -> list.stream()
						.map(studentDTO -> modelMapper.map(studentDTO, Student.class))
						.collect(Collectors.toList()));
	}


	public Mono<Student> getStudent(Long id) {
		String cacheKey = "STUDENTINFO_" + id;
		return cacheService.get(cacheKey)
				.doOnNext(val -> log.info("Retrive value from cache: {}", val))
				.switchIfEmpty(Mono.defer(() -> getStudentDataFromDB(id)
						.flatMap(student -> {
							if(Objects.nonNull(student)) {
								log.info("Saved student data in cache for student: {}, value: {}", id, student);
								LocalDateTime today = LocalDateTime.now(ZoneOffset.UTC);
								LocalDateTime nextSunday = today.with(TemporalAdjusters.next(DayOfWeek.SUNDAY));
								return cacheService.put(cacheKey, student, Duration.between(today, nextSunday))
										.thenReturn(student);
							} else {
								log.error("Could not load studentData from database or api for student: {}", id);
								return Mono.error(new ValidationException("Either student details not found"));
							}
						}))
						);
	}


	private Mono<Student> getStudentDataFromDB(Long id) {
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
