import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { MybarComponent } from './mybar.component';

describe('MybarComponent', () => {
  let component: MybarComponent;
  let fixture: ComponentFixture<MybarComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ MybarComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(MybarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
