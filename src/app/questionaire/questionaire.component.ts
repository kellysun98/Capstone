import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { FormsModule, ReactiveFormsModule } from "@angular/forms";
import { MatCardModule} from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { FormBuilder, FormGroup, FormArray, FormControl, Validators } from '@angular/forms';


interface DialogData {
  value: string;
  viewValue: string;
}


@Component({
  selector: 'app-questionaire',
  templateUrl: './questionaire.component.html',
  styleUrls: ['./questionaire.component.css']
})
export class QuestionaireComponent implements OnInit {
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;
  // selected = 'nolimit';
  // selected2= 'hospital';

  // Data: Array<any> = [
  //   { name: 'Walking', value: 'Walking' },
  //   { name: 'Public Transit', value: 'Public Transit' },
  //   { name: 'Biking', value: 'Biking' },
  // ];
  transportations = new FormControl('', Validators.required);
  transportationList: string[] = ['Walking', 'Public Transit', 'Biking'];

  periods = new FormControl('', Validators.required);
  periodList: string[] = ['Less than 5 minutes', 'Less than 10 minutes', 'Less than 15 minutes','No limit'];

  safeties = new FormControl('', Validators.required);
  safetyList: string[] = ['I want to avoid hospital/covid-19 assessment center', 'I want to avoid public gathering places (e.g.: shopping malls)','I want to avoid over-crowded streets','I don\'t have a specific concern'];

  constructor(
    public dialogRef: MatDialogRef<QuestionaireComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData, 
    private fb: FormBuilder) {}
  
  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.firstFormGroup = this.fb.group({
      fristCtrl: ['Default Value', Validators.required]
    });
    this.secondFormGroup = this.fb.group({
      secondCtrl: ['Default Value', Validators.required]
    });
    this.thirdFormGroup = this.fb.group({
      thirdCtrl: ['Default Value', Validators.required]
    });
  }

}
