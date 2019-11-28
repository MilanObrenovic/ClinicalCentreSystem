package ftn.tim16.ClinicalCentreSystem.service;

import ftn.tim16.ClinicalCentreSystem.common.RandomPasswordGenerator;
import ftn.tim16.ClinicalCentreSystem.dto.NurseDTO;
import ftn.tim16.ClinicalCentreSystem.enumeration.UserStatus;
import ftn.tim16.ClinicalCentreSystem.model.Authority;
import ftn.tim16.ClinicalCentreSystem.model.ClinicAdministrator;
import ftn.tim16.ClinicalCentreSystem.model.Examination;
import ftn.tim16.ClinicalCentreSystem.model.Nurse;
import ftn.tim16.ClinicalCentreSystem.repository.NurseRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class NurseServiceImpl implements NurseService {
    @Autowired
    private NurseRepository nurseRepository;

    @Autowired
    private UserServiceImpl userService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    EmailNotificationService emailNotificationService;

    @Autowired
    private ExaminationService examinationService;

    @Autowired
    private TimeOffNurseService timeOffNurseService;

    public Nurse changePassword(String newPassword, Nurse user) {
        user.setPassword(newPassword);
        if (user.getStatus().equals(UserStatus.NEVER_LOGGED_IN)) {
            user.setStatus(UserStatus.ACTIVE);
        }
        return nurseRepository.save(user);
    }

    @Override
    public List<NurseDTO> getAllNursesInClinic(Long id) {
        return convertToDTO(nurseRepository.findAllByClinicId(id));
    }

    @Override
    public List<NurseDTO> getAllNursesInClinic(Long id, Pageable page) {
        return convertToDTO(nurseRepository.findAllByClinicId(id, page));
    }

    @Override
    public Nurse create(NurseDTO nurseDTO, ClinicAdministrator clinicAdministrator) {
        UserDetails userDetails = userService.findUserByEmail(nurseDTO.getEmail());
        if (userDetails != null) {
            return null;
        }

        if (nurseRepository.findByPhoneNumber(nurseDTO.getPhoneNumber()) != null) {
            return null;
        }

        LocalTime workHoursFrom = LocalTime.parse(nurseDTO.getWorkHoursFrom(), DateTimeFormatter.ofPattern("HH:mm"));
        LocalTime workHoursTo = LocalTime.parse(nurseDTO.getWorkHoursTo(), DateTimeFormatter.ofPattern("HH:mm"));
        if (workHoursFrom.isAfter(workHoursTo)) {
            return null;
        }

        RandomPasswordGenerator randomPasswordGenerator = new RandomPasswordGenerator();
        String generatedPassword = randomPasswordGenerator.generatePassword();
        String hashedPassword = passwordEncoder.encode(generatedPassword);

        List<Authority> authorities = authenticationService.findByName("ROLE_NURSE");

        Nurse newNurse = new Nurse(nurseDTO.getEmail(), hashedPassword, nurseDTO.getFirstName(), nurseDTO.getLastName(),
                nurseDTO.getPhoneNumber(), workHoursFrom, workHoursTo, clinicAdministrator.getClinic(), authorities);

        Nurse nurse = nurseRepository.save(newNurse);

        String subject = "New position: Nurse";
        StringBuilder sb = new StringBuilder();
        sb.append("You have been registered as a nurse of a ");
        sb.append(clinicAdministrator.getClinic().getName());
        sb.append(" Clinic. From now on, you are in charge of stamping prescriptions to patients and helping doctors on examinations.");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("You can login to the Clinical Centre System web site using your email address and the following password:");
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("     ");
        sb.append(generatedPassword);
        sb.append(System.lineSeparator());
        sb.append(System.lineSeparator());
        sb.append("Because of the security protocol, you will have to change this given password the first time you log in.");
        String text = sb.toString();
        emailNotificationService.sendEmail(nurse.getEmail(), subject, text);

        return nurse;
    }

    private List<NurseDTO> convertToDTO(List<Nurse> nurses) {
        List<NurseDTO> nursesDTO = new ArrayList<>();
        for (Nurse nurse : nurses) {
            nursesDTO.add(new NurseDTO(nurse));
        }

        return nursesDTO;
    }

    private List<NurseDTO> convertToDTO(Page<Nurse> nurses) {
        List<NurseDTO> nursesDTO = new ArrayList<>();
        for (Nurse nurse : nurses) {
            nursesDTO.add(new NurseDTO(nurse));
        }

        return nursesDTO;
    }

    public Nurse getRandomNurse(Long clinic_id,LocalDateTime startDateTime,LocalDateTime endDateTime) {
        List<Nurse> nurses = getAvailable(clinic_id,startDateTime,endDateTime);
        if(nurses.isEmpty()){
            return null;
        }
        return nurses.get(new Random().nextInt(nurses.size()));
    }

    private List<Nurse> getAvailable(Long clinic_id, LocalDateTime startDateTime,LocalDateTime endDateTime){
        List<Nurse> nurses = nurseRepository.findByClinicId(clinic_id);
        List<Nurse> availableNurses = new ArrayList<>();
        for (Nurse nurse: nurses) {
            if(isAvailable(nurse.getId(),startDateTime,endDateTime)){
                availableNurses.add(nurse);
            }
        }
        return availableNurses;
    }

    private boolean isAvailable(Long nurseId, LocalDateTime startDateTime,LocalDateTime endDateTime){
        Nurse nurse = nurseRepository.getById(nurseId);
        if(!nurse.isAvailable(startDateTime.toLocalTime(),endDateTime.toLocalTime())){
            return false;
        }

        if(timeOffNurseService.isNurseOnVacation(nurseId,startDateTime,endDateTime)){
            return false;
        }
        List<Examination> examinations = examinationService.getNursesExamination(nurseId);
        if(!examinations.isEmpty()){
            for(Examination examination : examinations){
                if(!examination.getInterval().isAvailable(startDateTime,endDateTime)){
                    return false;
                }
            }
        }
        return true;
    }
}
