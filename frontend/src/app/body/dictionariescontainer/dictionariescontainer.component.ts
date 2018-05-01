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
      this.content = null;
      var dec = new DictionariesContent();
      dec.canonical_word = "CanWordna";
      dec.accidence = Array.of('Masculine', 'Singular');

      let de1 = new DictionaryEntry();
      de1.meaning ="To behold, to see";
      de1.dictionary_name = 'Platts';

      let de2 = new DictionaryEntry();
      de2.meaning ="To see, to watch, to understand";
      de2.dictionary_name = 'Murshid';

      dec.dictionary_entries = Array.of(de1, de2);

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
