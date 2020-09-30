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
import { QuestionaireComponent } from './questionaire/questionaire.component';
import {MatStepperModule} from '@angular/material/stepper';
import {MatSelectModule} from '@angular/material/select';
import { WelcomepageComponent } from './welcomepage/welcomepage.component';
import { StartlocationComponent } from './startlocation/startlocation.component';
import { MatIconModule } from '@angular/material/icon';

@NgModule({
  declarations: [
    AppComponent,
    QuestionaireComponent,
    WelcomepageComponent,
    StartlocationComponent
  ],
  imports: [
    FormsModule,
    BrowserModule,
    HttpClientModule,
    MatDialogModule,
    MatButtonModule,
    MatFormFieldModule,
    MatInputModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatCheckboxModule,
    MatStepperModule,
    MatSelectModule,
    MatIconModule
  ],
  exports: [MatFormFieldModule],
  providers: [],
  bootstrap: [AppComponent, QuestionaireComponent, StartlocationComponent],
  entryComponents: [QuestionaireComponent, StartlocationComponent]
})
export class AppModule { }

