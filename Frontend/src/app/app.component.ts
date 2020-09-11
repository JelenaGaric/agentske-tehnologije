import { Component } from '@angular/core';
import { WsAdapterService } from './services/ws-adapter.service';
import { IPService } from './services/IP.service';
import { SocketService } from './services/socket.service';
import { map } from 'rxjs/operators';
import { Subject } from 'rxjs';

export interface Message {
  message: string;
}

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'Frontend';
  ipAdress: string;
  public messages: Subject<Message>;

  
  constructor(private wsService: SocketService,private ws: WsAdapterService, ipService: IPService) {
        
   /* ipService.getIPAddress().subscribe((res:any)=>{  
      this.ipAdress = res.ip;
      console.log(this.ipAdress)

      ws.ipAdress =  this.ipAdress;
      console.log("WS IP: " + ws.ipAdress)

      this.messages = <Subject<Message>>wsService.connect("ws://localhost:8080/ChatAppWar/ws/" + this.ipAdress).pipe(map(
        (response: MessageEvent): Message => {
          console.log("DATA")
          console.log(response.data)
          //let data = JSON.parse(response.data);
          return {
            message: response.data
          };
        }
      ));
        

        this.messages.subscribe(msg => {
          console.log("Response from websocket: " );
          console.log(msg);
        });

      ws.messages.subscribe(msg => {
        console.log("Response from websocket: " + msg);
      });

    });  */
    
   
  }

  


  private message = {
    author: "tutorialedge",
    message: "this is a test message"
  };

  sendMsg() {
    console.log("new message from client to websocket: ", this.message);
    this.messages.next(this.message);
    this.message.message = "";
  }

}
