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
    len: number;
    line: Array<string>;
    nstop: Array<number>;
    start: Array<string>;
    end: Array<string>;
    walk: Route[]=[];
    selectedIndex: number;
    response: any;
    visible: boolean = true;
    Object = Object;
    ngOnInit(): void {
        // console.log('im in')
        this.http.get('http://localhost:8080/api').pipe(take(1)).subscribe(
        data => {
            this.response = data;
        // console.log('im in' + this.response);
        },
        (error)=>{console.log(error)},
        ()=>{
            this.start, this.line, this.end = [],[],[]
            this.routeService.getTransitInfo().pipe(take(1)).subscribe( data => {
            this.response = data
            if(Object.keys(this.response).length ===0){
                this.visible = false;
                alert('No transit line available!');
            }else{
                for(var index = 0; index<Object.keys(this.response).length; index++){
                this.start = JSON.parse(this.response[index]['startstop']);
                this.end = JSON.parse(this.response[index]['endstop']);
                this.line = JSON.parse(this.response[index]['ttcname']);
                this.nstop = JSON.parse(this.response[index]['nstop']);
                this.len = this.start.length}
        }
        this.bus = data;
        // console.log('start stops'+this.start)
        // console.log('bus')
        console.log(this.bus)
        } )})
        
        this.http.get('http://localhost:8080/api').pipe(take(1)).subscribe(
        data => {
        this.response = data;
        // console.log('im in' + this.response);
        },
        (error)=>{console.log(error)},
        ()=>{
        this.routeService.getWalkingInfo().pipe(take(1)).subscribe( data => { 
        this.response = data; 
        for (var index = 0; index<Object.keys(this.response).length; index++){
        var risk = JSON.parse(this.response[index]['risk']);
        if (risk.includes(-1.0)){
        delete this.response[index]}
        } 
        this.walk = this.response;
        console.log(this.walk)
        })})
        
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