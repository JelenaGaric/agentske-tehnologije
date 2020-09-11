import { Injectable } from '@angular/core';
import { Observable, Subject } from "rxjs";
import { map } from "rxjs/operators";
import { SocketService } from './socket.service';
import { IPService } from './IP.service';


export interface Message {
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class WsAdapterService {
  public ipAdress: string;
  public messages: Subject<Message>;

  public urlString:string = "ws://localhost:8080/ChatAppWar/ws/";

  constructor(wsService: SocketService) {
     console.log("MY URL " + this.urlString)
    
    /*this.messages = <Subject<Message>>wsService.connect(this.urlString + this.ipAdress).pipe(map(
      (response: MessageEvent): Message => {
        let data = JSON.parse(response.data);
        return {
          author: data.author,
          message: data.message
        };
      }
    ));*/
  }
}
