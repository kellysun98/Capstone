import { Component, OnDestroy, OnInit } from '@angular/core';
import { TwitterService } from './twitter.service';
import { Tweets } from './tweet';

@Component({
  selector: 'app-tweets',
  templateUrl: './tweets.component.html',
  styleUrls: ['./tweets.component.css']
})
export class TweetsComponent implements OnInit,OnDestroy {

  constructor(private _twitterService: TwitterService) { }
  subscriptions: Array<any> = [];
  tweets: Array<any> = [];
  tweetList = new Map<String, String>();
  ngOnInit(): void {
    this.getIntroResourceList()
  }
  ngOnDestroy(): void {
    this.subscriptions.forEach(s => s.unsubscribe());
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
