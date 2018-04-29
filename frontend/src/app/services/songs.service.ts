import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {HttpClient} from "@angular/common/http";
import { SongModel } from '../models/SongModel';

@Injectable()
export class SongsService {

  private messageSource = new BehaviorSubject<string>("select a song");
  currentMessage = this.messageSource.asObservable();

  public currentSong: SongModel;

  constructor(private http: HttpClient) { }

  changeMessage(message: string) {
    this.retrieveSong(message);

  }

  retrieveSong(songTitleLatin: string): void {
    this.http.get<SongModel>( "http://localhost:8080/songs/findByLatinName", {params:{ "songLatinName" : songTitleLatin }})
      .subscribe(data => {
        if ( data!= null ) {
          this.currentSong = data;
          this.messageSource.next(data.title_latin);
        }else{
          alert("Song not found");
        }
    });
  }

}
