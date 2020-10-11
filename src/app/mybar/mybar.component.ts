import { Component, Input, OnInit } from '@angular/core';
import {MatTabsModule} from '@angular/material/tabs';
import { MatSidenavModule, MatSidenav } from '@angular/material/sidenav';


@Component({
  selector: 'app-mybar',
  templateUrl: './mybar.component.html',
  styleUrls: ['./mybar.component.css']
})
export class MybarComponent implements OnInit {
  
  amentities: string[] = ['Covid-19 Assessment center', 'Hospital', 'Mall', 'Restaurants'];
  @Input() inputSideNav: MatSidenav;

  constructor() { }
  ngOnInit(): void {
  };
}
