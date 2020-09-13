import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ACLMessageDTO } from '../model/ACLMessageDTO';

@Injectable({
  providedIn: 'root'
})
export class PingPongService {

  httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(private http: HttpClient) { }

  pingpong(acl: ACLMessageDTO) {
    return this.http.post('http://172.16.117.120:8080/ChatAppWar/rest/messages/pingpong', acl)
  }

}