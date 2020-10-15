import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {MatTabsModule} from '@angular/material/tabs';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import {MatSliderModule} from '@angular/material/slider';
import { DataService } from '../data.service';

@Component({
  selector: 'app-mybar',
  templateUrl: './mybar.component.html',
  styleUrls: ['./mybar.component.css']
})
export class MybarComponent implements OnInit {
  ngOnInit(): void {
  }
  
  amentities: string[] = ['Covid-19 Assessment center', 'Hospital', 'Mall', 'Restaurants'];
  gridsize: number;
  // message = 'I am here!'
  // @Output() sendSliderValue = new EventEmitter<number>();
  updateSetting(event) {
    this.gridsize = event.value;
    // this.sendSliderValue.emit(event.value);
    console.log(this.gridsize);
  }

  // myCallbackFunction = (): void=>{
  //   console.log(this.gridsize)
  // }
  constructor(private sliderService:DataService) { }

  newMessage(){
    this.sliderService.changeMessage(this.gridsize)
  }

}
