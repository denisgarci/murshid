import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import {HttpClientModule} from "@angular/common/http";


import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { BodyComponent } from './body/body.component';
import { SongcontainerComponent } from './body/songcontainer/songcontainer.component';
import { DictionariescontainerComponent } from './body/dictionariescontainer/dictionariescontainer.component';
import { TranslationscontainerComponent } from './body/translationscontainer/translationscontainer.component';

import { SongsService } from './services/songs.service';



@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    BodyComponent,
    SongcontainerComponent,
    DictionariescontainerComponent,
    TranslationscontainerComponent
  ],
  imports: [
    BrowserModule, HttpClientModule
  ],
  providers: [SongsService],
  bootstrap: [ AppComponent ]
})
export class AppModule { }
