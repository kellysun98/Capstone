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
    // this.newMessage();
    //this.sliderService.currentMessage.subscribe(mess=>console.log(mess));
    this.sliderService.currentActive.subscribe(active => console.log(active));
  }
  
  amentities: string[] = ['Covid-19 Assessment center', 'Hospital', 'Mall', 'Restaurants'];
  gridsize: number;
  active:boolean;

  updateSetting(event) {
    this.gridsize = event.value;
    this.active = true;
  }

  constructor(private sliderService:DataService) { }

  newMessage(){
    console.log(this.gridsize)
    this.sliderService.changeMessage(this.gridsize)
  }

  newActive(){
    this.sliderService.changeActive(this.active)
  }

}
