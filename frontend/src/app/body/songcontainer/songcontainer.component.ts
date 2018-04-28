import {Component, ElementRef, Input, OnInit, Renderer2} from '@angular/core';
import { SongsService } from '../../services/songs.service';


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
      this.newMessage();
    })
  }

  newMessage() {
    this.renderer.setProperty(this.elementRef.nativeElement, 'innerHTML', this.message);
  }


}
