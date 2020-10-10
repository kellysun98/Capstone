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
import { Observable, forkJoin, EMPTY } from 'rxjs';
import { map } from 'rxjs/operators';
import GeoJSON from 'ol/format/GeoJSON';
import {MatSidenav, MatSidenavModule} from '@angular/material/sidenav';
import {FullScreen, defaults as defaultControls} from 'ol/control';
import XYZ from 'ol/source/XYZ';
import { ɵBrowserPlatformLocation } from '@angular/common';



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
  start_add: String = 'St. Michael\'s College parking, Bay Street, Toronto Centre, Old Toronto, Toronto';
  end_add: String = '65, Queen\'s Park Crescent East, University—Rosedale, Old Toronto, Toronto';
  latitude: number;
  longitude: number;
  // end_lat: number;
  // end_long: number;
  map: any;
  response: Object;
  email: string;
  testArray: any[];
  coor: Coordinates[];
  amentities: string[] = ['Covid-19 Assessment center', 'Hospital', 'Mall', 'Restaurants'];
  


  constructor(private http: HttpClient, public dialog: MatDialog){
  }
    ngOnInit() {
      this.openWelcome();
      //this.Heatmap();
      this.Heatmap2();   
      //this.setMapToFullScreen();
    //this.getAllCoords();
    var mousePositionControl = new ol.control.MousePosition({
      coordinateFormat: ol.coordinate.createStringXY(4),
      projection: 'EPSG:4326',
      // comment the following two lines to have the mouse position
      // be placed within the map.
      // className: 'custom-mouse-position',
      // target: document.getElementById('mouse-position'),
      undefinedHTML: '&nbsp;'
    });

    this.map = new ol.Map({
      target: 'map',
      controls: ol.control.defaults({
        attributionOptions: {
          collapsible: false
        }
      })
      .extend([mousePositionControl], [new FullScreen({
        source: 'fullscreen',
      })]),
      layers: [
        new ol.layer.Tile({
          source: new ol.source.OSM(),
        })
      ],
      view: new ol.View({
        center: ol.proj.fromLonLat([-79.3883, 43.6548]),
        zoom: 12
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
  
  setMapToFullScreen(){
    var elem = document.getElementById('map');
    if (elem.requestFullscreen) {
      elem.requestFullscreen();
  }}

  getUrl(addr){
    var nomUrl = 'https://nominatim.openstreetmap.org/?addressdetails=1&q='
    var tempUrl = addr.split(' ').join('+')
    var finalUrl = nomUrl+tempUrl+'&format=json&limit=1'
    console.log(finalUrl)
    // var proxyUrl = 'https://cors-anywhere.herokuapp.com/'

    return (finalUrl)
    // .then((
    //   blob=>blob.json()
    // ))
    // .then((
    //   data=>{
    //     this.longitude = (data[0]['lon']);
    //     console.table(data);
    //     console.log(data[0]['lon']);
    //     console.log(this.longitude)
    // }))
    // .catch(e => {
    //   console.log(e);
    //   return e;
    // });
    // console.log('Yvonne\'s longitude'+this.longitude);
    // return this.longitude;
  }

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
  // getAllCoords(){
  //   let start_obs = this.http.get(this.getUrl(this.start_add));
  //   let end_obs = this.http.get(this.getUrl(this.end_add));

  //   forkJoin([start_obs, end_obs]).subscribe(
  //     result => {
  //       let params = new HttpParams().set('longitude', result[0][0]['lon'].toString())
  //                                 .set('latitude', result[0][0]['lat'].toString())
  //                                 .set('end_long', result[1][0]['lon'].toString())
  //                                 .set('end_lat', result[1][0]['lat'].toString())
  //       this.http.get('http://localhost:8080/api', {params:params})
  //     }
  //   )

    // let params = new HttpParams().set('longitude', this.getLon(this.start_add).toString())
    //                               .set('latitude', this.getLat(this.start_add).toString())
    //                               .set('end_long', this.getLon(this.end_add).toString())
    //                               .set('end_lat', this.getLat(this.end_add).toString())
    // return this.http.get('http://localhost:8080/api', {params:params})
  //}

  setMarker(){
    // console.log(this.longitude.toString());
    // let params = new HttpParams().set('longitude', this.longitude.toString()).set('latitude', this.latitude.toString()).set('end_long',this.end_long.toString()).set('end_lat', this.end_lat.toString())
    // this.http.get("http://localhost:8080/api", {params:params})
    // .subscribe((response) => {this.response})
    let start_obs = this.http.get(this.getUrl(this.start_add));
    let end_obs = this.http.get(this.getUrl(this.end_add));

    forkJoin([start_obs, end_obs]).subscribe(
      result => {
        this.map.getLayers().forEach(function(layer) {
          console.log(layer.get('name'));
          if (layer.get('name') != undefined && layer.get('name') === 'markers') {
            layer.getSource().clear();
            console.log("markers removed ");
         
          }
      });
        var Markers = [{lat: JSON.parse(result[0][0]['lat']), lng: JSON.parse(result[0][0]['lon'])}, {lat: JSON.parse(result[1][0]['lat']), lng: JSON.parse(result[1][0]['lon'])}];
        console.log(result[0][0]['lat'],result[1][0]['lat']);
        var features = [];
        for (var i = 0; i < Markers.length; i++) {
          var item = Markers[i];
          console.log(item);
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
          features: features,
        });
        
        var indicator = 0;
        this.map.getLayers().forEach(function(layer) {
          if (layer.get('name') != undefined && layer.get('name') === 'markers') {
            features.forEach((feature) => {
              layer.getSource().addFeature(feature);            
            });    
              indicator = 1;   
              console.log("feature added to existing layer"); 
          }
        });

          if(indicator  === 0) {  
            console.log("new layer created"); 
            var vectorLayer = new ol.layer.Vector({
            source: vectorSource,
            name: 'markers',
          });
          this.map.addLayer(vectorLayer);}

          this.map.getLayers().forEach(function(layer) {
            console.log(layer.get('name'));  });    
        // tslint:disable-next-line:prefer-const
        // var vectorLayer = new ol.layer.Vector({
        //   source: vectorSource,
        //   name: 'markers',
        // });
        
        // this.map.addLayer(vectorLayer);
    
      }
    )    
  }

drawLine2(){
  let start_obs = this.http.get(this.getUrl(this.start_add));
  let end_obs = this.http.get(this.getUrl(this.end_add));

  forkJoin([start_obs, end_obs]).subscribe(
    result => {
      // let params = new HttpParams().set('longitude', result[0][0]['lon'].substring(0,10))
      //                           .set('latitude', result[0][0]['lat'].substring(0,9))
      //                           .set('end_long', result[1][0]['lon'].substring(0,10))
      //                           .set('end_lat', result[1][0]['lat'].substring(0,9));

      let params = new HttpParams().set('bound_start', result[0][0]['boundingbox']).set('bound_end', result[1][0]['boundingbox'])
      this.http.get('http://localhost:8080/api', {params:params}).subscribe(
        (res)=>{

          this.map.getLayers().forEach(function(layer) {
            if (layer.get('name') != undefined && layer.get('name') === 'lines') {
              var features = layer.getSource().getFeatures();
              features.forEach((feature) => {
                  layer.getSource().removeFeature(feature);
              });          
            }
        });

          this.response = res; 
          // console.log('From backend: ' + JSON.parse(res.toString()));
          for (let index in this.response){
            console.log('first loop: ' + index)
            for (let key of Object.keys(this.response[index])){
              var route = JSON.parse('[' + this.response[index][key] + ']');
              console.log('second loop: ' + route)
              
          // for (let key of Object.keys(this.response)){
          //   var route = this.response[key];
          //   route = '[' + route + ']';
          //   try{
          //     route = JSON.parse(route)
          //   }catch{
          //     console.log(route.length);
          //     continue;
          //   }
              
              var r_color =  Math.floor(Math.random() * (255 - 0 + 1) + 0);
              var g_color = Math.floor(Math.random() * (225 - 0 + 1) + 0);
              var b_color = Math.floor(Math.random() * (225 - 0 + 1) + 0);
              var color = 'rgba('+r_color+','+g_color+','+b_color+', 0.5'+')';

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
                      fill: new ol.style.Fill({ color: color, weight: 5 }),
                      stroke: new ol.style.Stroke({ color: color, width: 5})
                  }),
                  name: 'lines',
            });
            this.map.addLayer(vectorLineLayer);

          }}      
            
          this.map.getLayers().forEach(function(el) {
            console.log(el.get('name'));
        });        
      },
          // this.testArray.concat(Array((this.response))),
          // console.log('test: '+this.response)},
        (err)=>console.error(err),
        ()=>console.log(this.response + 'Process Complete!')
      );
    }
  )  //  .pipe(map(response=>JSON.parse))
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

Heatmap2(){
  var test = new ol.source.Vector({});
  var points = new Array();
  let params = new HttpParams().set('start_time', "2020-09-11 00:00:00").set('end_time', "2020-09-13 00:00:00")
  this.http.get("http://localhost:8080/heatmap2",{params:params}).subscribe((data)=>{
    for (let key of Object.keys(data)){
      var coord  = '[ ' + key + ' ]';
      try { coord = JSON.parse(coord); 
        }
      catch {console.log(coord);
      continue;}

      var pointFeature = new ol.Feature({
        geometry: new ol.geom.Point(ol.proj.transform(coord, 'EPSG:4326', 'EPSG:3857')),
    });
    test.addFeature(pointFeature);
    //  points.push(new ol.Feature(new ol.geom.Point(ol.proj.transform(coord, 'EPSG:4326', 'EPSG:3857'))));
  }
 
  
  // var source = new ol.source.Vector({
  //   features: points
  // });
  
  // var vector = new ol.layer.Heatmap({
  //   source: points,
  //   weight: function(feature) { return feature.get('points').length/1000; },
  //   blur: 10,
  //   radius: 10,
  // });

  var vector = new ol.layer.Heatmap({
    source: test,
    blur: 20,
    radius: 15,
    opacity : 0.5,
    name:'heatmap',
  });
  
  this.map.addLayer(vector); 
  });
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