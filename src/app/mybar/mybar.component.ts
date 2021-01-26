import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {MatTabChangeEvent, MatTabsModule} from '@angular/material/tabs';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import {MatSliderModule} from '@angular/material/slider';
import { DataService } from '../data.service';
import { RouteService } from '../route.service';
import {Route} from '../route'
import { take } from 'rxjs/operators';

@Component({
  selector: 'app-mybar',
  templateUrl: './mybar.component.html',
  styleUrls: ['./mybar.component.css']
})
export class MybarComponent implements OnInit {
  @Input() transitTypeChild: any;
  @Output() selectedTab = new EventEmitter<number>();
  route: Route[]=[];
  ngOnInit(): void {
    this.routeService.getRouteInfo().pipe(take(1)).subscribe( data => {this.route = data; 
      //console.log(this.route)
    } )
    // this.newMessage();
    // this.sliderService.currentMessage.subscribe(mess=>console.log(mess));
    // this.sliderService.currentActive.subscribe(active => console.log(active));
  }

  tabChange(event:MatTabChangeEvent){
    //console.log(event);
    this.selectedTab.emit(event.index);
    //console.log('tab change successful: ', event.index)
  }

  // amentities: string[] = ['Covid-19 Assessment center', 'Hospital', 'Mall', 'Restaurants'];
  gridsize: number;
  active:boolean;

  updateSetting(event) {
    this.gridsize = event.value;
    this.active = true;
  }

  constructor(private sliderService:DataService, private routeService:RouteService) { }

  newMessage(){
    console.log(this.gridsize)
    this.sliderService.changeMessage(this.gridsize)
  }

  newActive(){
    this.sliderService.changeActive(this.active)
  }

}
