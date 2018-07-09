import { Injectable } from '@angular/core';
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {HttpClient} from "@angular/common/http";
import { SongModel } from '../models/SongModel';
import { IGeo } from '../models/IGeo';
import {WordListInflected} from "../models/WordListInflected";
import {InflectedKey} from "../models/InflectedKey";
import {IInflectedEntries} from "../models/IInflectedEntries";
import {IDictionaryEntriesMap} from "../models/IDictionaryEntriesMap";
import {Globals} from "../globals";
import {INotInflectedEntries} from "../models/INotInflectedEntries";
import {NotInflectedEntry} from "../models/NotInflectedEntry";
import {NotInflectedKey} from "../models/NotInflectedKey";
import {WordListNotInflected} from "../models/WordListNotInflected";
import {IDictionaryEntriesModel} from "../models/IDictionaryEntriesModel";

@Injectable()
export class SongsService {

  //song-related
  private songSelectionChange = new BehaviorSubject<string>("select a song");
  public songSelectionChangeObservable = this.songSelectionChange.asObservable();

  public currentSong: SongModel;
  public inflectedGeo: IGeo = {};
  public notInflectedGeo: IGeo = {};
  public inflectedEntries: IInflectedEntries = {};
  public notInflectedEntries: INotInflectedEntries = {};
  public dictionaryEntriesInflected: IDictionaryEntriesModel[];
  public dictionaryEntriesNotInflected: IDictionaryEntriesModel[];

  //item-related
  public itemHoverChange = new BehaviorSubject<string>(null);
  public itemHoverChangeObservable = this.itemHoverChange.asObservable();

  //dictionary window position
  public pinned: boolean;
  public pinnedLeftStyle: string;
  public pinnedTopStyle: string;



  constructor(private http: HttpClient, private globals: Globals) { }

  changeSelectedSong(message: string) {
    this.retrieveSong(message);
  }

  retrieveSong(songTitleLatin: string): void {
    this.http.get<SongModel>( "http://localhost:8080/songs/findByLatinName", {params:{ "songLatinName" : songTitleLatin }})
      .subscribe(data => {
        if ( data!= null ) {
          this.currentSong = data;
          this.populateInflectedGeo(data);
          this.populateNotInflectedGeo(data);
          this.songSelectionChange.next(data.title_latin);
        }else{
          alert("Song not found");
        }
    });
  }

  populateInflectedGeo(song: SongModel) {
    this.inflectedGeo = {};
    this.inflectedEntries = {};

    if (song.word_list_master != null) {

      song.word_list_master.forEach(entry => {
        let wlm = entry as WordListInflected;
        if (wlm.song_word_indices == null) {
          alert(wlm + " has no song_word_indexes")
        }
        wlm.song_word_indices.forEach(id => {
          this.inflectedGeo[String(id)] = SongsService.buildInflectedKey(wlm.inflected_key);
        })
      });

    }else{
      alert("No word_list_master found for song: " + song.title_latin);
    }

    this.dictionaryEntriesInflected = JSON.parse(song.dictionary_entries_inflected);
    this.inflectedEntries = JSON.parse(song.inflected_entries);
    this.notInflectedEntries = JSON.parse(song.not_inflected_entries);

    //complement part of speech labels
    for (let key in this.inflectedEntries) {
      let value = this.inflectedEntries[key];
      value.part_of_speech_label = this.globals.partsOfSpeech[value.part_of_speech];
    }
  }

  populateNotInflectedGeo(song: SongModel) {
    this.notInflectedGeo = {};
    this.notInflectedEntries = {};
    if (song.word_list_not_inflected == null) {
      return;
    }

    song.word_list_not_inflected.forEach(entry => {
      let wlm = entry as WordListNotInflected;
      if (wlm.song_word_indices == null){
        alert(wlm + " has no song_word_indexes")
      }
      wlm.song_word_indices.forEach(id => {
        this.notInflectedGeo[String(id)] = SongsService.buildNotInflectedKey(wlm.not_inflected_key);
      })
    });

    this.dictionaryEntriesNotInflected = JSON.parse(song.dictionary_entries_not_inflected);
    this.notInflectedEntries = JSON.parse(song.not_inflected_entries);

    //complement part of speech labels
    for (let key in this.notInflectedEntries) {
      let value = this.notInflectedEntries[key];
      value.part_of_speech_label = this.globals.partsOfSpeech[value.part_of_speech];
    }

  }


  private static buildInflectedKey(mk: InflectedKey){
    return mk.inflected_hindi + "_" + mk.inflected_hindi_index;
  }

  private static buildNotInflectedKey(mk: NotInflectedKey){
    return mk.hindi + "_" + mk.hindi_index;
  }


}
