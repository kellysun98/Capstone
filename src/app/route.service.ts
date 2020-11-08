import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import {Route} from './route';

@Injectable({
  providedIn: 'root'
})
export class RouteService {

  constructor(private http:HttpClient) { }

  getRouteInfo(): Observable<Route[]>{
    return this.http.get<Route[]>('http://localhost:8080/api')
  }
}
