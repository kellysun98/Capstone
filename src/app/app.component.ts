import { Component } from '@angular/core';

declare var ol: any;
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  latitude: number = 43.6548;
  longitude: number = -79.3883;
  end_lat: number = 43.8561;
  end_long: number = -79.3370;

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
    view.setZoom(10);
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

      //features.push(featureLine);
  
  }
  
  var vectorSource = new ol.source.Vector({
      features: features
  });
  
  var vectorLayer = new ol.layer.Vector({
      source: vectorSource
  });
  this.map.addLayer(vectorLayer);
  }

  drawLine(){
    var points = [[this.longitude, this.latitude], [this.end_long, this.end_lat]];

    /**for (var i = 0; i < points.length; i++) {
      var item = points[i];
      points[i] = ol.proj.transform(points[i], 'EPSG:4326', 'EPSG:3857');
    }**/
    var featureLine = new ol.Feature({
      geometry: new ol.geom.LineString(
        [ol.proj.fromLonLat(points[0]), ol.proj.fromLonLat(points[1])]
      )
    });
  
    var vectorLine = new ol.source.Vector({});
    vectorLine.addFeature(featureLine);
    
    var vectorLineLayer = new ol.layer.Vector({
        source: vectorLine,
        style: new ol.style.Style({
            fill: new ol.style.Fill({ color: '#00FF00', weight: 4 }),
            stroke: new ol.style.Stroke({ color: '#00FF00', width: 2 })
        })
    });
  
  this.map.addlayer(vectorLineLayer);
}

drawLine2(){
  var start_point = new ol.Geometry.Point(0,10);
  var end_point = new ol.Geometry.Point(30,0);

  var vector = new ol.Layer.Vector();
  vector.addFeatures([new ol.Feature.Vector(new ol.Geometry.LineString([start_point, end_point]))]);
  this.map.addLayers([vector]);

}
}