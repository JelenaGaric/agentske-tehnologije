import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { predictDTO } from '../model/predictDTO';

@Injectable({
  providedIn: 'root'
})
export class PredictService {

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };
  constructor(private http: HttpClient) { }

  predict(info: predictDTO) {
    this.http.post<predictDTO>("http://localhost:8080/ChatAppWar/master/predict", info, this.httpOptions)
  }


}
