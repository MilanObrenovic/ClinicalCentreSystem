package ftn.tim16.ClinicalCentreSystem.repository;

import ftn.tim16.ClinicalCentreSystem.enumeration.ExaminationKind;
import ftn.tim16.ClinicalCentreSystem.enumeration.ExaminationStatus;
import ftn.tim16.ClinicalCentreSystem.model.Examination;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ExaminationRepository extends JpaRepository<Examination, Long> {

    List<Examination> findByRoomIdAndStatusNotOrderByIntervalStartDateTime(Long id, ExaminationStatus status);

    List<Examination> findByDoctorsIdAndStatusNot(Long id, ExaminationStatus status);

    List<Examination> findByPatientIdAndStatusNot(Long idPatient, ExaminationStatus status);

    List<Examination> findByNurseIdAndStatusNot(Long id, ExaminationStatus status);

    Examination getByIdAndStatusNot(Long id, ExaminationStatus status);

    List<Examination> findByClinicAdministratorIdAndStatusAndKind(Long id, ExaminationStatus status, ExaminationKind kind);

    Page<Examination> findByClinicAdministratorIdAndStatusAndKind(Long id, ExaminationStatus status, ExaminationKind kind, Pageable page);

    List<Examination> findByStatus(ExaminationStatus status);

    List<Examination> findByDoctorsIdAndStatusNotAndIntervalStartDateTimeAfter(Long id, ExaminationStatus status, LocalDateTime localDateTime);

    Page<Examination> findByDoctorsIdAndStatusNotAndIntervalStartDateTimeAfter(Long id, ExaminationStatus status, LocalDateTime localDateTime, Pageable page);
}
