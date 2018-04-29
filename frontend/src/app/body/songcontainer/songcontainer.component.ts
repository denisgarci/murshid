import {Component, ElementRef, Input, OnInit, Renderer2} from '@angular/core';
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

  message:string;

  ngOnInit() {
    this.songsService.currentMessage.subscribe(message => {
      this.message = message;
      this.updateContainerContent();
    })
  }

  updateContainerContent() {
    if (this.songsService.currentSong != null)
    {
      let divElement = this.elementRef.nativeElement.firstElementChild;
      this.renderer.setProperty(divElement, 'innerHTML', this.songsService.currentSong.html);
    }
  }

  init(){

    var allRelevant = document.getElementsByClassName("relevant");

    for (let index = 0; index < allRelevant.length; index++){
      allRelevant[index].addEventListener("mouseenter", function() {
        console.log("over " + allRelevant[index].id)
        //inElement(allRelevant[index].id);
      });
    }
  }


}
