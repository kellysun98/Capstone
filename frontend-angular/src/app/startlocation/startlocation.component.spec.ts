import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { StartlocationComponent } from './startlocation.component';

describe('StartlocationComponent', () => {
  let component: StartlocationComponent;
  let fixture: ComponentFixture<StartlocationComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ StartlocationComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(StartlocationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
