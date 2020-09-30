import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
@Component({
  selector: 'app-startlocation',
  templateUrl: './startlocation.component.html',
  styleUrls: ['./startlocation.component.css']
})
export class StartlocationComponent implements OnInit {

  constructor(public dialogRef: MatDialogRef<StartlocationComponent>, public dialog: MatDialog) { }

  ngOnInit(): void {
  }

}
