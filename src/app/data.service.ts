import { Injectable } from '@angular/core';
import { BehaviorSubject, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  private messageSource = new BehaviorSubject(0);
  private activeBoolean = new BehaviorSubject<boolean>(false);

  
  currentMessage = this.messageSource.asObservable();
  currentActive = this.activeBoolean.asObservable();
  
  constructor() { }

  changeMessage(message: number) {
    this.messageSource.next(message)
  }
  changeActive(active:boolean){
    this.activeBoolean.next(active)
  }
}
