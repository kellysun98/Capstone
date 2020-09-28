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
  selectedValue: string;
  Walking = false;
  PublicTransit = false;
  Biking = false;
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;
  selected = 'nolimit';
  selected2= 'hospital';

  Data: Array<any> = [
    { name: 'Walking', value: 'Walking' },
    { name: 'Public Transit', value: 'Public Transit' },
    { name: 'Biking', value: 'Biking' },
  ];

  constructor(
    public dialogRef: MatDialogRef<QuestionaireComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData, 
    private fb: FormBuilder) {}

    foods: DialogData[] = [
      {value: '5mins', viewValue: 'Less than 5 minutes'},
      {value: '10mins', viewValue: 'Less than 10 minutes'},
      {value: '15mins', viewValue: 'Less than 15 minutes'},
      {value: 'nolimit', viewValue: 'No limit'}
    ];
  

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.firstFormGroup = this.fb.group({
      firstCtrl: ['', Validators.required]
    });
    this.secondFormGroup = this.fb.group({
      secondCtrl: ['', Validators.required]
    });
    this.thirdFormGroup = this.fb.group({
      secondCtrl: ['', Validators.required]
    });
  }

}
