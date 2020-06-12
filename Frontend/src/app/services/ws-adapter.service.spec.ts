import { TestBed } from '@angular/core/testing';

import { WsAdapterService } from './ws-adapter.service';

describe('WsAdapterService', () => {
  let service: WsAdapterService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(WsAdapterService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
