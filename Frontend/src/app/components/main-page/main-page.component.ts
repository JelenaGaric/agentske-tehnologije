import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { PredictService } from 'src/app/services/predict.service';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {

  predictForm: FormGroup;

  constructor(fb: FormBuilder, private service:PredictService) 
  {
      this.predictForm = fb.group({
        redgold: [""],
        bluegold: [""],
        redkills: [""],
        bluekills: [""],
        reddragons: [""],
        bluedragons: [""],
        redTopLaneStructures: [""],
        blueTopLaneStructures: [""],
        redMidLaneStructures: [""],
        blueMidLaneStructures: [""],
        redBotLaneStructures: [""],
        blueBotLaneStructures: [""]
      });
  }

  ngOnInit(): void {
  }

  clear() {

  }

  predict() {

  }

  onSubmit() {
    this.service.predict(this.predictForm.value)
  }

}
