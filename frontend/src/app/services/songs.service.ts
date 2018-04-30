import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {HttpClient} from "@angular/common/http";
import { SongModel } from '../models/SongModel';
import { IGeo } from '../models/IGeo';
import {WordListMaster} from "../models/WordListMaster";
import {MasterKey} from "../models/MasterKey";
import {IMasterEntries} from "../models/IMasterEntries";

@Injectable()
export class SongsService {

  //song-related
  private songSelectionChange = new BehaviorSubject<string>("select a song");
  public songSelectionChangeObservable = this.songSelectionChange.asObservable();

  public currentSong: SongModel;
  public geo: IGeo = {};
  public masterEntries: IMasterEntries = {};

  //item-related
  public itemHoverChange = new BehaviorSubject<string>("select a span");
  public itemHoverChangeObservable = this.itemHoverChange.asObservable();



  constructor(private http: HttpClient) { }

  changeSelectedSong(message: string) {
    this.retrieveSong(message);
  }

  changeSelectedSpan(id: string){
    console.log("id changed to" + id);
  }

  retrieveSong(songTitleLatin: string): void {
    this.http.get<SongModel>( "http://localhost:8080/songs/findByLatinName", {params:{ "songLatinName" : songTitleLatin }})
      .subscribe(data => {
        if ( data!= null ) {
          this.currentSong = data;
          this.populateGeo(data);
          this.songSelectionChange.next(data.title_latin);
        }else{
          alert("Song not found");
        }
    });
  }

  populateGeo(song: SongModel){
      this.geo = {};
      this.masterEntries = {};

      song.word_list_master.forEach(entry =>{
        let wlm = entry as WordListMaster;
        wlm.indices.forEach(id => {
          this.geo[String(id)] = this.buildMasterKey(wlm.master_key);
        })
    })
    this.masterEntries = JSON.parse(song.master_entries);
  }

  private buildMasterKey(mk: MasterKey){
    let wordWithUnderscore = mk.hindi_word.replace(/ /g,"_");
    return wordWithUnderscore + "_" + mk.word_index;
  }

}