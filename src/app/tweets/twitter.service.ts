import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

declare let EventSource: any

@Injectable({
  providedIn: 'root'
})
export class TwitterService {
  api_url = 'http://localhost:8080/tweets';

  constructor(private http: HttpClient) { }
  getTweets(): Observable<any[]> {
    return this.http.get<any[]>(this.api_url);
  }

  getEventSource(url): EventSource{
    return new EventSource(url);
  }

}
