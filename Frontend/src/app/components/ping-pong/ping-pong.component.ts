import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ACLMessageDTO } from 'src/app/model/ACLMessageDTO';
import { PingPongService } from 'src/app/services/ping-pong.service';
import { SocketService } from 'src/app/services/socket.service';
import { IPService } from 'src/app/services/IP.service';
import { Subject } from 'rxjs';
import { map } from 'rxjs/operators';
import { Message } from 'src/app/services/ws-adapter.service';

@Component({
  selector: 'app-ping-pong',
  templateUrl: './ping-pong.component.html',
  styleUrls: ['./ping-pong.component.css']
})
export class PingPongComponent implements OnInit {

  acl: ACLMessageDTO = new ACLMessageDTO();
  ipAdress: string;
  public messages: Subject<Message>;
  ACLMessages: ACLMessageDTO[] = [];
  ACLMessagesPing: ACLMessageDTO[] = [];
  ACLMessagesPong: ACLMessageDTO[] = [];

  pingField: string;
  pongField: string;
  msgForPing = new FormControl('');

  constructor(fb: FormBuilder, private service: PingPongService, private wsService: SocketService, private ipService: IPService) 
  {
    ipService.getIPAddress().subscribe((res:any)=>{  
      this.ipAdress = res.ip;

      this.messages = <Subject<Message>>wsService.connect("ws://172.16.117.120:8080/ChatAppWar/ws/" + this.ipAdress).pipe(map(
        (response: MessageEvent): Message => {
          return {
            message: response.data
          };
        }
      ));
        

        this.messages.subscribe(msg => {
          console.log("Response from websocket: " );
          
          this.ACLMessages.push(JSON.parse(msg.message))
        
          for(let acl of this.ACLMessages){
            console.log(acl)
            if(acl.sender != null)
              if(acl.sender.name === "ping"){
                this.pingField = "Ping message content:\n" + acl.content
                +"\nPerformative:\n" + acl.performative + "\nSender:\n"+acl.sender.name;
                if(acl.userArgs != null){
                  this.pingField = this.pingField + "\nUser arguments:\n" + JSON.stringify(acl.userArgs)
                }
              } else if (acl.sender.name === "pong"){
                this.pongField = "Pong message content:\n" + acl.content
                +"\nPerformative:\n" + acl.performative + "\nSender:\n"+acl.sender.name;
                if(acl.userArgs != null){
                  this.pongField = this.pongField + "\nUser arguments:\n" + JSON.stringify(acl.userArgs)
                }
              }
          }
        });

      });
  }

  ngOnInit(): void {
  }

  clear() {

  }


  onSubmit() {
    this.acl.content = this.msgForPing.value;
    this.acl.performative = "request";
    this.service.pingpong(this.acl).subscribe(data => {
    })
  }

}