import { Component } from '@angular/core';
import Map from 'ol/Map';
import View from 'ol/View';
import VectorLayer from 'ol/layer/Vector';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import OSM from 'ol/source/OSM';
import * as olProj from 'ol/proj';
import TileLayer from 'ol/layer/Tile';
import { HttpClient, HttpParams } from '@angular/common/http';
import {MatDialogModule} from '@angular/material/dialog';
import { WelcomeComponent } from './Welcome/welcome.component' 
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import Point from 'ol/geom/Point'; 


declare var ol: any;
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  latitude: number;
  longitude: number;
  end_lat: number;
  end_long: number;
  map: any;
  response: any;
  email: string;

  constructor(private http:HttpClient, public dialog: MatDialog){

  }

  ngOnInit() {
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

    var points = [[-79.44, 43.66], [-79.36, 43.67] ,[-79.35, 43.65],[-79.42, 43.63]];
    var points1 = [[-80.44, 43.66], [-80.36, 43.67] ,[-80.35, 43.65],[-80.42, 43.63]];
    
    var opacity = 0.5;

    var style1 = new ol.style.Style({
      fill: new ol.style.Fill({ color: 'rgba(128,0,128,0.5)'}),
      stroke: new ol.style.Stroke({ color: 'rgba(0,0,0,0.8)', width: 2 })
    });

    var poly= new ol.geom.Polygon([points]);
    poly.transform('EPSG:4326', 'EPSG:3857');    
    var poly1= new ol.geom.Polygon([points1]);
    poly1.transform('EPSG:4326', 'EPSG:3857');    
    var featureLine = new ol.Feature({
          geometry: poly,
          });
    featureLine.setStyle(style1);

    var featureLine1 = new ol.Feature({
          geometry: poly1,
          });
  
    var vectorLine = new ol.source.Vector({});
    vectorLine.addFeature(featureLine);
    vectorLine.addFeature(featureLine1);
  
    var vectorLineLayer = new ol.layer.Vector({
        source: vectorLine,
    });
      this.map.addLayer(vectorLineLayer);
  }

  openDialog(): void {
    const dialogRef = this.dialog.open(WelcomeComponent, {
      width: '350px',
      data: {}
    });
  }
  
  setCenter() {
    var view = this.map.getView();
    view.setCenter(ol.proj.fromLonLat([this.longitude, this.latitude]));
    view.setZoom(10);
  }

  setMarker(){
    console.log(this.longitude.toString());
    let params = new HttpParams().set('longitude', this.longitude.toString()).set('latitude', this.latitude.toString()).set('end_long',this.end_long.toString()).set('end_lat', this.end_lat.toString())
    this.http.get("http://localhost:8080/api", {params:params})
    .subscribe((response) => {
        this.response = response;
        console.log(this.response);
    })

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
  var points = [ [this.longitude, this.latitude], [this.end_long, this.end_lat] ];

  for (var i = 0; i < points.length; i++) {
      points[i] = ol.proj.transform(points[i], 'EPSG:4326', 'EPSG:3857');
  }

  var featureLine = new ol.Feature({
      geometry: new ol.geom.LineString(points)
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
}

}