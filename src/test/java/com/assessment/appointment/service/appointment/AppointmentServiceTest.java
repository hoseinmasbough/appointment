package com.assessment.appointment.service.appointment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.assessment.clinic.exception.BusinessException;
import com.assessment.clinic.model.appointment.Appointment;
import com.assessment.clinic.model.appointment.AppointmentDao;
import com.assessment.clinic.model.calendar.Presence;
import com.assessment.clinic.model.doctor.Doctor;
import com.assessment.clinic.model.doctor.DoctorGrade;
import com.assessment.clinic.model.patient.Patient;
import com.assessment.clinic.service.appointment.dto.CreateAutoAppointmentRequest;
import com.assessment.clinic.service.appointment.dto.CreateNewAppointmentRequest;
import com.assessment.clinic.service.appointment.impl.AppointmentServiceImpl;
import com.assessment.clinic.service.doctor.DoctorService;
import com.assessment.clinic.service.patient.PatientService;
import com.assessment.clinic.utli.ConfigProvider;
import com.assessment.clinic.utli.TimeUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@Import({ AppointmentServiceImpl.class, ConfigProvider.class })
@ComponentScan(basePackages = { "com.assessment.clinic.service.appointment.mapper", "com.assessment.clinic.service.appointment.impl" })
@TestPropertySource(locations = { "classpath:application-test.properties" })
class AppointmentServiceTest {

	@Autowired
	private AppointmentServiceImpl service;

	@MockBean
	private DoctorService doctorService;

	@MockBean
	private PatientService patientService;

	@MockBean
	private AppointmentDao appointmentDao;

	@Value("${general.visit.duration.in.min}")
	private Integer generalDuration;

	@Value("${specialist.visit.duration.in.min}")
	private Integer specialistDuration;

	private static Doctor doctor;


	@BeforeAll
	static void init() {
		doctor = createGeneralDoctor(1L);
		doctor.setPresences(createDoctorPresence(doctor));
	}

	@Test
	@DisplayName("setAppointment - doctor is general - day is in business day - hour is in working hours - Success")
	void setAppointment_success_1() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 17, 45);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, generalDuration);
		long endTime = startTime + (generalDuration * 60000);
		Patient patient = createPatient(request.getPatientId());
		doctor.setGrade(DoctorGrade.GENERAL);

		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.doReturn(null).when(this.appointmentDao).getAllByPatientAndStartTimeBetween(patient,
				TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));
		Mockito.doReturn(null).when(this.appointmentDao).getAllByDoctorAndStartTimeBetween(doctor,
				TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));

		service.setAppointment(request);

		ArgumentCaptor<Appointment> appointmentArgumentCaptor = ArgumentCaptor.forClass(Appointment.class);

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, times(1)).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).save(appointmentArgumentCaptor.capture());

		Appointment appointment = appointmentArgumentCaptor.getValue();
		assertThat(appointment).isNotNull();
		assertThat(appointment.getDoctor()).isEqualTo(doctor);
		assertThat(appointment.getPatient()).isEqualTo(patient);
		assertThat(appointment.getStartTime()).isEqualTo(startTime);
		assertThat(appointment.getEndTime()).isEqualTo(endTime);
	}

	@Test
	@DisplayName("setAppointment - doctor is specialist - day is in business day - hour is in working hours - no prev doctor appointment - no prev patient appointment - Success")
	void setAppointment_success_2() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 17, 30);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, specialistDuration);
		long endTime = startTime + (specialistDuration * 60000);
		Patient patient = createPatient(request.getPatientId());
		doctor.setGrade(DoctorGrade.SPECIALIST);

		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.doReturn(null).when(this.appointmentDao).getAllByPatientAndStartTimeBetween(patient,
				TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));
		Mockito.doReturn(null).when(this.appointmentDao).getAllByDoctorAndStartTimeBetween(doctor,
				TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));

		service.setAppointment(request);

		ArgumentCaptor<Appointment> appointmentArgumentCaptor = ArgumentCaptor.forClass(Appointment.class);

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, times(1)).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).save(appointmentArgumentCaptor.capture());

		Appointment appointment = appointmentArgumentCaptor.getValue();
		assertThat(appointment).isNotNull();
		assertThat(appointment.getDoctor()).isEqualTo(doctor);
		assertThat(appointment.getPatient()).isEqualTo(patient);
		assertThat(appointment.getStartTime()).isEqualTo(startTime);
		assertThat(appointment.getEndTime()).isEqualTo(endTime);
	}

	@Test
	@DisplayName("setAppointment - doctor is specialist - day is in business day - hour is in working hours - with one conflict prev doctor appointment - no prev patient appointment - Success")
	void setAppointment_success_3() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 12, 0);
		LocalDateTime prevDoctorAppointmentTime = LocalDateTime.of(2023, 6, 17, 15, 30);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, specialistDuration);
		long endTime = startTime + (specialistDuration * 60000);
		Patient patient = createPatient(request.getPatientId());
		doctor.setGrade(DoctorGrade.SPECIALIST);

		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.doReturn(null).when(this.appointmentDao).getAllByPatientAndStartTimeBetween(patient,
				TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));
		Mockito.doReturn(List.of(createAppointment(doctor, new Patient(), prevDoctorAppointmentTime, prevDoctorAppointmentTime.plusMinutes(specialistDuration))))
				.when(this.appointmentDao).getAllByDoctorAndStartTimeBetween(doctor,
						TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));

		service.setAppointment(request);

		ArgumentCaptor<Appointment> appointmentArgumentCaptor = ArgumentCaptor.forClass(Appointment.class);

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, times(1)).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).save(appointmentArgumentCaptor.capture());

		Appointment appointment = appointmentArgumentCaptor.getValue();
		assertThat(appointment).isNotNull();
		assertThat(appointment.getDoctor()).isEqualTo(doctor);
		assertThat(appointment.getPatient()).isEqualTo(patient);
		assertThat(appointment.getStartTime()).isEqualTo(startTime);
		assertThat(appointment.getEndTime()).isEqualTo(endTime);
	}

	@Test
	@DisplayName("setAppointment - doctor is specialist - day is in business day - hour is in working hours - no prev doctor appointment - with one prev patient appointment - Success")
	void setAppointment_success_4() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 15, 30);
		LocalDateTime prevPatientAppointmentTime = LocalDateTime.of(2023, 6, 17, 16, 1);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, specialistDuration);
		long endTime = startTime + (specialistDuration * 60000);
		Patient patient = createPatient(request.getPatientId());
		doctor.setGrade(DoctorGrade.SPECIALIST);

		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.doReturn(List.of(createAppointment(doctor, patient, prevPatientAppointmentTime, prevPatientAppointmentTime.plusMinutes(specialistDuration))))
				.when(this.appointmentDao).getAllByPatientAndStartTimeBetween(patient, TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));
		Mockito.doReturn(null).when(this.appointmentDao).getAllByDoctorAndStartTimeBetween(doctor,
				TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));

		service.setAppointment(request);

		ArgumentCaptor<Appointment> appointmentArgumentCaptor = ArgumentCaptor.forClass(Appointment.class);

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, times(1)).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).save(appointmentArgumentCaptor.capture());

		Appointment appointment = appointmentArgumentCaptor.getValue();
		assertThat(appointment).isNotNull();
		assertThat(appointment.getDoctor()).isEqualTo(doctor);
		assertThat(appointment.getPatient()).isEqualTo(patient);
		assertThat(appointment.getStartTime()).isEqualTo(startTime);
		assertThat(appointment.getEndTime()).isEqualTo(endTime);
	}

	@Test
	@DisplayName("setAppointment - doctor is general - day is FRIDAY - hour is in working hours - no prev doctor appointment - no prev patient appointment - Failed")
	void setAppointment_failed_1() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 16, 15, 30);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, generalDuration);
		doctor.setGrade(DoctorGrade.GENERAL);

		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "Appointment day is not in business day";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, never()).getPatientById(1L);
		verify(appointmentDao, never()).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setAppointment - doctor is general - day is in business day - hour is after working hours - no prev doctor appointment - no prev patient appointment - Failed")
	void setAppointment_failed_2() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 18, 0);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, generalDuration);
		doctor.setGrade(DoctorGrade.GENERAL);

		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "Appointment hour is not in working hour";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, never()).getPatientById(1L);
		verify(appointmentDao, never()).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setAppointment - doctor is general - day is in business day - duration is after working hours - no prev doctor appointment - no prev patient appointment - Failed")
	void setAppointment_failed_3() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 17, 46);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, generalDuration);
		doctor.setGrade(DoctorGrade.GENERAL);

		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "Appointment hour is not in working hour";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, never()).getPatientById(1L);
		verify(appointmentDao, never()).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setAppointment - doctor is specialist - day is in business day - duration is after working hours - no prev doctor appointment - no prev patient appointment - Failed")
	void setAppointment_failed_4() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 17, 31);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, specialistDuration);
		doctor.setGrade(DoctorGrade.SPECIALIST);

		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "Appointment hour is not in working hour";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, never()).getPatientById(1L);
		verify(appointmentDao, never()).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setAppointment - doctor is general - day is in business day - hour is before working hours - with 2 prev doctor appointment - no prev patient appointment - Failed")
	void setAppointment_failed_5() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 17, 0);
		LocalDateTime prevDoctorAppointmentTime = LocalDateTime.of(2023, 6, 17, 17, 0);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, generalDuration);
		doctor.setGrade(DoctorGrade.GENERAL);
		Patient patient = createPatient(request.getPatientId());
		Appointment appointment1 = createAppointment(doctor, patient, prevDoctorAppointmentTime, prevDoctorAppointmentTime.plusMinutes(generalDuration));
		Appointment appointment2 = createAppointment(doctor, patient, prevDoctorAppointmentTime, prevDoctorAppointmentTime.plusMinutes(generalDuration));


		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.doReturn(null).when(this.appointmentDao).
				getAllByPatientAndStartTimeBetween(patient, TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));
		Mockito.doReturn(List.of(appointment2, appointment1)).when(this.appointmentDao).
				getAllByDoctorAndStartTimeBetween(doctor, TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "The allowed max conflict time count of doctor";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, times(1)).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setAppointment - doctor is specialist - day is in business day - hour is before working hours - with 3 prev doctor appointment - no prev patient appointment - Failed")
	void setAppointment_failed_6() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 17, 0);
		LocalDateTime prevDoctorAppointmentTime = LocalDateTime.of(2023, 6, 17, 17, 0);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, specialistDuration);
		doctor.setGrade(DoctorGrade.SPECIALIST);
		Patient patient = createPatient(request.getPatientId());
		Appointment appointment1 = createAppointment(doctor, patient, prevDoctorAppointmentTime, prevDoctorAppointmentTime.plusMinutes(specialistDuration));
		Appointment appointment2 = createAppointment(doctor, patient, prevDoctorAppointmentTime, prevDoctorAppointmentTime.plusMinutes(specialistDuration));
		Appointment appointment3 = createAppointment(doctor, patient, prevDoctorAppointmentTime, prevDoctorAppointmentTime.plusMinutes(specialistDuration));


		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.doReturn(null).when(this.appointmentDao).
				getAllByPatientAndStartTimeBetween(patient, TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));
		Mockito.doReturn(List.of(appointment2, appointment1, appointment3)).when(this.appointmentDao).
				getAllByDoctorAndStartTimeBetween(doctor, TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "The allowed max conflict time count of doctor";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, times(1)).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setAppointment - doctor is specialist - day is in business day - hour is before working hours - no prev doctor appointment - with 2 prev patient appointment - Failed")
	void setAppointment_failed_7() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 17, 0);
		LocalDateTime prevDoctorAppointmentTime = LocalDateTime.of(2023, 6, 17, 10, 0);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, specialistDuration);
		doctor.setGrade(DoctorGrade.SPECIALIST);
		Patient patient = createPatient(request.getPatientId());
		Appointment appointment1 = createAppointment(doctor, patient, prevDoctorAppointmentTime, prevDoctorAppointmentTime.plusMinutes(specialistDuration));
		Appointment appointment2 = createAppointment(doctor, patient, prevDoctorAppointmentTime.plusHours(1), prevDoctorAppointmentTime.plusHours(1).plusMinutes(specialistDuration));


		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.doReturn(List.of(appointment2, appointment1)).when(this.appointmentDao).
				getAllByPatientAndStartTimeBetween(patient, TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "has previous appointments more than";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, never()).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setAppointment - doctor is specialist - day is in business day - hour is before working hours - no prev doctor appointment - with 1 prev conflict patient appointment - Failed")
	void setAppointment_failed_8() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 17, 0);
		LocalDateTime prevDoctorAppointmentTime = LocalDateTime.of(2023, 6, 17, 17, 0);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, specialistDuration);
		doctor.setGrade(DoctorGrade.SPECIALIST);
		Patient patient = createPatient(request.getPatientId());
		Appointment appointment1 = createAppointment(doctor, patient, prevDoctorAppointmentTime, prevDoctorAppointmentTime.plusMinutes(specialistDuration));


		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.doReturn(List.of(appointment1)).when(this.appointmentDao).
				getAllByPatientAndStartTimeBetween(patient, TimeUtil.getFirstMomentOfDay(startTime), TimeUtil.getLastMomentOfDay(startTime));

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "The new appointment date has conflict with older appointment of patient";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, never()).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setAppointment - doctor is specialist - day is in business day - hour is before working hours - no prev doctor appointment - no prev patient appointment - time is not in presence - Failed")
	void setAppointment_failed_9() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 11, 30);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateNewAppointmentRequest request = makeCreateNewAppointmentRequest(startTime, specialistDuration);
		doctor.setGrade(DoctorGrade.SPECIALIST);

		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());

		BusinessException exception = Assertions.assertThrows(BusinessException.class, () -> service.setAppointment(request));

		String expectedMessage = "Appointment time is not in doctor schedule";
		String actualMessage = exception.getMessage();
		Assertions.assertTrue(actualMessage.contains(expectedMessage));

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, never()).getPatientById(1L);
		verify(appointmentDao, never()).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, never()).save(any());
	}

	@Test
	@DisplayName("setEarliestAppointment - doctor is general- no prev doctor appointment - no prev patient appointment - Success")
	void setEarliestAppointment_success_1() throws BusinessException {

		LocalDateTime appointmentTime = LocalDateTime.of(2023, 6, 17, 12, 0);
		long startTime = TimeUtil.toEpochMilli(appointmentTime);
		CreateAutoAppointmentRequest request = makeCreateAutoAppointmentRequest(generalDuration);
		long endTime = startTime + (generalDuration * 60000);
		Patient patient = createPatient(request.getPatientId());
		doctor.setGrade(DoctorGrade.GENERAL);

		Mockito.doReturn(patient).when(this.patientService).getPatientById(request.getPatientId());
		Mockito.doReturn(doctor).when(this.doctorService).getDoctorById(request.getDoctorId());
		Mockito.when(this.appointmentDao.getAllByPatientAndStartTimeBetween(eq(patient), anyLong(), anyLong())).thenReturn(new ArrayList<>());
		Mockito.when(this.appointmentDao.getAllByDoctorAndStartTimeBetween(eq(doctor), anyLong(), anyLong())).thenReturn(new ArrayList<>());

		service.setEarliestAppointment(request);

		ArgumentCaptor<Appointment> appointmentArgumentCaptor = ArgumentCaptor.forClass(Appointment.class);

		verify(doctorService, times(1)).getDoctorById(1L);
		verify(patientService, times(1)).getPatientById(1L);
		verify(appointmentDao, times(1)).getAllByDoctorAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).getAllByPatientAndStartTimeBetween(any(), anyLong(), anyLong());
		verify(appointmentDao, times(1)).save(appointmentArgumentCaptor.capture());

		Appointment appointment = appointmentArgumentCaptor.getValue();
		assertThat(appointment).isNotNull();
		assertThat(appointment.getDoctor()).isEqualTo(doctor);
		assertThat(appointment.getPatient()).isEqualTo(patient);
		assertThat(appointment.getStartTime()).isEqualTo(startTime);
		assertThat(appointment.getEndTime()).isEqualTo(endTime);
	}

	private static Doctor createGeneralDoctor(Long doctorId) {
		Doctor doctor = new Doctor();
		doctor.setId(doctorId);
		doctor.setName("ahmad doci");
		doctor.setGrade(DoctorGrade.GENERAL);
		return doctor;
	}

	private static List<Presence> createDoctorPresence(Doctor doctor) {
		List<Presence> schedule = new ArrayList<>(3);
		LocalDateTime sundayStart = LocalDateTime.of(2023, 6, 17, 12, 0);
		LocalDateTime sundayEnd = LocalDateTime.of(2023, 6, 17, 18, 0);
		schedule.add(createDoctorPresence(doctor, TimeUtil.toEpochMilli(sundayStart), TimeUtil.toEpochMilli(sundayEnd)));
		LocalDateTime mondayStart = LocalDateTime.of(2023, 6, 19, 11, 0);
		LocalDateTime mondayEnd = LocalDateTime.of(2023, 6, 19, 16, 0);
		schedule.add(createDoctorPresence(doctor, TimeUtil.toEpochMilli(mondayStart), TimeUtil.toEpochMilli(mondayEnd)));
		LocalDateTime wednesdayStart = LocalDateTime.of(2023, 6, 21, 11, 0);
		LocalDateTime wednesdayEnd = LocalDateTime.of(2023, 6, 21, 18, 0);
		schedule.add(createDoctorPresence(doctor, TimeUtil.toEpochMilli(wednesdayStart), TimeUtil.toEpochMilli(wednesdayEnd)));
		return schedule;
	}

	private static Presence createDoctorPresence(Doctor doctor, Long startTime, Long endTime) {
		Presence presence = new Presence();
		presence.setDoctor(doctor);
		presence.setStartTime(startTime);
		presence.setEndTime(endTime);
		return presence;
	}

	private Patient createPatient(long patientId) {
		Patient patient = new Patient();
		patient.setId(patientId);
		patient.setName("mamad mariz");
		return patient;

	}

	private CreateNewAppointmentRequest makeCreateNewAppointmentRequest(long appointmentTime, int duration) {
		CreateNewAppointmentRequest request = new CreateNewAppointmentRequest();
		request.setDoctorId(1L);
		request.setPatientId(1L);
		request.setDurationInMin(duration);
		request.setAppointmentTime(appointmentTime);
		return request;
	}

	private CreateAutoAppointmentRequest makeCreateAutoAppointmentRequest(int duration) {
		CreateAutoAppointmentRequest request = new CreateAutoAppointmentRequest();
		request.setDoctorId(1L);
		request.setPatientId(1L);
		request.setDurationInMin(duration);
		return request;
	}

	private Appointment createAppointment(Doctor doctor, Patient patient, LocalDateTime start, LocalDateTime end) {
		Appointment appointment = new Appointment();
		appointment.setId(1L);
		appointment.setDoctor(doctor);
		appointment.setPatient(patient);
		appointment.setStartTime(TimeUtil.toEpochMilli(start));
		appointment.setEndTime(TimeUtil.toEpochMilli(end));
		return appointment;
	}
}
