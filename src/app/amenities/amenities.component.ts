import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-amenities',
  templateUrl: './amenities.component.html',
  styleUrls: ['./amenities.component.css']
})
export class AmenitiesComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }
  
  checked = false;

  amentities: string[] = ['Covid-19 Assessment center', 'Pharmacies Offering COVID-19 Testing', 'Mall'];

  assessmentCentre: string[] = ['2 Janda Court Etobicoke', '200 Church Street North York', 
  '825 Coxwell Avenue Toronto', '600 University Avenue Toronto', '555 Finch Avenue West Toronto', '3030 Birchmount Road Scarborough', '2867 Ellesmere Road Scarborough', 
  '3050 Lawrence Ave E Scarborough', '2075 Bayview Avenue Toronto', '347 Bathurst Street Toronto', '30 The Queensway Toronto', '38 Shuter Street Toronto', 
  '76 Grenville Street Toronto', '4 The Market Place East York', '45 Overlea Boulevard Toronto', '22 Vaughan Road Toronto']

  pharmacies: string[] = ['Shoppers Drug Mart, 1630 Danforth Avenue',
  'Shoppers Drug Mart, 1601 Bayview Avenue',
  'Shoppers Drug Mart, 1027 Yonge Street',
  'Shoppers Drug Mart, 3446 Dundas Street West',
  'Shoppers Drug Mart, 1400 Dupont Street',
  'Shoppers Drug Mart, 360A Bloor Street West',
  'Shoppers Drug Mart, 123 Rexdale Boulevard',
  'Shoppers Drug Mart, 900 Albion Road',
  'Shoppers Drug Mart, 4841 Yonge Street',
  'Shoppers Drug Mart, 5095 Yonge Street',
  'Shoppers Drug Mart, 3874 Bathurst Street',
  'Shoppers Drug Mart, 2550 Finch Avenue West',
  'Shoppers Drug Mart, 2751 Eglinton Avenue East',
  'Shoppers Drug Mart, 629 Markham Road',
  'Shoppers Drug Mart, 2301 Kingston Road',
  'Shoppers Drug Mart, Unit A-1780 Markham Road',
  'Medicine Shoppe, 2600 Eglinton Avenue West',
  'Village Square Pharmacy, 2942 Finch Avenue East',
  'Rexall, 4459 Kingston Road',
  'Rexall, 250 Wincott Drive',
  'Rexall, 901 Eglinton Avenue West']

  malls: string[] = [
    'Fairview Mall',
    'Scarborough Town Centre',
    'Sherway Gardens',
    'Toronto Eaton Centre',
    'Yorkdale Shopping Centre'
  ]
}
