import {Component, ElementRef, Input, OnInit, Renderer2, SimpleChanges} from '@angular/core';
import {SongsService} from "../../services/songs.service";
import {DictionariesContent} from "../../models/DictionariesContent";
import {Globals} from "../../globals";

@Component({
  selector: 'app-dictionariescontainer',
  templateUrl: './dictionariescontainer.component.html',
  styleUrls: ['./dictionariescontainer.component.css']
})
export class DictionariescontainerComponent implements OnInit {

  content: DictionariesContent;

  constructor(private songsService: SongsService, private globals: Globals) {}

  ngOnInit() {
    this.songsService.itemHoverChangeObservable.subscribe(message => {

      if (message == null) return;

      var dec = new DictionariesContent();

      let inflectedKey = this.songsService.inflectedGeo[message];
      if (inflectedKey == null) {
        console.log("no inflected key found for span=" + message);
        return;
      }

      let inflectedEntry = this.songsService.inflectedEntries[inflectedKey];

      dec.canonical_hindi = inflectedEntry.canonical_hindi;
      dec.inflected_hindi = inflectedEntry.inflected_hindi;
      dec.accidence = inflectedEntry.accidence;
      dec.accidence_labels = [];
      if (inflectedEntry.accidence != null) {
        inflectedEntry.accidence.forEach(a => dec.accidence_labels.push(this.globals.accidenceTypes[a]));
      }
      dec.inflected_part_of_speech_label = inflectedEntry.part_of_speech_label;
      dec.dictionary_entries_inflected = [];

      let dictionaryEntriesInflected = this.songsService.dictionaryEntriesInflected;
      inflectedEntry.canonical_keys.forEach(ck => {
        [].concat(...dictionaryEntriesInflected[ck]).forEach( de => dec.dictionary_entries_inflected.push(de));
      });


      let notInflectedKey = this.songsService.notInflectedGeo[message];
      let notInflectedEntry = this.songsService.notInflectedEntries[notInflectedKey];


      if (notInflectedEntry != null) {
        dec.not_inflected_hindi = notInflectedEntry.hindi;
        dec.not_inflected_part_of_speech_label = notInflectedEntry.part_of_speech_label;
        dec.dictionary_entries_not_inflected = [];

        let dictionaryEntriesNotInflected = this.songsService.dictionaryEntriesNotInflected;
        notInflectedEntry.canonical_keys.forEach(ck => {
          [].concat(...dictionaryEntriesInflected[ck]).forEach( de => dec.dictionary_entries_not_inflected.push(de));
        });
      }

      this.content = dec;

    });
  }

}
