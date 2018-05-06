import {Component, ElementRef, Input, OnInit, Renderer2, SimpleChanges} from '@angular/core';
import {SongsService} from "../../services/songs.service";
import {DictionaryEntry} from "../../models/DictionaryEntry";
import {DictionariesContent} from "../../models/DictionariesContent";

@Component({
  selector: 'app-dictionariescontainer',
  templateUrl: './dictionariescontainer.component.html',
  styleUrls: ['./dictionariescontainer.component.css']
})
export class DictionariescontainerComponent implements OnInit {

  content: DictionariesContent;

  constructor(private songsService: SongsService) {}

  ngOnInit() {
    this.songsService.itemHoverChangeObservable.subscribe(message => {

      if (message == null) return;

      var dec = new DictionariesContent();

      let masterKey = this.songsService.geo[message];
      if (masterKey == null){
        console.log("no master key found for span=" + message );
        return;
      }

      let inflectedEntry = this.songsService.inflectedEntries[masterKey];

      dec.canonical_hindi = inflectedEntry.canonical_hindi;
      dec.inflected_hindi = inflectedEntry.inflected_hindi;
      dec.accidence = inflectedEntry.accidence;
      dec.part_of_speech = inflectedEntry.part_of_speech;
      dec.dictionary_entries = [];

      let dictionaryEntries = this.songsService.dictionaryEntries;

      inflectedEntry.canonical_keys.forEach(ck =>{
         dec.dictionary_entries.push(dictionaryEntries[ck]);
      });

      this.content = dec;

    });
  }

}
