import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {Route} from './route';
import {Transit} from './transit'

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  constructor(private http:HttpClient) { }

  getWalkingInfo(): Observable<Route[]>{
    return this.http.get<Route[]>('http://localhost:8080/walking')
  }

  getTransitInfo(): Observable<Transit[]>{
    return this.http.get<Transit[]>('http://localhost:8080/publictransit')
  }
}
