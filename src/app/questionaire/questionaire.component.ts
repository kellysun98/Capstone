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
import { StartlocationComponent } from '../startlocation/startlocation.component';
import { debounceTime, distinctUntilChanged, map, pairwise } from 'rxjs/operators';
import { combineLatest, forkJoin } from 'rxjs';

// interface DialogData {
//   value: string;
//   viewValue: string;
// }


@Component({
  selector: 'app-questionaire',
  templateUrl: './questionaire.component.html',
  styleUrls: ['./questionaire.component.css']
})
export class QuestionaireComponent implements OnInit {
  firstFormGroup: FormGroup;
  secondFormGroup: FormGroup;
  thirdFormGroup: FormGroup;
  df1: any = ["Walking"];
  df2: any = 'Less than 5 minutes';
  df3: any = ['I want to avoid hospital/covid-19 assessment center'];
  combined_forms: FormArray;

  autoCompleteValues = [];
  // selected = 'nolimit';
  // selected2= 'hospital';

  // Data: Array<any> = [
  //   { name: 'Walking', value: 'Walking' },
  //   { name: 'Public Transit', value: 'Public Transit' },
  //   { name: 'Biking', value: 'Biking' },
  // ];
  // transportations = new FormControl('', Validators.required);
  transportationList: string[] = ['Walking', 'Public Transit', 'Biking'];

  // periods = new FormControl('', Validators.required);
  periodList: string[] = ['Less than 5 minutes', 'Less than 10 minutes', 'Less than 15 minutes','No limit'];

  // safeties = new FormControl('', Validators.required);
  safetyList: string[] = ['I want to avoid hospital/covid-19 assessment center', 'I want to avoid public gathering places (e.g.: shopping malls)','I want to avoid over-crowded streets','I don\'t have a specific concern'];
  // dialog: any;
  

  constructor(
    public dialogRef: MatDialogRef<QuestionaireComponent>,
    public dialog: MatDialog,
    // @Inject(MAT_DIALOG_DATA) public data: DialogData, 
    private fb: FormBuilder) {}
    
  
  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
    this.firstFormGroup = this.fb.group({
      fristCtrl: ['', Validators.required],
      // firstArray: this.fb.array([])
    });
    this.secondFormGroup = this.fb.group({
      secondCtrl: ['', Validators.required]
    });
    this.thirdFormGroup = this.fb.group({
      thirdCtrl: ['', Validators.required]
    });



    // forkJoin([this.firstFormGroup.valueChanges, this.secondFormGroup.valueChanges, this.thirdFormGroup.valueChanges])
    // .pipe(
    //   debounceTime(400),
    //   distinctUntilChanged(),
      // map(data=>data.reduce((result, arr) => [...result, ...arr], []))
    // ).subscribe((data=>
    //   {
    //     // this.autoCompleteValues=data;
    //     console.log(data)
    //   }
    // ))

    // this.firstFormGroup.valueChanges.pipe(
    //   debounceTime(400),
    //   distinctUntilChanged()
    //   ).subscribe(
    //     (res)=>console.log(res)
    //   );
    
  }

  openNav(): void {
    const dialogRef = this.dialog.open(StartlocationComponent, {
      width: '500px',
      height: '500px',
      data: {}
    });

  }

  ngOnDestroy(){

  }

  combineVal(){
    console.log(11111);
    // forkJoin([this.firstFormGroup.valueChanges, this.secondFormGroup.valueChanges, this.thirdFormGroup.valueChanges])
    // .pipe(
    //   debounceTime(400),
    //   distinctUntilChanged(),
    //   // map(data=>data.reduce((result, arr) => [...result, ...arr], []))

    // ).subscribe((data)=>
    //   {
    //     console.log(data)
    //   })
    // this.combined_forms.push(this.firstFormGroup.value)
    // console.log('combined values: '+this.combined_forms)

    console.log(Object.values(this.firstFormGroup.value)[0]) //['Walking']
    console.log(Object.values(this.secondFormGroup.value)[0]) //'less than 5 minutes'
    console.log('3. '+ this.thirdFormGroup.value)   
  }

}  

