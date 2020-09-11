import { Component, OnInit } from '@angular/core';
import { AgentType } from 'src/app/model/agentType';
import { Router, ActivatedRoute } from '@angular/router';
import { AgentListService } from 'src/app/services/agent-list.service';
import { Agent } from  'src/app/model/agent';
import { AID } from 'src/app/model/AID';
import { AgentCenter } from 'src/app/model/agentCenter';


@Component({
  selector: 'app-agent-lists',
  templateUrl: './agent-lists.component.html',
  styleUrls: ['./agent-lists.component.css']
})
export class AgentListsComponent implements OnInit {

  agentType: AgentType
  agentTypes: AgentType[] = []

  running: Agent[] = []
  agents: Agent[] = []
  agentAID: AID
  name: string
  agentCenter: AgentCenter


  // runningAgents: string[]= ["agent1", "agent2"]
  // agentTypes: string[] = [tip1", "tip2"]
  // selectedAgent :string
  // selected type: string
  selectedAgent :Agent
  selectedType : AgentType

  newAgentType: string
  newAgentName: string



  constructor(private router: Router, private route: ActivatedRoute, private service: AgentListService) { 
    this.agentType = new AgentType();
    this.selectedType = new AgentType();
    this.selectedAgent = new Agent();
    this.agentAID = new AID();
    this.agentCenter = new AgentCenter();

  }

  ngOnInit(): void {

    this.service.getAgentType().subscribe(data => {
      this.agentTypes = data;
      console.log(data);
      this.service.getAgents().subscribe(data1 => {
        this.running = data1;
        console.log(data1);
        for(let a of this.running){
          a.isRunning = true;
        }
        this.service.getAllAgents().subscribe(data2 => {
          this.agents = data2;
          for(let a of this.agents){
            for(let ra of this.running) {
              if(a.id.name == ra.id.name) {
                a.isRunning = true;
              }
            }
          }

       })
      })
    });
    
  }

  onSelectAgent(agent){
    this.selectedAgent=agent
    console.log( 'name aid: ' + this.selectedAgent.id.name + ' host alias: ' + this.selectedAgent.id.host.alias + 'hpst address: ' + this.selectedAgent.id.host.address);
  }
  onSelectType(type){
    this.selectedType=type
  }

  stopAgent(idStop){
    this.service.stopAgent(idStop).subscribe( data => {
      window.location.reload();
      
    });

  }
  startAgent(type, idStart){
    this.service.startAgent(type, idStart).subscribe( data => {
      window.location.reload();
      
    });

  }

  selectNewType() {
    console.log(this.newAgentType);

  }

 

}
