import { Component } from '@angular/core';

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
        zoom: 8
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

  setCenter() {
    var view = this.map.getView();
    view.setCenter(ol.proj.fromLonLat([this.longitude, this.latitude]));
    view.setZoom(8);
  }

  setMarker(){
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
  
  }
  
  var vectorSource = new ol.source.Vector({
      features: features
  });
  
  var vectorLayer = new ol.layer.Vector({
      source: vectorSource
  });
  this.map.addLayer(vectorLayer);
}}
