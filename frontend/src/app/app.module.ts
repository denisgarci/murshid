import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';


import { AppComponent } from './app.component';
import { HeaderComponent } from './header/header.component';
import { BodyComponent } from './body/body.component';
import { SongcontainerComponent } from './body/songcontainer/songcontainer.component';
import { DictionariescontainerComponent } from './body/dictionariescontainer/dictionariescontainer.component';
import { TranslationscontainerComponent } from './body/translationscontainer/translationscontainer.component';


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
    BrowserModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
