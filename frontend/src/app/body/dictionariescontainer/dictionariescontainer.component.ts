import {Component, OnInit} from '@angular/core';
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

  objectKeys = Object.keys;

  constructor(private songsService: SongsService, private globals: Globals) {
  }

  ngOnInit() {
    this.songsService.itemHoverChangeObservable.subscribe(message => {

      if (message == null) return;

      let dec = new DictionariesContent();

      let inflectedKey = this.songsService.inflectedGeo[message];
      if (inflectedKey == null) {
        console.log("no inflected key found for span=" + message);
        return;
      }

      let inflectedEntry = this.songsService.inflectedEntries[inflectedKey];

      if (inflectedEntry == null) {
        console.log("no inflected entry found for key " + inflectedKey);
        return;
      }

      let masterDictionaryKeyInflected = inflectedEntry.master_dictionary_key.hindi_word + "_" + inflectedEntry.master_dictionary_key.word_index;

      dec.canonical_hindi = inflectedEntry.master_dictionary_key.hindi_word;
      dec.inflected_hindi = inflectedEntry.inflected_hindi;
      dec.accidence = inflectedEntry.accidence;
      dec.accidence_labels = [];
      if (inflectedEntry.accidence != null) {
        inflectedEntry.accidence.forEach(a => dec.accidence_labels.push(this.globals.accidenceTypes[a]));
      }
      dec.inflected_part_of_speech_label = inflectedEntry.part_of_speech_label;


      dec.dictionary_entries_inflected = this.songsService.dictionaryEntriesInflected[masterDictionaryKeyInflected];


      let notInflectedKey = this.songsService.notInflectedGeo[message];
      let notInflectedEntry = this.songsService.notInflectedEntries[notInflectedKey];



      if (notInflectedEntry != null) {
        dec.not_inflected_hindi = notInflectedEntry.hindi;
        dec.not_inflected_part_of_speech_label = notInflectedEntry.part_of_speech_label;

        let masterDictionaryKeyNotInflected = notInflectedEntry.master_dictionary_key.hindi_word + "_" + notInflectedEntry.master_dictionary_key.word_index;

        dec.dictionary_entries_not_inflected = this.songsService.dictionaryEntriesNotInflected[masterDictionaryKeyNotInflected];
      }

      this.content = dec;

    });
  }

}
