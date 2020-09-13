import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { predictDTO } from '../model/predictDTO';
import { IPService } from './IP.service';

@Injectable({
  providedIn: 'root'
})
export class PredictService {

  /*httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };*/

  public ipAdress: string;

  httpOptions = {
    headers: new HttpHeaders()
    .set('content-type', 'application/json')
  }

  constructor(private http: HttpClient, private ipService: IPService) { }

  predict(info: predictDTO) {
    // this.ipService.getIPAddress().subscribe((res:any)=>{  
    //   this.ipAdress = res.ip;
    //   console.log(this.ipAdress)
    //   console.log(JSON.stringify(info))
    //   return this.http.post<predictDTO>('http://'+this.ipAdress+':8080/ChatAppWar/rest/messages', info)
    // });
   
    return this.http.post<predictDTO>('http://192.168.0.103:8080/ChatAppWar/rest/messages', info)
  }


}
