import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Tweets } from './tweet';

@Injectable({
  providedIn: 'root'
})
export class TwitterService {
  api_url = 'http://localhost:8080/tweets';

  constructor(private http: HttpClient) { }
  getTweets(): Observable<Tweets[]> {
    return this.http.get<Tweets[]>(this.api_url);
  }
}
