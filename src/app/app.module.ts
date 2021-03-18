import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { AppComponent } from './app.component';
import { HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MatCardModule} from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import {MatGridListModule} from '@angular/material/grid-list';
import { QuestionaireComponent } from './questionaire/questionaire.component';
import {MatStepperModule} from '@angular/material/stepper';
import {MatSelectModule} from '@angular/material/select';
import { WelcomepageComponent } from './welcomepage/welcomepage.component';
import { StartlocationComponent } from './startlocation/startlocation.component';
import { MatIconModule } from '@angular/material/icon';
import {MatSlideToggleModule} from '@angular/material/slide-toggle';
import { MybarComponent } from './mybar/mybar.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTabsModule } from '@angular/material/tabs';
import { MatListModule } from '@angular/material/list';
import { MatSliderModule } from '@angular/material/slider';
import { TweetsComponent } from './tweets/tweets.component';

@NgModule({
  declarations: [
    AppComponent,
    QuestionaireComponent,
    WelcomepageComponent,
    StartlocationComponent,
    MybarComponent,
    TweetsComponent
  ],
  imports: [
    FormsModule,
    BrowserModule,
    HttpClientModule,
    MatDialogModule,
    MatSlideToggleModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatCheckboxModule,
    MatStepperModule,
    MatSelectModule,
    MatIconModule,
    MatSidenavModule,
    MatTabsModule,
    MatListModule,
    MatSliderModule,
    MatGridListModule
  ],
  exports: [MatFormFieldModule],
  providers: [],
  bootstrap: [AppComponent, QuestionaireComponent, StartlocationComponent],
  entryComponents: [QuestionaireComponent, StartlocationComponent]
})
export class AppModule { }

