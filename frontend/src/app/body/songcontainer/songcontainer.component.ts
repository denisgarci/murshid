import {Component, ElementRef, HostListener, Input, OnInit, Renderer2} from '@angular/core';
import { SongsService } from '../../services/songs.service';
import { SongModel } from '../../models/SongModel';



@Component({
  selector: 'app-songcontainer',
  templateUrl: './songcontainer.component.html',
  styleUrls: ['./songcontainer.component.css']
})
export class SongcontainerComponent implements OnInit {


  constructor(private songsService: SongsService, private elementRef: ElementRef, private renderer: Renderer2) {

  }

  ngOnInit() {
    this.songsService.songSelectionChangeObservable.subscribe(message => {
      this.updateContainerContent();
    });

  }

  updateContainerContent() {
    if (this.songsService.currentSong != null)
    {
      let divElement = this.elementRef.nativeElement.firstElementChild;

      //cloning is a practical way of removing all previous listeners from the divElement
      let newDiv = divElement.cloneNode(true);
      divElement.parentNode.replaceChild(newDiv, divElement);

      this.renderer.setProperty(newDiv, 'innerHTML', this.songsService.currentSong.html);
      this.init();
    }
  }

  init(){

    var allRelevant = document.getElementsByClassName("relevant");

    for (let index = 0; index < allRelevant.length; index++){
      allRelevant[index].addEventListener("mouseenter", () =>  {
        let spanSelected = allRelevant[index];
        this.songsService.itemHoverChange.next(spanSelected.id);
        spanSelected.classList.add("selectedRed");


        var topPos = spanSelected.getBoundingClientRect().top + window.scrollY;
        var leftPos = spanSelected.getBoundingClientRect().left + window.scrollX;

        let bcr = spanSelected.getBoundingClientRect();
        document.getElementById("dictionariesContainer").style.top = topPos + 20 + 'px';
        document.getElementById("dictionariesContainer").style.left = leftPos + 20 + 'px';
      });

      allRelevant[index].addEventListener("mouseleave", () =>  {
        let spanSelected = allRelevant[index];
        spanSelected.classList.remove("selectedRed");

      });

    }
  }

}
