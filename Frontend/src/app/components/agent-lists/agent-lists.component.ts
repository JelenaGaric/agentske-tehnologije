import { Component, OnInit } from '@angular/core';
import { AgentType } from 'src/app/model/agentType';
import { Router, ActivatedRoute } from '@angular/router';
import { AgentListService } from 'src/app/services/agent-list.service';
import { Agent } from  'src/app/model/agent';


@Component({
  selector: 'app-agent-lists',
  templateUrl: './agent-lists.component.html',
  styleUrls: ['./agent-lists.component.css']
})
export class AgentListsComponent implements OnInit {

  agentType: AgentType
  agentTypes: AgentType[] = []

  running: Agent[] = []


  // runningAgents: string[]= ["agent1", "agent2"]
  // agentTypes: string[] = [tip1", "tip2"]
  // selectedAgent :string
  // selected type: string
  selectedAgent :Agent
  selectedType : AgentType

  constructor(private router: Router, private route: ActivatedRoute, private service: AgentListService) { 
    this.agentType = new AgentType();
    this.selectedType = new AgentType();
    this.selectedAgent = new Agent();
  }

  ngOnInit(): void {
    this.service.getAgentType().subscribe(data => {
      this.agentTypes = data;

      this.service.getAgents().subscribe(data1 => {
        this.running = data1;
      })
    });
    
  }

  onSelectAgent(agent){
    this.selectedAgent=agent
  }
  onSelectType(type){
    this.selectedType=type
  }

 

}
