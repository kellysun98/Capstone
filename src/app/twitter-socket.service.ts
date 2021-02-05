import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import * as io from 'socket.io-client';

@Injectable({
  providedIn: 'root'
})
export class TwitterSocketService {
  private socket: SocketIOClient.Socket

  constructor(private http:HttpClient) {
    this.socket = io('http://localhost:5000')
   }

  public initSocket(){
    this.http.get('http://localhost:5000/transit').subscribe(
      data => {
        console.log(data)
      }
    )
  }  
}
