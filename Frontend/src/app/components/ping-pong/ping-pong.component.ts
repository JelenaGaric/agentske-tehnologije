import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { PingPongService } from 'src/app/services/pingpong-service';
import { ACLMessageDTO } from 'src/app/model/ACLMessagDTO';

@Component({
  selector: 'app-ping-pong',
  templateUrl: './ping-pong.component.html',
  styleUrls: ['./ping-pong.component.css']
})
export class PingPongComponent implements OnInit {

  acl: ACLMessageDTO = new ACLMessageDTO();

  pingPongForm = new FormGroup ({
    pingField: new FormControl(),
    pongField:  new FormControl()
  });

  constructor(fb: FormBuilder, private service: PingPongService) 
  {
  }

  ngOnInit(): void {
  }

  clear() {

  }


  onSubmit() {
    this.acl.content = "a";
    this.acl.performative = "request";
    this.service.pingpong(this.acl).subscribe(data => {
      alert("");
    })
  }

}
