import { Component, NgZone, OnDestroy, OnInit } from '@angular/core';
import { Observable } from 'rxjs';
import { TwitterService } from './twitter.service';

declare let EventSource: any

@Component({
  selector: 'app-tweets',
  templateUrl: './tweets.component.html',
  styleUrls: ['./tweets.component.css']
})
export class TweetsComponent implements OnInit,OnDestroy {

  constructor(private _twitterService: TwitterService, private _zone: NgZone) { }
  subscriptions: Array<any> = [];
  tweets: Array<any> = [];
  tweetList = new Map<String, String>();

  ngOnInit(): void {
    this.getTweetStreaming()
  }
  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
  }

  getServerSentEvent(): Observable<any>{
    return Observable.create(
      (observer)=>{
        const eventSource = this._twitterService.getEventSource('http://localhost:8080/tweets');

        eventSource.onmessage = event =>{
          this._zone.run(
            ()=>{
              observer.next(event);
            });
          };
        eventSource.onerror = error => {
          this._zone.run(()=>{
            observer.error(error);
          });
        };
      });
  }

  getTweetStreaming(){
    this.getServerSentEvent().subscribe(
      (response: any) => {
        this.tweets = response;
        console.log(this.tweets)
        for (let key of Object.keys(this.tweets)){
          this.tweetList.set(key,this.tweets[key]);
          console.log(key);
          console.log(this.tweets[key]);
        }
       
    }
    )
  }


  getIntroResourceList(): void {
    if(this.tweets.length){
      this.tweets = [];
    }
    this.subscriptions.push(this._twitterService.getTweets().subscribe(
      (response: any) => {
          this.tweets = response;
          for (let key of Object.keys(this.tweets)){
            this.tweetList.set(key,this.tweets[key]);
            console.log(key);
            console.log(this.tweets[key]);
          }
         
      }
  ));
}



}
