import { Component, ErrorHandler, ViewChild } from '@angular/core';
import Map from 'ol/Map';
import View from 'ol/View';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import OSM from 'ol/source/OSM';
import * as olProj from 'ol/proj';
import TileLayer from 'ol/layer/Tile';
import { HttpClient, HttpErrorResponse, HttpParams } from '@angular/common/http';
import {MatDialogModule} from '@angular/material/dialog';
import { QuestionaireComponent } from './questionaire/questionaire.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatCardModule} from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { WelcomepageComponent } from './welcomepage/welcomepage.component';
import { ThrowStmt } from '@angular/compiler';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Options } from './options';
import { Route } from './route';
import { Coordinates} from './coordinates';
import GeoJSON from 'ol/format/GeoJSON';
import {MatSidenav, MatSidenavModule} from '@angular/material/sidenav';
import { SideNavService } from './side-nav.service';


declare var ol: any;
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  @ViewChild('sidenav') public sidenav: MatSidenav;
  open: boolean;
  heatmap: Object;
  latitude: number = 43.653871;
  longitude: number = -79.3728709;
  // tslint:disable-next-line:variable-name
  end_lat: number = 43.6656433;
  // tslint:disable-next-line:variable-name
  end_long: number = -79.3913338;
  map: any;
  response: Object;
  email: string;
  testArray: any[];
  option:Options[];
  route: Route[];
  coor: Coordinates[];
  


  constructor(private http: HttpClient, public dialog: MatDialog, public sideNavService: SideNavService){
  }
    ngOnInit() {
      this.sideNavService.sideNavToggleSubject.subscribe(()=> 
      {
        this.sidenav.toggle();
      });
      this.openWelcome();
      this.Heatmap();
   

    //this.getAllCoords();
    var mousePositionControl = new ol.control.MousePosition({
      coordinateFormat: ol.coordinate.createStringXY(4),
      projection: 'EPSG:4326',
      // comment the following two lines to have the mouse position
      // be placed within the map.
      className: 'custom-mouse-position',
      target: document.getElementById('mouse-position'),
      undefinedHTML: '&nbsp;'
    });


    this.map = new ol.Map({
      target: 'map',
      controls: ol.control.defaults({
        attributionOptions: {
          collapsible: false
        }
      }).extend([mousePositionControl]),
      layers: [
        new ol.layer.Tile({
          source: new ol.source.OSM()
        })
      ],
      view: new ol.View({
        center: ol.proj.fromLonLat([-79.3883, 43.6548]),
        zoom: 10
      })
    });

    this.map.on('click', function (args) {
      console.log(args.coordinate);
      var lonlat = ol.proj.transform(args.coordinate, 'EPSG:3857', 'EPSG:4326');
      console.log(lonlat);
      var lon = lonlat[0];
      var lat = lonlat[1];
      alert(`lat: ${lat} long: ${lon}`);
    });
    console.log(this.map.getLayers());

  }

  // openDialog(): void {
  //   const dialogRef = this.dialog.open(QuestionaireComponent, {
  //     width: '500px',
  //     height: '500px',
  //     data: {}
  //   });

  //   dialogRef.afterClosed().subscribe(result => {
  //     this.email = result;
  //   });
  // }

  openWelcome(): void{
    const dialogRef = this.dialog.open(WelcomepageComponent, {
      width: '500px',
      height: '500px',
      data: {}
    });
  }

  setCenter() {
    var view = this.map.getView();
    view.setCenter(ol.proj.fromLonLat([this.longitude, this.latitude]));
    view.setZoom(10);
  }
//return observable
  getAllCoords(){
    let params = new HttpParams().set('longitude', this.longitude.toString()).set('latitude', this.latitude.toString()).set('end_long',this.end_long.toString()).set('end_lat', this.end_lat.toString())
    return this.http.get('http://localhost:8080/api', {params:params})
  }

  setMarker(){
    // console.log(this.longitude.toString());
    // let params = new HttpParams().set('longitude', this.longitude.toString()).set('latitude', this.latitude.toString()).set('end_long',this.end_long.toString()).set('end_lat', this.end_lat.toString())
    // this.http.get("http://localhost:8080/api", {params:params})
    // .subscribe((response) => {this.response})

    var Markers = [{lat: this.latitude, lng: this.longitude}, {lat:this.end_lat, lng: this.end_long}];
    var features = [];
    for (var i = 0; i < Markers.length; i++) {
      var item = Markers[i];
      var longitude = item.lng;
      var latitude = item.lat;
  
      var iconFeature = new ol.Feature({
          geometry: new ol.geom.Point(ol.proj.transform([longitude, latitude], 'EPSG:4326', 'EPSG:3857'))
      });    
      
      var iconStyle = new ol.style.Style({
          image: new ol.style.Icon(({
              anchor: [0.5, 1],
              src: "http://cdn.mapmarker.io/api/v1/pin?text=P&size=50&hoffset=1"
          }))
      });
    
      iconFeature.setStyle(iconStyle);
      features.push(iconFeature);

      //features.push(featureLine);
  
    }
  
    var vectorSource = new ol.source.Vector({
      features: features
    });
  
    // tslint:disable-next-line:prefer-const
    var vectorLayer = new ol.layer.Vector({
      source: vectorSource
    });
    this.map.addLayer(vectorLayer);

    
  }

drawLine2(){
  this.getAllCoords()
  //  .pipe(map(response=>JSON.parse))
  .subscribe(
    (res)=>{ console.log(res), 
      this.response = res; 
      for (let key of Object.keys(this.response)){
        var route = this.response[key];
        route = '[' + route + ']';
        try{
          route = JSON.parse(route)
        }catch{
          console.log(route.length);
          continue;
        }
        for (var i = 0; i < route.length; i++) {
          console.log('length enumeration '+i);
          route[i] = ol.proj.transform(route[i], 'EPSG:4326', 'EPSG:3857');
        }
        var featureLine = new ol.Feature({
          geometry: new ol.geom.LineString(route)
        });
        var vectorLine = new ol.source.Vector({});
        vectorLine.addFeature(featureLine);
      
        var vectorLineLayer = new ol.layer.Vector({
            source: vectorLine,
            style: new ol.style.Style({
                fill: new ol.style.Fill({ color: '#000000', weight: 10 }),
                stroke: new ol.style.Stroke({ color: '#000000', width: 10 })
            })
        });
          this.map.addLayer(vectorLineLayer);

      };},
      // this.testArray.concat(Array((this.response))),
      // console.log('test: '+this.response)},
    (err)=>console.error(err),
    ()=>console.log(this.response + 'Proces Complete!')
  );
  //console.log(points)
  // var points = this.testArray[0];
  // for (var i =0; i<this.testArray.length; i++) {
  //   console.log('I\'m here'+ i);
  //   console.log(this.testArray[i])
  // }




}

  Heatmap(){
    var style1 = new ol.style.Style({
      fill: new ol.style.Fill({ color: 'rgba(128,0,128,0.3)'}),
      stroke: new ol.style.Stroke({ color: 'rgba(0,0,0,0.8)', width: 0.5 })
    });
    var vectorLine = new ol.source.Vector({});

    this.http.get("http://localhost:8080/heatmap")
    .subscribe((heatmap) => {
      this.heatmap = heatmap;
      for (let key of Object.keys(this.heatmap)){
    
        var test = this.heatmap[key];
    test = test.replace(/\,-79/g, '),(-79').replace(/\(/g, '[').replace(/\)/g, ']');
      test = '['+test+']';
      try{ test = JSON.parse(test) }
    // catch { console.log(key);
      catch{continue;}
      var poly= new ol.geom.Polygon([test]);
      poly.transform('EPSG:4326', 'EPSG:3857');       
      var featureLine = new ol.Feature({
          geometry: poly,
          });
    featureLine.setStyle(style1);
    vectorLine.addFeature(featureLine);
  
  }
    var vectorLineLayer = new ol.layer.Vector({
      source: vectorLine,
    });
    this.map.addLayer(vectorLineLayer); 

});
}

noHeatmap(){
  console.log(this.map.getLayers());
  //this.map.removeLayer(vectorLineLayer);
}

  hideit(){
    var x = document.getElementById("willhide");
    if (x.style.display === "none") {
      x.style.display = "block";
    } else {
      x.style.display = "none";
    }
  }

}