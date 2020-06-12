import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AgentListsComponent } from './agent-lists.component';

describe('AgentListsComponent', () => {
  let component: AgentListsComponent;
  let fixture: ComponentFixture<AgentListsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AgentListsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AgentListsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
