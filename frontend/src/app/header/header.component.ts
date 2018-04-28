import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {SongsService} from "../services/songs.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {

  @Output() songNameUpdated = new EventEmitter();

  constructor(private songsService: SongsService) {

  }

  onSearchClicked(latinSongName: string){
    this.songsService.changeMessage(latinSongName);
  }

  ngOnInit() {
  }

}
