import { Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import {MatTabChangeEvent, MatTabsModule} from '@angular/material/tabs';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';
import {MatGridListModule} from '@angular/material/grid-list';
import {MatSliderModule} from '@angular/material/slider';
import { DataService } from '../data.service';
import { RouteService } from '../route.service';
import {Route} from '../route'
import {Transit} from '../transit'
import { concatMap, delay, take, timeout } from 'rxjs/operators';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import { of } from 'rxjs';

@Component({
  selector: 'app-mybar',
  templateUrl: './mybar.component.html',
  styleUrls: ['./mybar.component.css']
})

export class MybarComponent implements OnInit {
  @Input() transitTypeChild: any;
  @Output() selectedTab = new EventEmitter<number>();
  bus: Transit[]=[];
  walk: Route[]=[];
  selectedIndex: number;
  response: any;
  Object = Object;
  ngOnInit(): void {
    this.routeService.getTransitInfo().pipe(
      concatMap( item => of(item).pipe ( delay( 1000 ) ))
    ).subscribe( data => {
      this.bus = data; 
      console.log(this.bus)
    } )
    this.routeService.getWalkingInfo().pipe(
      concatMap( item => of(item).pipe ( delay( 1000 ) ))
    ).subscribe( data => { 
      this.response = data; 
      for (var index = 0; index<Object.keys(this.response).length; index++){
        var risk = JSON.parse(this.response[index]['risk']);
        if (risk.includes(-1.0)){
          delete this.response[index]}
      } 
      this.walk = this.response;
      console.log("the new walk is", this.walk)
    })
    // this.newMessage();
    // this.sliderService.currentMessage.subscribe(mess=>console.log(mess));
    // this.sliderService.currentActive.subscribe(active => console.log(active));
    this.getSelectedIndex();
  }

  getSelectedIndex(){
    this.http.get('http://localhost:8080/getTrans').pipe(take(1)).subscribe(
      (res)=>{
        this.response = res;
        console.log(this.response);
        if(this.response.includes('Walking')){
          this.selectedIndex = 0
        }else{
          this.selectedIndex = 1
        }
      }
    )
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

  constructor(private http: HttpClient, private sliderService:DataService, private routeService:RouteService) { }

  newMessage(){
    console.log(this.gridsize)
    this.sliderService.changeMessage(this.gridsize)
  }

  newActive(){
    this.sliderService.changeActive(this.active)
  }

}
