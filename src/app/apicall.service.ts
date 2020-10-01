import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { AppComponent } from './app.component';
import { map, catchError } from 'rxjs/operators';
import { throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApicallService {

  constructor(private httpClient: HttpClient) { }
  getRouteList(){
    // let params = new HttpParams().set('longitude', AppComponent.longitude.toString()).set('latitude', AppComponent.latitude.toString()).set('end_long',AppComponent.end_long.toString()).set('end_lat', AppComponent.end_lat.toString())
    // return this.httpClient.get("http://localhost:8080/api", {params:params}).pipe(
    //   map((data:Coordinates[])=>{
    //     return data
    //   }), catchError((error)=>{
    //     return throwError('something went wrong!')
    //   })
    // )

  }
}
