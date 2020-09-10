import {Injectable} from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import{Observable} from 'rxjs';
import { AgentType } from '../model/agentType';
import { Agent } from  'src/app/model/agent';

@Injectable({
    providedIn: 'root'
  })
export class AgentListService {

    httpOptions = {
        headers: new HttpHeaders({
          'Content-Type': 'application/json'
        })
      };

    constructor(private http: HttpClient) { 
      console.log( JSON.stringify( this.getAgentType ) );
    }

      public getAgentType(): Observable<AgentType[]> {
        //stavila sam svoju sad za sad da vidim radi li
        return this.http.get<AgentType[]>('192.168.0.108:8080/ChatAppWar/master/agents/classes', this.httpOptions);
      }

      public getAgents(): Observable<Agent[]> {
        return this.http.get<Agent[]>('http://localhost:8080/ChatAppWar/rest/agents/running', this.httpOptions);
      }

}