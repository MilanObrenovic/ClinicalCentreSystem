import { ClinicService } from './../../../services/clinic.service';
import { ToastrService } from 'ngx-toastr';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { Component, OnInit } from '@angular/core';
import { Clinic } from 'src/app/models/clinic';

@Component({
  selector: 'app-edit-clinic-profile',
  templateUrl: './edit-clinic-profile.component.html',
  styleUrls: ['./edit-clinic-profile.component.css']
})
export class EditClinicProfileComponent implements OnInit {
  editClinicForm: FormGroup;
  selectedClinic: Clinic;

  constructor(private toastr: ToastrService, private clinicService: ClinicService) {
  }

  ngOnInit() {
    this.editClinicForm = new FormGroup({
      name: new FormControl(null, [Validators.required, Validators.maxLength(50)]),
      address: new FormControl(null, [Validators.required]),
      description: new FormControl(null, [Validators.required])
    });
    this.clinicService.getClinicInWhichClinicAdminWorks().subscribe((data: Clinic) => {
      this.selectedClinic = data;
      this.editClinicForm.patchValue(
        {
          'name': this.selectedClinic.name,
          'address': this.selectedClinic.address,
          'description': this.selectedClinic.description
        }
      );
      this.clinicService.selectedClinic = this.selectedClinic;
    })

  }

  edit() {
    if (this.editClinicForm.invalid) {
      this.toastr.error("Please enter a valid data.", "Edit clinic's profile ");
      return;
    }
    if (!this.selectedClinic) {
      this.toastr.error("Please choose a clinic.", "Edit clinic's profile ");
      return;
    }
    const clinic = new Clinic(this.editClinicForm.value.name, this.editClinicForm.value.address,
      this.editClinicForm.value.description, this.selectedClinic.id);

    this.clinicService.edit(clinic).subscribe(
      (responseData: Clinic) => {
        this.toastr.success("Successfully changed clinic's profile.", "Edit clinic's profile ");
        this.clinicService.selectedClinic = clinic;
      },
      message => {
        this.toastr.error("You can not edit this profile because clinic with the same name or address already exists ",
          "Edit clinic's profile ");

      }
    );
  }
}
