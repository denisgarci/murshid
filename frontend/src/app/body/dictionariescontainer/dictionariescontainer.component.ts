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

  @Input()
  content: DictionariesContent;

  constructor(private songsService: SongsService, private elementRef: ElementRef, private renderer: Renderer2) {}

  ngOnInit() {
    this.songsService.itemHoverChangeObservable.subscribe(message => {
      console.log("hovered over  " + message);

      if (message == null) return;

      var dec = new DictionariesContent();

      let masterKey = this.songsService.geo[message];
      if (masterKey == null){
        console.log("no master key found for span=" + message );
        return;
      }

      let masterEntry = this.songsService.masterEntries[masterKey];

      dec.canonical_word = masterEntry.canonical_word;
      dec.accidence = masterEntry.accidence;
      dec.dictionary_entries = [];

      let dictionaryEntries = this.songsService.dictionaryEntries;

      masterEntry.canonical_keys.forEach(ck =>{
         dec.dictionary_entries.push(dictionaryEntries[ck]);
      });

      this.content = dec;

    });
  }

  ngOnChanges(changes: SimpleChanges) {
    for (let propName in changes) {
      let change = changes[propName];

      let curVal  = JSON.stringify(change.currentValue);
      let prevVal = JSON.stringify(change.previousValue);

      console.log("previous value = " + prevVal + " current value = " + curVal);
    }
  }

}
