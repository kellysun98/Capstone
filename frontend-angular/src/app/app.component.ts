import { DataService } from './data.service';
import { RouteService } from './route.service';
import { Component, ErrorHandler, ViewChild } from '@angular/core';
import Map from 'ol/Map';
import Stroke from 'ol/style/Stroke';
import Fill from 'ol/style/Fill';
import View from 'ol/View';
import VectorLayer from 'ol/layer/Vector';
import VectorSource from 'ol/source/Vector';
import Style from 'ol/style/Style';
import Icon from 'ol/style/Icon';
import OSM from 'ol/source/OSM';
import * as olProj from 'ol/proj';
import TileLayer from 'ol/layer/Tile';
import { HttpClient, HttpErrorResponse, HttpHeaders, HttpParams } from '@angular/common/http';
import {MatDialogModule} from '@angular/material/dialog';
import { QuestionaireComponent } from './questionaire/questionaire.component';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatCardModule} from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { WelcomepageComponent } from './welcomepage/welcomepage.component';
import { ThrowStmt } from '@angular/compiler';
import { Observable, forkJoin, EMPTY } from 'rxjs';
import { concatMap, map, take } from 'rxjs/operators';
import GeoJSON from 'ol/format/GeoJSON';
import {MatSidenav, MatSidenavModule} from '@angular/material/sidenav';
import {FullScreen, defaults as defaultControls} from 'ol/control';
import XYZ from 'ol/source/XYZ';
import { ɵBrowserPlatformLocation } from '@angular/common';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';
import { MybarComponent } from './mybar/mybar.component';
import { Input, Directive } from '@angular/core';
import { MAT_RIPPLE_GLOBAL_OPTIONS } from '@angular/material/core';
import { Route } from './route';
import { THIS_EXPR } from '@angular/compiler/src/output/output_ast';
import Select from 'ol/interaction/Select';
import {altKeyOnly, click, pointerMove} from 'ol/events/condition';
import { TwitterService } from './tweets/twitter.service';
import { TweetsComponent } from './tweets/tweets.component';


declare var ol: any;
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css'],
})
export class AppComponent {
  tweetslists: Array<any>;
  active: boolean;
  open: boolean;
  heatmap: Object;
  start_add: String = '7 grenville, toronto';
  end_add: String = '68 shuter street, toronto';
  latitude: number;
  longitude: number;
  // end_lat: number;
  // end_long: number;
  map: any;
  response: Object;
  email: string;
  testArray: any[];
  coor: Coordinates[];
  public useDefault = true;
  public show: boolean = false;
  slider_val: number;
  gridsize: number;
  featureOverlay:any;
  highlight:any;
  info: any;
  valueEmittedFromChildComponent: any;
  userTrans: any;
  isSearching: boolean;
  amenities_data = [
    {"Covid-19 Assessment center": [
        [43.7230426, -79.601108], [43.7088966, -79.5072457], [43.689953200000005, -79.32493147310899], [43.657436849999996, -79.3903184208715],
        [43.771426, -79.44728711605052], [43.80174045, -79.3091964718297], [43.65432, -79.3787717], [43.661733999999996, -79.38754180202815],
        [43.6403905, -79.45014220870584], [43.7806321, -79.2055214], [43.697279, -79.424049], [43.689953200000005 , -79.32493147310899], 
        [43.7806321 , -79.2055214], [43.75646975 , -79.24772575726186], [43.72285855 , -79.37566139839973], [43.70565595 , -79.34613324230222]
    ]},
    {"Pharmacies Offering COVID-19 Testing": [
        [43.6839624 , -79.32225115772025], [43.7063262 , -79.3751848], [43.6777039 , -79.3895544], [43.6400616 , -79.53802386472626],
        [43.6666823 , -79.4474682], [43.6665844 , -79.4051229], [43.7113345 , -79.56505991761807], [43.7358015 , -79.56011714788619], 
        [43.649079 , -79.3776658], [43.649079 , -79.3776658], [43.6369239 , -79.3994356], [43.7561355 , -79.51578446064315], 
        [43.7330733 , -79.2676115], [43.8536178 , -79.2571515], [43.725349 , -79.2306562], [43.7944521 , -79.2399838], [43.6907767 , -79.4729653], 
        [43.700423 , -79.4272015], [44.250337 , -76.55354297448093], [43.6797074 , -79.5463636]
    ]},
    {"Shopping Malls": [
        [43.777758500000004 , -79.34429375180316], [43.7761341 , -79.25843763592165], [43.6119 , -79.5571], [43.691600300000005 , -79.39028416035379], [43.7256238 , -79.45230789320112]
    ]}
  ]
  
  
  // private slider:DataService

  constructor(
    private _twitterService: TwitterService,
    private http: HttpClient, 
    public dialog: MatDialog, 
    private route:RouteService){
  }
  ngOnInit() {
    this.isSearching = true;
    this.openWelcome();
    //this.Heatmap2();
    // this.initBackEnd();
    var mousePositionControl = new ol.control.MousePosition({
      coordinateFormat: ol.coordinate.createStringXY(4),
      projection: 'EPSG:4326',
      undefinedHTML: '&nbsp;'
    });


    var highlightStyle = new ol.style.Style({
      stroke: new ol.style.Stroke({
        color: [255,0,0,0.6],
        width: 2
      }),
      fill: new ol.style.Fill({
        color: [255,0,0,0.6],
        width: 2
      }),
      zIndex: 2
    });
    var container = document.getElementById('popup');
    var content = document.getElementById('popup-content');
    var closer = document.getElementById('popup-closer');

/**
 * Create an overlay to anchor the popup to the map.
 */
    var popup = new ol.Overlay({
      element: container,
      autoPan: true,
      autoPanAnimation: {
      duration: 250,
  },
});

  closer.onclick = function () {
    popup.setPosition(undefined);
    closer.blur();
    return false;
};

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
    this.map.addOverlay(popup);

    this.toggleAmenities([this.amenities_data[0]['Covid-19 Assessment center'], 1, './assets/data/hospital.png']);
    this.toggleAmenities([this.amenities_data[1]['Pharmacies Offering COVID-19 Testing'], 2, './assets/data/drug.png']);
    this.toggleAmenities([this.amenities_data[2]['Shopping Malls'], 3, './assets/data/shopping_bag.png']);

    var selected = new ol.interaction.Select({
      condition: click,
 
    }); // ref to currently selected interaction
    this.map.addInteraction(selected);



    this.map.on('pointermove', function(evt) {    //change pointer when on a feature 
      this.getTargetElement().style.cursor = this.hasFeatureAtPixel(evt.pixel) ? 'pointer' : '';
    });

  var featureOverlay = new ol.layer.Vector({
    source: new ol.source.Vector({}),
    map: this.map,
    //style: highlightStyle,
  });

  

  this.map.on('click', function (args) {
    console.log(args.coordinate);
    var lonlat = ol.proj.transform(args.coordinate, 'EPSG:3857', 'EPSG:4326');
    console.log(lonlat);
    
 
    this.forEachFeatureAtPixel(args.pixel, function(feature,layer ) {  
      //remove previous highlighter
    var source = layer.getSource();
    var allRoutes = source.getFeatures();
    for (var i = 0; i < allRoutes.length; i++) {
      if(allRoutes[i].get('name') !== undefined && allRoutes[i].get('name') === 'highlighter'){
        source.removeFeature(allRoutes[i]);
      }
    }
      //add new highlighter
      if(feature.get('name') != undefined && (feature.get('name').includes('route') || feature.get('name').includes('route_trans'))){
        console.log(feature.get('name'));
        var routeName = feature.get('name');
    
          //find all features with the same name
          var source = layer.getSource();
          var allRoutes = source.getFeatures();
          for (var i = 0; i < allRoutes.length; i++) {
              // var highlightsList = [];
              if(allRoutes[i].get('name') !== undefined && allRoutes[i].get('name') === routeName){
                var highlights = allRoutes[i].clone();
                highlights.set('name','highlighter');
                var highlightStyle = new ol.style.Style({
                  fill: new ol.style.Fill({
                      color: 'rgba(128,0,0,0.3)', weight: 15
                    }),
                  stroke: new ol.style.Stroke({
                      color: 'rgba(128,0,0,0.3)', width: 15
                    }),
                });
                highlights.setStyle(highlightStyle);
                source.addFeature(highlights);
              }
          }
          
          content.innerHTML = '<p>Route Information: \n</p><code>' + feature.get('description') + '</code>';
          popup.setPosition(args.coordinate);


        }  

      // if ( feature.get('name').includes('additional_line')) {  //overlay closable pop up box 
      //   console.log(feature.get('name'));
      //     //  featureOverlay.getSource().clear()
      //     //  featureOverlay.
      //     //  featureOverlay.getSource().addFeature(feature);
      //     //  console.log("feature added to overlay")
      //     content.innerHTML = '<p>You clicked here:</p><code>' + feature.get('description') + '</code>';
      //     popup.setPosition(args.coordinate);
           

      // }
        
    });
  });
}
  updateSetting(event) {
    this.gridsize = event.value;
  }


  initBackEnd(){
    console.log('init started')
    let params = new HttpParams().set('init_num', 'loading request'); 
    var test = new ol.source.Vector({});   
    this.http.get("http://localhost:8080/init", {params:params}).pipe(take(1)).subscribe(
      (res)=>{
        for (var x =0; x < Object.keys(res).length; x+=1){
          var coord = Object.keys(res)[x];
          try {var tempkey = JSON.parse(coord);
          }
        catch {console.log(coord);
        continue;}
        var pointFeature = new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.transform(tempkey, 'EPSG:4326', 'EPSG:3857')),
            weight: res[coord] == "NaN"? 0 : res[coord]/9,
        });
        //console.log("coord is " + coord);
        //console.log("weight is " + pointFeature.getWeight());
        test.addFeature(pointFeature);
      }
    
      var Heat = new ol.layer.Heatmap({
        source: test,
        blur: 13,
        radius: 14,
        opacity : 0.6,
        name:'heatmap',
      });
      
      this.map.addLayer(Heat); 
      console.log('Process Complete'), alert("Backend Initialization Complete!")
    });
    console.log('loading')
  }
  

  getUrl(addr){
    var nomUrl = 'https://nominatim.openstreetmap.org/?addressdetails=1&q='
    var tempUrl = addr.split(' ').join('+')
    var finalUrl = nomUrl+tempUrl+'&format=json&limit=1'
    //console.log(finalUrl)
    // var proxyUrl = 'https://cors-anywhere.herokuapp.com/'

    return (finalUrl)
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

  setMarker(){

    let start_obs = this.http.get(this.getUrl(this.start_add));
    let end_obs = this.http.get(this.getUrl(this.end_add));

    forkJoin([start_obs, end_obs]).pipe(take(1)).subscribe(
      result => {
        this.map.getLayers().forEach(function(layer) {
          //console.log(layer.get('name'));
          if (layer.get('name') != undefined && layer.get('name') === 'markers') {
            layer.getSource().clear();
            console.log("markers removed ");
         
          }
      }); 
        var Markers = [{lat: JSON.parse(result[0][0]['lat']), lng: JSON.parse(result[0][0]['lon'])}, {lat: JSON.parse(result[1][0]['lat']), lng: JSON.parse(result[1][0]['lon'])}];
        //console.log(result[0][0]['lat'],result[1][0]['lat']);
        var features = [];
        for (var i = 0; i < Markers.length; i++) {
          var item = Markers[i];
          //console.log(item);
          var longitude = item.lng;
          var latitude = item.lat;
      
          var iconFeature = new ol.Feature({
              geometry: new ol.geom.Point(ol.proj.transform([longitude, latitude], 'EPSG:4326', 'EPSG:3857')),
              name: 'markericon'
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
    
      
       }
    
      )    
  }

  sliderLine(){
  
    this.http.get('http://localhost:8080/api').subscribe(
      (res)=>{
        this.map.getLayers().forEach(function(layer) {
          if (layer.get('name') != undefined && layer.get('name') === 'lines') {
          layer.getSource().clear();
          console.log("routes removed")   
          }
      });   //remove routes once the drawline function is called 

        this.response = res; 
        var myroutes = []
        var res_length = Object.keys(this.response).length;

        // console.log('From backend: ' + JSON.parse(res.toString()));
        for(var index = this.gridsize * 2 ; index < this.gridsize * 2 + 2; index++){   
          //console.log(index);
          var route = JSON.parse('[' + this.response[index][1] + ']');
          //console.log('second loop: ' + route)
                        
          var r_color =  Math.floor(Math.random() * (255 - 0 + 1) + 0);
          var g_color = Math.floor(Math.random() * (255 - 0 + 1) + 0);
          var b_color = Math.floor(Math.random() * (255 - 0 + 1) + 0);
          var color = 'rgba('+r_color+','+g_color+','+b_color+', 0.5'+')';

          for (var i = 0; i < route.length; i++) {
            //console.log('length enumeration '+i);
            route[i] = ol.proj.transform(route[i], 'EPSG:4326', 'EPSG:3857');
          }
          var featureLine = new ol.Feature({
            geometry: new ol.geom.LineString(route),
            name: 'nav_line',
            description: 'total time:' + this.response[index][2] + ', \n route description: ' + this.response[index][3],
          });

          var linestyle = new ol.style.Style({
            fill: new ol.style.Fill({
              color: color, weight: 5,
            }),
            stroke: new ol.style.Stroke({
              color: color, width: 5
            }),
          });
          featureLine.setStyle(linestyle);
          myroutes.push(featureLine)
        }
        var vectorSource = new ol.source.Vector({
          features: myroutes,
        }); //multiple routes added 
        
        var indicator = 0;
        this.map.getLayers().forEach(function(layer) {
          if (layer.get('name') != undefined && layer.get('name') === 'lines') {
            myroutes.forEach((feature) => {
              layer.getSource().addFeature(feature);            
            });    
              indicator = 1;   
              console.log("feature added to existing layer"); 
          }
        });

          if(indicator  === 0) {  
            console.log("new layer created"); 
            var vectorLineLayer = new ol.layer.Vector({
              source: vectorSource,
              name: 'lines',
          });
          this.map.addLayer(vectorLineLayer);
        }
      
      });    
        //});

  }

  //helper function to draw line according to risk level
  getColor(risk){ //route - line with two coordintaes
    // var myroutes = new Array();
    var color: string;
    if(risk>0 && risk <=3){
      var color = 'rgba(0, 204, 0, 1)'; //safe
      var risk_description = 'low'
    } else if(risk > 3 && risk <=6){
      var color = 'rgba(255, 152, 51, 1)'; //medium
      var risk_description = 'medium'
    }else{
      var color = 'rgba(255, 0, 0, 1)'; //dangerous
      var risk_description = 'high'
    }
    return color;
  }

  getColor4(risk){
    var color: string;
    if(risk>0 && risk <=1){
      var color = 'rgba(0, 204, 0, 1)'; //safe
      var risk_description = 'low'
    } else if(risk > 2 && risk <=4){
      var color = 'rgba(240, 255, 0, 1)'; //medium
      var risk_description = 'medium'
    } else if (risk > 4 && risk <= 6){
      var color = 'rgba(230, 126, 34, 1)'
      var risk_description = 'high'
    }
    else{
      var color = 'rgba(255, 0, 0, 1)'; //dangerous
      var risk_description = 'dangerous'
    }
    return color;
  }

  getGeomPed(amen, route, time, risk, description, mapnode, myroutes){
    //createing a layer feature which connects the full route 
    //and displays as transprant when not selected
    let avg = (array) => array.reduce((a, b) => a + b) / array.length;
    var avg_risk = (avg(risk)/9 * 100).toFixed(2);
    for (var i = 1; i < route.length; i++){
      route[i] = ol.proj.transform(route[i], 'EPSG:4326', 'EPSG:3857');
      
      var featureLine = new ol.Feature({
        geometry: new ol.geom.LineString([route[i-1],route[i]]),
        name: 'route'+amen,
        description: 'total time:' + time + ', \n route risk: ' + avg_risk + '%',
      });

      // var highlightLine = new ol.Feature({
      //   geometry: new ol.geom.LineString([route[i-1],route[i]]),
      //   name: 'highlight'+amen
      // });

      var linestyle = new ol.style.Style({
        fill: new ol.style.Fill({
            color: this.getColor(risk[i-1]), weight: 5
        }),
        stroke: new ol.style.Stroke({
          color: this.getColor(risk[i-1]), width: 5
        })
      });

      var highlightstyle = new ol.style.Style({
        fill: new ol.style.Fill({
            color: 'rgba(255,255,0,0.5)', weight: 5
        }),
        stroke: new ol.style.Stroke({
          color: 'rgba(255,255,0,0.5)', width: 5
        })
      });
      featureLine.setStyle(linestyle);
      myroutes.push(featureLine);

    }

  }

  getGeom(amen,route, time, risk, description, mapnode, myroutes){
    let avg = (array) => array.reduce((a, b) => a + b) / array.length;
    var avg_risk = (avg(risk)/9 * 100).toFixed(2);
    for (var i = 1; i < route.length; i++){
      route[i] = ol.proj.transform(route[i], 'EPSG:4326', 'EPSG:3857');
      //console.log('initial: ', route[i]);
      if(mapnode[i-1]==5 && mapnode [i] == 5){ //both are ped routes
        // var featurePoint = new ol.Feature({
        //   geometry: new ol.geom.Point(route[i-1]),
        // })
        // var pointStyle = new ol.style.Style({
        //   image: new ol.style.Circle({
        //     radius: 5,
        //     fill: new ol.style.Fill({color: this.getColor(risk[i-1])}),
        //     // stroke: new ol.style.Stroke({
        //     //   color: [255,0,0], width: 2
        //     // })
        //   })
        // })
        // featurePoint.setStyle(pointStyle);
        // myroutes.push(featurePoint)

        var pedLine = new ol.Feature({
          geometry: new ol.geom.LineString([route[i-1],route[i]]),
          name: 'route_trans'+ amen,
          description: 'total time:' + time + ', \n route risk: ' + avg_risk + '%',
        });
       //console.log([route[i-1], route[i]])

        var darkStroke = new ol.style.Style({
          stroke: new ol.style.Stroke({
            color: this.getColor(risk[i-1]),
            width: 8,
            lineDash: [6,28]
          })
        });
        pedLine.setStyle(darkStroke);
        myroutes.push(pedLine);
        console.log("pedline in effect")
        

        //console.log('transformed: ', route[i])
      }else if(mapnode[i-1]!=5 && mapnode[i]!=5){
        var featureLine = new ol.Feature({
          geometry: new ol.geom.LineString([route[i-1],route[i]]),
          name: 'route_trans'+ amen,
          description: 'total time:' + time + ', \n route risk: ' + avg_risk +'%',
        });
       //console.log([route[i-1], route[i]])
        var linestyle = new ol.style.Style({
          fill: new ol.style.Fill({
              color: this.getColor(risk[i-1]), weight: 8,
          }),
          stroke: new ol.style.Stroke({
            color: this.getColor(risk[i-1]), width: 8
          }),
    
        });
        
        featureLine.setStyle(linestyle);
        myroutes.push(featureLine);
      }
 
      //additional function for highlight line 
      // var highlightline = new ol.Feature({
      //   geometry: new ol.geom.LineString([route[i-1],route[i]]),
      //   name: 'route_trans'+amen,
      // });
      // var nullstyle = new ol.style.Style({
      //   stroke: new ol.style.Stroke({
      //     color: 'transparent',
      //     width: 10
      //   })
      // });


      // highlightline.setStyle(nullstyle);
      // myroutes.push(highlightline);
      // console.log(highlightline.get('name'));

    }
    //console.log(myroutes);
  }

  DynamicColoring(route, time, risk, des, myroutes, ttcname = []){ //display text box info
    // var myroutes = new Array();
  
    var featureLine = new ol.Feature({
      geometry: new ol.geom.LineString(route),
      name: 'additional_line',
      description: 'total time:' + time + ', \n route risk: ' + risk,
    });
    var nullstyle = new ol.style.Style({
      fill: new ol.style.Stroke({
        color: 'transparent'
     }),
     width: 10,
      text: new ol.style.Text({
        font: '14px Calibri,sans-serif',
        textBaseline: 'top',
        offsetY: 6,
        backgroundFill: new ol.style.Fill({
          color: 'rgba(255,255,255, 0.5)'
        }),
        backgroundStroke: new ol.style.Stroke({
          width: 1,
          color: 'rgba(0,0,0,0.5)'
        }),
        padding: [0,2,0,2]
      }),
      zIndex: 999,

    });
    var filteredAry = ttcname.filter(function(e) { return e !== "null" }).filter((v, i, a) => a.indexOf(v) === i);
    if (filteredAry != []){
    nullstyle.getText().setText('total time:\n' + time + "min");} //\n Transit:" + filteredAry
    else {nullstyle.getText().setText('total time: \n' + time + "min ")}
    featureLine.setStyle(nullstyle);
    myroutes.push(featureLine)
  }

  initSubwayStop(){
    let params = new HttpParams().set('ver', 'loading request'); 
    this.http.get("http://localhost:8080/subway", {params:params}).pipe(take(1)).subscribe(
      (res)=>{
        this.response = res;

        //console.log(this.response);
        
        var features = [];
        for (let amen of Object.values(this.response)){
          //console.log(amen);
          //var coor = JSON.parse(amen);
          //console.log("subway coordinate is: ", coor)
          // coor[0] = ol.proj.transform(coor[0], 'EPSG:4326', 'EPSG:3857');
          // coor[1] = ol.proj.transform(coor[1], 'EPSG:4326', 'EPSG:3857');
          var iconFeature = new ol.Feature({
            geometry: new ol.geom.Point(ol.proj.transform(amen, 'EPSG:4326', 'EPSG:3857')),
            name: 'markericon'
          });    
        
          // var iconStyle = new ol.style.Style({
          //   image: new ol.style.Icon(({
          //       anchor: [0.5, 1],
          //       src: "https://openlayers.org/en/latest/examples/data/icon.png"
          //   }))          
          // })
          // iconFeature.setStyle(iconStyle);
          features.push(iconFeature);
        }
        var vectorSource = new ol.source.Vector({
          features: features,
        });
        var vectorLayer = new ol.layer.Vector({
          source: vectorSource        });

        this.map.addLayer(vectorLayer);
    }
    )
  }

  showLine(event){
    this.valueEmittedFromChildComponent=event;
    this.http.get("http://localhost:8080/heatmap").pipe(take(1))
    .subscribe(() => {
      if (this.valueEmittedFromChildComponent == 0){
        var transit_status = false;
        var ped_status = true} 
      else{
          var transit_status = true;
          var ped_status = false
      }
      this.map.getLayers().forEach(function(layer){
        if(layer.get('name') != undefined && layer.get('name') === 'ped-lines'){
            layer.setVisible(ped_status);
        } 
        if(layer.get('name') != undefined && layer.get('name') === 'transit-lines'){
            layer.setVisible(transit_status);
          }
        })
      })
    }

    closeNotification(): void{
      this.isSearching = true;
    }

  drawLine2(){
    

    this.http.get("http://localhost:8080/getTrans").pipe(take(1))
    .subscribe((trans) => {
       this.userTrans = trans;
       //console.log(this.userTrans);
    })
    let start_obs = this.http.get(this.getUrl(this.start_add));
    let end_obs = this.http.get(this.getUrl(this.end_add));
    forkJoin([start_obs, end_obs]).pipe(take(1)).pipe(take(1)).subscribe(
      result => {
        var bound_list = {'start_bound': (result[0][0]['boundingbox']).toString(), 'end_bound': (result[1][0]['boundingbox']).toString()}
    // console.log(bound_list);
        let headers = new HttpHeaders({ 'Content-Type': 'application/json' });
        this.http.post('http://localhost:8080/address', bound_list, { headers:headers }).pipe(
          take(1),
          concatMap(
            async (val) => this.http.get('http://localhost:8080/api').pipe(take(1)).subscribe(
              (res)=>{
              this.map.getLayers().forEach(function(layer) {
                if (layer.get('name') != undefined && (layer.get('name') === 'ped-lines' || layer.get('name') === 'transit-lines')) {
                  layer.getSource().clear();
                  console.log("routes removed")   
                }
              });   //remove routes once the drawline function is called 
          
          //if(this.valueEmittedFromChildComponent==0)

          this.response = res; 
          for (let result of Object.values(this.response)){
              var res_length = Object.keys(this.response[0]).length;
              if (res_length>=5){
                var iter_len = 5
              }else{
                var iter_len = res_length
              }
              var myroutes = [];
              var myhighlighter = [];
          //console.log("res_lenght is " + res_length) 
              for(var amen = 0; amen<iter_len; amen++){ 
              // for(let index in this.response[amen]['routeNode']){
                // for (let key of Object.keys(this.response[index])){
                var route = JSON.parse(this.response[0][amen]['routeNode']);
                var risk = JSON.parse(this.response[0][amen]['risk']);
                route[0] = ol.proj.transform(route[0], 'EPSG:4326', 'EPSG:3857');
                var time = this.response[0][amen]['time'];
                var description = this.response[0][amen]['description'];
                var mapnode = Array(route.length).fill(5)
                var g_color = Math.floor(Math.random() * (255 - 0 + 1) + 0);
                var b_color = Math.floor(Math.random() * (255 - 0 + 1) + 0);
                var color = 'rgba(0'+','+g_color+','+b_color+', 0.8)';

            //colors adjusted for different risk levels
            // var safe = 'rgba(0, 204, 0, 1)';
            // var medium = 'rgba(255, 152, 51, 1)';
            // var dangerous = 'rgba(255, 0, 0, 1)';
          
                this.getGeomPed(amen, route, time, risk, description, mapnode, myroutes);
                this.DynamicColoring(route, time, risk, description,myroutes)  //display text
              }
              var vectorSource = new ol.source.Vector({
                features: myroutes,
              }); //multiple routes added 


              var indicator = 0;
               this.map.getLayers().forEach(function(layer) {
                 if (layer.get('name') != undefined && layer.get('name') === 'ped-lines') {
                   myroutes.forEach((feature) => {
                     layer.getSource().addFeature(feature);            
                   });      
                     indicator = 1;   
                     console.log("feature added to existing layer"); 
                 }
               });
  
              if(indicator === 0) {  
                console.log("new layer created"); 
                var vectorLineLayer = new ol.layer.Vector({
                  source: vectorSource,
                  name: 'ped-lines',
              });
                this.map.addLayer(vectorLineLayer);
              }
      
              //public transit start

              // this.map.getLayers().forEach(function(layer) {
              //   console.log(layer.get('name'));  
              // }); 
              var res_length_transit = Object.keys(this.response[1]).length;
              var myroutes_transit = [];
              var routes_walk = [];
              for(var amen = 0; amen<res_length_transit; amen++){ 
              // for(let index in this.response[amen]['routeNode']){
                // console.log('first loop: ' + this.response[amen]['routeNode'])
                // for (let key of Object.keys(this.response[index])){
                var route_transit = JSON.parse(this.response[1][amen]['routeNode']);
                var risk_transit = JSON.parse(this.response[0][amen]['risk']); 
                
                route_transit[0] = ol.proj.transform(route_transit[0], 'EPSG:4326', 'EPSG:3857');
                var time_transit = this.response[1][amen]['time'];
                var description_transit = this.response[1][amen]['description'];
                var nodetype = JSON.parse(this.response[1][amen]['nodetype']);
                var ttcname = JSON.parse(this.response[1][amen]['linenumber']);
                // console.log('print route: ' + route[1]);
                // console.log('print risk: ' + risk[0]);
                // console.log('route length: '+ route.length);

                var g_color = Math.floor(Math.random() * (255 - 0 + 1) + 0);
                var b_color = Math.floor(Math.random() * (255 - 0 + 1) + 0);
                var color = 'rgba(0'+','+g_color+','+b_color+', 0.8)';

            //colors adjusted for different risk levels
            // var safe = 'rgba(0, 204, 0, 1)';
            // var medium = 'rgba(255, 152, 51, 1)';
            // var dangerous = 'rgba(255, 0, 0, 1)';
                this.getGeom(amen,route_transit, time_transit, risk_transit, description_transit, nodetype, myroutes_transit);
                this.DynamicColoring(route_transit, time_transit, risk_transit, description_transit,myroutes_transit,ttcname)  //display text
              }
              var vectorSource_transit = new ol.source.Vector({
                features: myroutes_transit,
              }); //multiple routes added 
              
              var indicator = 0;
              this.map.getLayers().forEach(function(layer) {
                if (layer.get('name') != undefined && layer.get('name') === 'transit-lines') {
                  myroutes_transit.forEach((feature) => {
                    layer.getSource().addFeature(feature);            
                  });    
                    indicator = 1;   
                    console.log("feature added to existing layer"); 
                }
              });
              if (this.userTrans.includes("Walking")){
                console.log("both walking and public only");
                this.showLine(0);
              }else {
               console.log("showing public first")
               this.showLine(1)}
  
              if(indicator === 0) {  
                console.log("new layer created"); 
                var vectorLineLayer_transit = new ol.layer.Vector({
                  source: vectorSource_transit,
                  name: 'transit-lines',
              });
                this.map.addLayer(vectorLineLayer_transit);
               if (this.userTrans.includes("Walking")){
                  console.log("both walking and public only");
                  this.showLine(0);
               }else {
                 console.log("showing public first")
                 this.showLine(1)}
                //this.showLine(0);
              }
            }
          })
    )).subscribe(
      
      
      response=>{
        this.isSearching = false;
      },
      error => {},
    // () => {
    //   this.isSearching = true;
    // }
      
    )
    })
  }


  Heatmap(){
    var style1 = new ol.style.Style({
      fill: new ol.style.Fill({ color: 'rgba(128,0,128,0.3)'}),
      stroke: new ol.style.Stroke({ color: 'rgba(0,0,0,0.8)', width: 0.5 })
    });
    var vectorLine = new ol.source.Vector({});

    this.http.get("http://localhost:8080/heatmap").pipe(take(1))
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
  this.http.get("http://localhost:8080/heatmap2",{params:params}).pipe(take(1)).subscribe((data)=>{
    for (let key of Object.keys(data)){

      var coord  = '[ ' + key + ' ]';
      try {coord = JSON.parse(coord); 
        }
      catch {console.log(coord);
      continue;}
      //console.log(coord)

      var pointFeature = new ol.Feature({
        geometry: new ol.geom.Point(ol.proj.transform(coord, 'EPSG:4326', 'EPSG:3857')),
    });
    test.addFeature(pointFeature);
    //  points.push(new ol.Feature(new ol.geom.Point(ol.proj.transform(coord, 'EPSG:4326', 'EPSG:3857'))));
  }
 
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

heatmapControl(event: MatSlideToggleChange){
  this.http.get("http://localhost:8080/heatmap").pipe(take(1))
  .subscribe(() => {
    //console.log(event.checked);
  this.map.getLayers().forEach(function(layer) {
    if (layer.get('name') != undefined && layer.get('name') === 'heatmap' && event.checked === true) {
      layer.setVisible(true);
      console.log("heatmap on ");
    }
    else if (layer.get('name') != undefined && layer.get('name') === 'heatmap' && event.checked === false){
      layer.setVisible(false);
      console.log("heatmap off ");
    } 
  });
});
}


hideit(){
  var x = document.getElementById("willhide");
  if (x.style.display === "none") {
    x.style.display = "block";
  } else {
    x.style.display = "none";
  }
  var y = document.getElementById("willshow");
  if (y.style.display === "none") {
    y.style.display = "block";
  } else {
    y.style.display = "none";
  }
}

toggle(){
  this.show = !this.show;
}

checked1 = true;
checked2 = true;
checked3 = true;

showAssess(){ 
  if (this.checked1 === true){
    return [this.amenities_data[0]['Covid-19 Assessment center'], 1, './assets/data/hospital.png'];
  }else{return [false, 1]};
}

showPharm(){
  if (this.checked2 === true && this.amenities_data !== null){
    return [this.amenities_data[1]['Pharmacies Offering COVID-19 Testing'], 2, './assets/data/drug.png']
  }else{return [false, 2]};
}

showMall(){
  if (this.checked3 === true){
    return [this.amenities_data[2]['Shopping Malls'], 3, './assets/data/shopping_bag.png']
  }else{return [false, 3]};
}


toggleAmenities(amen){
  //console.log(amen[1])
  if(amen[0] === false){
    this.map.getLayers().forEach(function(layer) {
      //console.log(layer.get('name'));
      if (layer.get('name') != undefined && layer.get('name') === ('markers'+amen[1])) {
        layer.getSource().clear();
        console.log("markers removed ");
      }
  }); 
  }else{
    //console.log('amen: '+amen[0][0]);
    var Markers = amen[0];
    var features = [];
    for (var i = 0; i < Markers.length; i++) {
      var item = Markers[i];
      //console.log(item);
      var longitude = item[1];
      var latitude = item[0];
  
      var iconFeature = new ol.Feature({
          geometry: new ol.geom.Point(ol.proj.transform([longitude, latitude], 'EPSG:4326', 'EPSG:3857')),
          name: 'markericon'
      });    
      
      var iconStyle = new ol.style.Style({
          image: new ol.style.Icon(({
              anchor: [0.5, 1],
              src: amen[2]
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
      if (layer.get('name') != undefined && layer.get('name') === ('markers'+amen[1])) {
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
          name: ('markers'+amen[1]),
        });
      this.map.addLayer(vectorLayer);}

      this.map.getLayers().forEach(function(layer) {
        console.log(layer.get('name'));  
      });    
      
      }
}



// assessmentCentre: string[] = ['2 Janda Court Etobicoke', '200 Church Street North York', 
// '825 Coxwell Avenue Toronto', '600 University Avenue Toronto', '555 Finch Avenue West Toronto', '3030 Birchmount Road Scarborough', '2867 Ellesmere Road Scarborough', 
// '3050 Lawrence Ave E Scarborough', '2075 Bayview Avenue Toronto', '347 Bathurst Street Toronto', '30 The Queensway Toronto', '38 Shuter Street Toronto', 
// '76 Grenville Street Toronto', '4 The Market Place East York', '45 Overlea Boulevard Toronto', '22 Vaughan Road Toronto']

// pharmacies: string[] = ['Shoppers Drug Mart, 1630 Danforth Avenue',
// 'Shoppers Drug Mart, 1601 Bayview Avenue',
// 'Shoppers Drug Mart, 1027 Yonge Street',
// 'Shoppers Drug Mart, 3446 Dundas Street West',
// 'Shoppers Drug Mart, 1400 Dupont Street',
// 'Shoppers Drug Mart, 360A Bloor Street West',
// 'Shoppers Drug Mart, 123 Rexdale Boulevard',
// 'Shoppers Drug Mart, 900 Albion Road',
// 'Shoppers Drug Mart, 4841 Yonge Street',
// 'Shoppers Drug Mart, 5095 Yonge Street',
// 'Shoppers Drug Mart, 3874 Bathurst Street',
// 'Shoppers Drug Mart, 2550 Finch Avenue West',
// 'Shoppers Drug Mart, 2751 Eglinton Avenue East',
// 'Shoppers Drug Mart, 629 Markham Road',
// 'Shoppers Drug Mart, 2301 Kingston Road',
// 'Shoppers Drug Mart, Unit A-1780 Markham Road',
// 'Medicine Shoppe, 2600 Eglinton Avenue West',
// 'Village Square Pharmacy, 2942 Finch Avenue East',
// 'Rexall, 4459 Kingston Road',
// 'Rexall, 250 Wincott Drive',
// 'Rexall, 901 Eglinton Avenue West']

// malls: string[] = [
//   'Fairview Mall',
//   'Scarborough Town Centre',
//   'Sherway Gardens',
//   'Toronto Eaton Centre',
//   'Yorkdale Shopping Centre'
// ]

getTweets(): void {
  const dialogRef = this.dialog.open(TweetsComponent, {
    width: '500px',
    height: '500px',
    data: {}
  });
}


}