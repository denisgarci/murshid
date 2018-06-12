import {DictionaryEntry} from "./DictionaryEntry";
import {IDictionaryEntriesMap} from "./IDictionaryEntriesMap";

export class DictionariesContent {
  accidence: string[];
  accidence_labels: string[] = [];
  canonical_hindi: string;
  inflected_hindi: string;
  not_inflected_hindi: string;
  not_inflected_part_of_speech_label: string;
  inflected_part_of_speech_label: string;
  dictionary_entries_inflected: IDictionaryEntriesMap[];
  dictionary_entries_not_inflected: IDictionaryEntriesMap[];

}
