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
  email: string;

}

@Component({
  selector: 'app-questionaire',
  templateUrl: './questionaire.component.html',
  styleUrls: ['./questionaire.component.css']
})
export class QuestionaireComponent implements OnInit {
  form: FormGroup;
  Data: Array<any> = [
    { name: 'Walking', value: 'Walking' },
    { name: 'Public Transit', value: 'Public Transit' },
    { name: 'Biking', value: 'Biking' },
  ];

  constructor(
    public dialogRef: MatDialogRef<QuestionaireComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData, 
    private fb: FormBuilder) 
    {
      this.form = this.fb.group({
        checkArray: this.fb.array([])
    })
  }

  onNoClick(): void {
    this.dialogRef.close();
  }

  onCheckboxChange(e) {
    const checkArray: FormArray = this.form.get('checkArray') as FormArray;
  
    if (e.target.checked) {
      checkArray.push(new FormControl(e.target.value));
    } else {
      let i: number = 0;
      checkArray.controls.forEach((item: FormControl) => {
        if (item.value == e.target.value) {
          checkArray.removeAt(i);
          return;
        }
        i++;
      });
    }
  }

  submitForm(){
    console.log(this.form.value)
  }

  ngOnInit() {
  }

}
