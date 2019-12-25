import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { environment } from './../../environments/environment';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject, Observable } from 'rxjs';
import { Clinic } from '../models/clinic';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class ClinicService {
  url = environment.baseUrl + environment.clinic;

  clinic: BehaviorSubject<Clinic> = new BehaviorSubject<Clinic>(null);
  clinics: BehaviorSubject<Clinic[]> = new BehaviorSubject<Clinic[]>([]);
  addSuccessEmitter = new Subject<Clinic>();
  selectedClinic: Clinic;

  constructor(private httpClient: HttpClient, private router: Router) { }

  public add(clinic: Clinic) {
    return this.httpClient.post(this.url, clinic);
  }

  public edit(clinic: Clinic) {
    return this.httpClient.put(this.url, clinic);
  }

  public getAllClinics(): Observable<Clinic[]> {
    this.httpClient.get(this.url + "/all").subscribe((data: Clinic[]) => {
      this.clinics.next(data)
    },
      (error: HttpErrorResponse) => {

      });
    return this.clinics.asObservable();
  }

  public getClinicById(clinicId) {
    return this.httpClient.get(this.url + "/" + clinicId);
  }

  public getClinicInWhichClinicAdminWorks() {
    return this.httpClient.get(this.url + "/clinic-in-which-admin-works");
  }

  public get(quaery: string) {
    return this.httpClient.get("https://nominatim.openstreetmap.org/search?q=" + quaery + "&format=json");
  }
}
