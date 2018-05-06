import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {HttpClient} from "@angular/common/http";
import { SongModel } from '../models/SongModel';
import { IGeo } from '../models/IGeo';
import {WordListMaster} from "../models/WordListMaster";
import {InflectedKey} from "../models/InflectedKey";
import {IInflectedEntries} from "../models/IInflectedEntries";
import {IDictionaryEntries} from "../models/IDictionaryEntries";

@Injectable()
export class SongsService {

  //song-related
  private songSelectionChange = new BehaviorSubject<string>("select a song");
  public songSelectionChangeObservable = this.songSelectionChange.asObservable();

  public currentSong: SongModel;
  public geo: IGeo = {};
  public inflectedEntries: IInflectedEntries = {};
  public dictionaryEntries: IDictionaryEntries = {};

  //item-related
  public itemHoverChange = new BehaviorSubject<string>(null);
  public itemHoverChangeObservable = this.itemHoverChange.asObservable();



  constructor(private http: HttpClient) { }

  changeSelectedSong(message: string) {
    this.retrieveSong(message);
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

  populateGeo(song: SongModel) {
    this.geo = {};
    this.inflectedEntries = {};

    song.word_list_master.forEach(entry => {
      let wlm = entry as WordListMaster;
      if (wlm.song_word_indices == null){
        alert(wlm + " has no song_word_indexes")
      }
      wlm.song_word_indices.forEach(id => {
        this.geo[String(id)] = SongsService.buildMasterKey(wlm.inflected_key);
      })
    });

    this.dictionaryEntries = JSON.parse(song.dictionary_entries);
    this.inflectedEntries = JSON.parse(song.inflected_entries);
  }

  private static buildMasterKey(mk: InflectedKey){
    return mk.inflected_hindi + "_" + mk.inflected_hindi_index;
  }

}
