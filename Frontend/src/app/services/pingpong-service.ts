import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { ACLMessageDTO } from '../model/ACLMessagDTO';

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
    return this.http.post('http://localhost:8080/ChatAppWar/rest/messages/pingpong', acl)
  }

}
