import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { PredictService } from 'src/app/services/predict.service';
import { predictDTO } from 'src/app/model/predictDTO';
import { WsAdapterService, Message } from 'src/app/services/ws-adapter.service';
import { Subject } from 'rxjs';
import { AppComponent } from 'src/app/app.component';
import { IPService } from 'src/app/services/IP.service';
import { SocketService } from 'src/app/services/socket.service';
import { map } from 'rxjs/operators';
import { ResultDTO } from 'src/app/model/resultDTO';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
  predict: predictDTO = new predictDTO();
  public messages: Subject<Message>;
  public ipAdress: string;
  result: ResultDTO = new ResultDTO();

  // predictForm: FormGroup;
  predictForm = new FormGroup ({
    redGold: new FormControl(''),
    blueGold:  new FormControl(''),
    redKills:  new FormControl(''),
    blueKills:  new FormControl(''),
    redDragons:  new FormControl(''),
    blueDragons:  new FormControl(''),
    redTopLaneStructures: new FormControl(''),
    blueTopLaneStructures: new FormControl(''),
    redMidLaneStructures:  new FormControl(''),
    blueMidLaneStructures: new FormControl(''),
    redBotLaneStructures: new FormControl(''),
    blueBotLaneStructures: new FormControl('')
  });

  constructor(fb: FormBuilder, private service:PredictService, private wsService: SocketService,private ws: WsAdapterService, ipService: IPService) 
  {
    ipService.getIPAddress().subscribe((res:any)=>{  
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
          this.result = JSON.parse(msg.message);
          console.log(this.result);

        });

      /*ws.messages.subscribe(msg => {
        console.log("Response from websocket: " + msg);
      });*/

    });  
    
  }

  ngOnInit(): void {

  }

  clear() {

  }
  

  onSubmit() {
    this.predict = this.predictForm.value;  

    this.service.predict(this.predict).subscribe(data => {
     
    })
  }

}
