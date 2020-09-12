import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { predictDTO } from '../model/predictDTO';

@Injectable({
  providedIn: 'root'
})
export class PredictService {

  /*httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };*/

  httpOptions = {
    headers: new HttpHeaders()
    .set('content-type', 'application/json')
  .set('Access-Control-Allow-Origin', '*'),
  }

  constructor(private http: HttpClient) { }

  predict(info: predictDTO) {
    console.log(JSON.stringify(info))
    return this.http.post<predictDTO>('http://localhost:8080/ChatAppWar/rest/messages', info)
    // alert("CAO");
  }


}
