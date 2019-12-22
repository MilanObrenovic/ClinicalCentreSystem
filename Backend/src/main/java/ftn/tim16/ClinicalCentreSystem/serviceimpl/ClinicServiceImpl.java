package ftn.tim16.ClinicalCentreSystem.serviceimpl;

import ftn.tim16.ClinicalCentreSystem.dto.requestandresponse.ClinicDTO;
import ftn.tim16.ClinicalCentreSystem.dto.requestandresponse.EditClinicDTO;
import ftn.tim16.ClinicalCentreSystem.model.Clinic;
import ftn.tim16.ClinicalCentreSystem.repository.ClinicRepository;
import ftn.tim16.ClinicalCentreSystem.service.ClinicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ClinicServiceImpl implements ClinicService {

    @Autowired
    private ClinicRepository clinicRepository;

    @Override
    public ClinicDTO findById(Long id) {
        Clinic clinic = clinicRepository.findOneById(id);
        if (clinic == null) {
            return null;
        }

        return new ClinicDTO(clinic);
    }

    @Override
    public Clinic findByName(String name) {
        return clinicRepository.findByNameIgnoringCase(name);
    }

    @Override
    public Clinic findByAddress(String address) {
        return clinicRepository.findByAddressIgnoringCase(address);
    }

    @Override
    public ClinicDTO create(ClinicDTO clinicDTO) {
        if (findByName(clinicDTO.getName()) != null || findByAddress(clinicDTO.getAddress()) != null) {
            return null;
        }

        Clinic clinic = new Clinic(clinicDTO.getName(), clinicDTO.getDescription(), clinicDTO.getAddress());

        return new ClinicDTO(clinicRepository.save(clinic));
    }

    @Override
    public List<ClinicDTO> findAll() {
        return convertToDTO(clinicRepository.findAll());
    }

    @Override
    public EditClinicDTO edit(EditClinicDTO clinicDTO, Long clinicIdInWhichAdminWorks) {

        Clinic existingClinic = clinicRepository.findOneById(clinicDTO.getId());

        if (existingClinic == null || existingClinic.getId() != clinicIdInWhichAdminWorks) {
            return null;
        }

        Clinic clinicWithSameName = findByName(clinicDTO.getName());
        Clinic clinicWithSameAddress = findByAddress(clinicDTO.getAddress());
        if ((clinicWithSameName != null && clinicWithSameName.getId() != existingClinic.getId())
                || (clinicWithSameAddress != null && clinicWithSameAddress.getId() != existingClinic.getId())) {

            return null;
        }
        existingClinic.setName(clinicDTO.getName());
        existingClinic.setAddress(clinicDTO.getAddress());
        existingClinic.setDescription(clinicDTO.getDescription());
        return new EditClinicDTO(clinicRepository.save(existingClinic));
    }

    private List<ClinicDTO> convertToDTO(List<Clinic> clinics) {
        if (clinics == null || clinics.isEmpty()) {
            return new ArrayList<>();
        }
        List<ClinicDTO> roomDTOS = new ArrayList<>();
        for (Clinic room : clinics) {
            roomDTOS.add(new ClinicDTO(room));
        }
        return roomDTOS;
    }
}
