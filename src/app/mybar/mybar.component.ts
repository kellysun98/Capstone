import { Component, Input, OnInit } from '@angular/core';
import {MatTabsModule} from '@angular/material/tabs';
import { MatSidenavModule } from '@angular/material/sidenav';
import { SideNavService } from '../side-nav.service';


@Component({
  selector: 'app-mybar',
  templateUrl: './mybar.component.html',
  styleUrls: ['./mybar.component.css']
})
export class MybarComponent implements OnInit {

  constructor(private sideNavService: SideNavService) { }
  back():void {
    this.sideNavService.toggle;
  }

  ngOnInit(): void {
  };

  

}
