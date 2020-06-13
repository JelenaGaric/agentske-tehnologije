import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-agent-lists',
  templateUrl: './agent-lists.component.html',
  styleUrls: ['./agent-lists.component.css']
})
export class AgentListsComponent implements OnInit {

  runningAgents: string[]= ["agent1", "agent2"]
  agentTypes: string[] = []
  selectedAgent :string
  selectedType :string

  constructor() { }

  ngOnInit(): void {
  }

  onSelectAgent(agent){
    this.selectedAgent=agent
  }
  onSelectType(type){
    this.selectedType=type
  }

}
