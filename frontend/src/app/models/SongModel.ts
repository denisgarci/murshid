import {s} from "@angular/core/src/render3";

export class SongModel{
  html: string;
  title_hindi: string;
  title_latin: string;
  author: string;
  media: string[];
  song: string;
  word_list_master: object[];
  word_list_not_inflected: object[];
  word_list: object;
  inflected_entries: string;
  not_inflected_entries: string;
  dictionary_entries_inflected: string;
  dictionary_entries_not_inflected: string;
  english_translation: string;
  english_translation_html: string;
}
