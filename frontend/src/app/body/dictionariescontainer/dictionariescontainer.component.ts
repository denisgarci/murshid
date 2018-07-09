import {Component, ElementRef, OnInit, Renderer2} from '@angular/core';
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

  constructor(private songsService: SongsService,  private renderer: Renderer2, private elementRef: ElementRef, private globals: Globals) {
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

      dec.canonical_hindi = inflectedEntry.canonical_hindi;
      dec.inflected_hindi = inflectedEntry.inflected_hindi;
      dec.accidence = inflectedEntry.accidence;
      dec.accidence_labels = [];
      if (inflectedEntry.accidence != null) {
        inflectedEntry.accidence.forEach(a => dec.accidence_labels.push(this.globals.accidenceTypes[a]));
      }
      dec.inflected_part_of_speech_label = inflectedEntry.part_of_speech_label;


      if (masterDictionaryKeyInflected in this.songsService.dictionaryEntriesInflected) {
        dec.dictionary_entries_inflected = this.songsService.dictionaryEntriesInflected[masterDictionaryKeyInflected];
      }else{
        console.log("the masterDictionaryKey " + masterDictionaryKeyInflected + " does not exist in the map of dictionary entries");
        return;
      }


      let notInflectedKey = this.songsService.notInflectedGeo[message];
      let notInflectedEntry = this.songsService.notInflectedEntries[notInflectedKey];



      if (notInflectedEntry != null) {
        dec.not_inflected_hindi = notInflectedEntry.hindi;
        dec.not_inflected_part_of_speech_label = notInflectedEntry.part_of_speech_label;

        let masterDictionaryKeyNotInflected = notInflectedEntry.master_dictionary_key.hindi_word + "_" + notInflectedEntry.master_dictionary_key.word_index;

        dec.dictionary_entries_not_inflected = this.songsService.dictionaryEntriesNotInflected[masterDictionaryKeyNotInflected];
      }

      this.content = dec;
      this.renderer.addClass(this.elementRef.nativeElement.firstElementChild, "modal-open");

    });
    this.dragElement(document.getElementById("dictionariesContainer"), this.songsService);
  }

  closeModal(){
    this.renderer.removeClass(this.elementRef.nativeElement.firstElementChild, "modal-open");
  }


  togglePin() {
    this.songsService.pinned = !this.songsService.pinned;
    this.songsService.pinnedLeftStyle = document.getElementById("dictionariesContainer").style.left;
    this.songsService.pinnedTopStyle = document.getElementById("dictionariesContainer").style.top;

    let pinButton = document.getElementById("dictionaryPinButton");
    if (this.songsService.pinned){
      pinButton.classList.remove("pin-button-white");
      pinButton.classList.add("pin-button-red");
    }else{
      pinButton.classList.remove("pin-button-red");
      pinButton.classList.add("pin-button-white");
    }

  }

  dragElement(elmnt, songService) {
    var pos1 = 0, pos2 = 0, pos3 = 0, pos4 = 0;
    if (document.getElementById(elmnt.id + "header")) {
      /* if present, the header is where you move the DIV from:*/
      document.getElementById(elmnt.id + "header").onmousedown = () =>  dragMouseDown(songService, event);
    } else {
      /* otherwise, move the DIV from anywhere inside the DIV:*/
      elmnt.onmousedown =   () => dragMouseDown(songService, event);
    }

    function dragMouseDown(songService, e) {
      e = e || window.event;
      e.preventDefault();
      // get the mouse cursor position at startup:
      pos3 = e.clientX;
      pos4 = e.clientY;
      document.onmouseup = closeDragElement;
      // call a function whenever the cursor moves:
      document.onmousemove = () =>  elementDrag(songService, event);
    }

    function elementDrag(songService, e) {
      e = e || window.event;
      e.preventDefault();
      // calculate the new cursor position:
      pos1 = pos3 - e.clientX;
      pos2 = pos4 - e.clientY;
      pos3 = e.clientX;
      pos4 = e.clientY;
      // set the element's new position:
      elmnt.style.top = (elmnt.offsetTop - pos2) + "px";
      elmnt.style.left = (elmnt.offsetLeft - pos1) + "px";

      songService.pinnedTopStyle = elmnt.style.top;
      songService.pinnedLeftStyle = elmnt.style.left;

    }

    function closeDragElement() {
      /* stop moving when mouse button is released:*/
      document.onmouseup = null;
      document.onmousemove = null;
    }
  }

}
