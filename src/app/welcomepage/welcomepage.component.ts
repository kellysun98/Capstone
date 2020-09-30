import { Component, OnInit } from '@angular/core';
import { MatDialogRef, MatDialog, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { QuestionaireComponent } from '../questionaire/questionaire.component';


// interface DialogData {
//   value: string;
//   viewValue: string;
// }

@Component({
  selector: 'app-welcomepage',
  templateUrl: './welcomepage.component.html',
  styleUrls: ['./welcomepage.component.css']
})
export class WelcomepageComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<WelcomepageComponent>, public dialog: MatDialog) { }

  ngOnInit(): void {
  }
  onNoClick(): void {
    this.dialogRef.close();
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(QuestionaireComponent, {
      width: '500px',
      height: '500px',
      data: {}
    });


  }

}
