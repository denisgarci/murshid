import {Component, ElementRef, Renderer2} from '@angular/core';
import {Globals} from './globals'
import {SongsService} from "./services/songs.service";
import {SongModel} from "./models/SongModel";
import {HttpClient} from "@angular/common/http";
import {IEnum} from "./models/IEnum";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'app';

  constructor(private globals: Globals, private http: HttpClient) {}

  ngOnInit() {

    this.http.get<IEnum>( "http://localhost:8080/domain/partsOfSpeech")
      .subscribe(data => {
        if ( data!= null ) {
          this.globals.partsOfSpeech = data;
        }else{
          alert("Parts of speech not received");
        }
      });

    this.http.get<IEnum>( "http://localhost:8080/domain/accidenceTypes")
      .subscribe(data => {
        if ( data!= null ) {
          this.globals.accidenceTypes = data;
        }else{
          alert("Parts of speech not received");
        }
      });
  }
}
