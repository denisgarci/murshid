import {Component, ElementRef, OnInit, Renderer2} from '@angular/core';
import {SongsService} from "../../services/songs.service";
import {DictionariesContent} from "../../models/DictionariesContent";
import {SongModel} from "../../models/SongModel";

declare var jquery:any;
declare var $ :any;

@Component({
  selector: 'app-translationscontainer',
  templateUrl: './translationscontainer.component.html',
  styleUrls: ['./translationscontainer.component.css']
})
export class TranslationscontainerComponent implements OnInit {

  englishTranslationHtml: string;

  constructor(private songsService: SongsService, private elementRef: ElementRef, private renderer: Renderer2) { }

  ngOnInit() {
    this.songsService.songSelectionChangeObservable.subscribe(message => {
      this.updateEnglishTranslationContainerContent();
    });
  }

  updateEnglishTranslationContainerContent() {
    if (this.songsService.currentSong != null)
    {
      let divElement = this.elementRef.nativeElement.firstElementChild;

      //cloning is a practical way of removing all previous listeners from the divElement
      //(this doesn't have listeners for the moment)
      let newDiv = divElement.cloneNode(true);
      divElement.parentNode.replaceChild(newDiv, divElement);

      this.renderer.setProperty(newDiv, 'innerHTML', this.songsService.currentSong.english_translation_html);
    }
  }

  ngAfterViewChecked(){
    $(".songcontainer-main, .translations_container").on("scroll", function() {
      $(".songcontainer-main, .translations_container").scrollTop($(this).scrollTop());
      console.log("reacting to scroll")
    });

  }

}
