import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { MainPageComponent } from './components/main-page/main-page.component';
import { AppComponent } from './app.component';
import { AgentListsComponent } from './components/agent-lists/agent-lists.component';
import { PingPongComponent } from './components/ping-pong/ping-pong.component';


const routes: Routes = [
  { path: "agents", component: AgentListsComponent },
  { path: "", component: MainPageComponent, pathMatch: "full" },
  { path: "pingpong", component: PingPongComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
