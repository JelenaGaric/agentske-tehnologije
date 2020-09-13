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
        return this.http.get<AgentType[]>('http://172.16.117.120:8080/ChatAppWar/rest/agents/classes');
      }

      public getAgents(): Observable<Agent[]> {
        return this.http.get<Agent[]>('http://172.16.117.120:8080/ChatAppWar/rest/agents/running');
      }

      public getAllAgents(): Observable<Agent[]> {
        return this.http.get<Agent[]>('http://172.16.117.120:8080/ChatAppWar/rest/agents');
      }

      public stopAgent(aid: string) {
        return this.http.delete("http://172.16.117.120:8080/ChatAppWar/rest/agents/running/" + aid);
      }
      public startAgent(type: string, aid: string) {
        return this.http.put("http://172.16.117.120:8080/ChatAppWar/rest/agents/running/" + type + "/" + aid, null);
      }

}