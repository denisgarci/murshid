import {Component, ElementRef, OnInit, Renderer2} from '@angular/core';
import {SongsService} from "../../services/songs.service";

@Component({
  selector: 'app-dictionariescontainer',
  templateUrl: './dictionariescontainer.component.html',
  styleUrls: ['./dictionariescontainer.component.css']
})
export class DictionariescontainerComponent implements OnInit {

  constructor(private songsService: SongsService, private elementRef: ElementRef, private renderer: Renderer2) {

  }

  ngOnInit() {
    this.songsService.itemHoverChangeObservable.subscribe(message => {
      console.log("hovered over  " + message);
      // this.message = message;
      // this.updateContainerContent();
    });
  }

}
