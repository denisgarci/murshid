import {Component, OnInit} from '@angular/core';
import {SongsService} from "../services/songs.service";

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {



  customers = ["Alvida", "Tere Mast Mast Do Nain"];

  constructor(private songsService: SongsService) {

  }

  onSearchClicked(latinSongName: string){
    this.songsService.changeSelectedSong(latinSongName);
  }

  ngOnInit() {




  }

}
export class Car
{
  colour:Colour;
}
export class Colour
{
  constructor(id:number, name:string) {
    this.id=id;
    this.name=name;
  }

  id:number;
  name:string;
}
