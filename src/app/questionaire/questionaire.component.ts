import { Component, OnInit, Inject } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import {MatTableDataSource} from '@angular/material/table';

interface DialogData {
  email: string;
}

@Component({
  selector: 'app-questionaire',
  templateUrl: './questionaire.component.html',
  styleUrls: ['./questionaire.component.css']
})
export class QuestionaireComponent implements OnInit {

  constructor(
    public dialogRef: MatDialogRef<QuestionaireComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) {}

  onNoClick(): void {
    this.dialogRef.close();
  }

  ngOnInit() {
  }

}
