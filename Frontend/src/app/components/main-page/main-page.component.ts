import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, FormControl } from '@angular/forms';
import { PredictService } from 'src/app/services/predict.service';
import { predictDTO } from 'src/app/model/predictDTO';

@Component({
  selector: 'app-main-page',
  templateUrl: './main-page.component.html',
  styleUrls: ['./main-page.component.css']
})
export class MainPageComponent implements OnInit {
  predict: predictDTO = new predictDTO();

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

  constructor(fb: FormBuilder, private service:PredictService) 
  {
  }

  ngOnInit(): void {
  }

  clear() {

  }


  onSubmit() {
    this.predict = this.predictForm.value;  

    this.service.predict(this.predict).subscribe(data => {
      alert("predict result");
    })
  }

}
